package com.fedradio

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.ServiceInfo
import android.content.res.AssetFileDescriptor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Bundle
import android.os.PowerManager
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.media.MediaBrowserServiceCompat
import java.util.Locale

/**
 * FED-Radio — CarAudioEngine
 *
 * The Android Auto car bridge. This service:
 *   1. Reads the LOCAL assets/index.html "brain" as raw text
 *   2. Parses out station data-ids + titles with a native regex (no external parsers)
 *   3. Publishes them to the car dashboard as a standard playable media list
 *   4. Plays bundled, offline audio when a track is tapped in the car
 *
 * AUDIO EXTENSION POINT (in-house, zero third-party):
 *   For each station in index.html with data-id="X", drop an audio file at
 *   app/src/main/assets/audio/X.wav  (wav/mp3/m4a/ogg all work).
 *   If no per-station file exists, the shared station-ID jingle plays instead,
 *   so the dashboard always has something real to play out of the box.
 *
 * The car UI itself (play/pause buttons, progress bar, steering-wheel
 * next/previous controls, speaker routing) is rendered entirely by
 * Android Auto — this class only supplies data and audio.
 */
class CarAudioEngine : MediaBrowserServiceCompat() {

    companion object {
        private const val TAG = "FED-Radio"
        private const val ROOT_ID = "root"
        private const val CHANNEL_ID = "fedradio_playback"
        private const val NOTIFICATION_ID = 41
        private const val AUDIO_FALLBACK = "audio/station_id.wav"
        private val AUDIO_EXTENSIONS = listOf("wav", "mp3", "m4a", "ogg")
    }

    /** A station track parsed from the local HTML brain. */
    data class Station(val id: String, val title: String)

    private val stations = mutableListOf<Station>()
    private var currentIndex = -1

    private lateinit var mediaSession: MediaSessionCompat
    private var mediaPlayer: MediaPlayer? = null
    private var resumeOnFocusGain = false

    private lateinit var audioManager: AudioManager
    private var focusRequest: AudioFocusRequest? = null
    private var stationArt: Bitmap? = null

    // =====================================================================
    // LIFECYCLE
    // =====================================================================

    override fun onCreate() {
        super.onCreate()
        audioManager = getSystemService(AUDIO_SERVICE) as AudioManager
        stationArt = loadStationArt()
        parseStationsFromBrain()

        mediaSession = MediaSessionCompat(this, "FEDRadioSession").apply {
            setCallback(SessionCallback())
            setPlaybackState(buildPlaybackState(PlaybackStateCompat.STATE_NONE))
        }
        // Hand the session token to the system — this is the exact moment
        // Android Auto gains the right to draw FED-Radio on the dashboard.
        setSessionToken(mediaSession.sessionToken)
        Log.i(TAG, "Car bridge online with ${stations.size} stations")
    }

    override fun onDestroy() {
        abandonAudioFocus()
        releasePlayer()
        mediaSession.release()
        super.onDestroy()
    }

    // =====================================================================
    // STEP 1 — PARSE THE LOCAL HTML BRAIN (no external calls)
    // =====================================================================

    private fun parseStationsFromBrain() {
        stations.clear()
        try {
            val html = assets.open("index.html").bufferedReader().use { it.readText() }
            // Contract: <div class="station" data-id="ID">TITLE</div>
            val regex = """data-id="([^"]*)">([^<]*)""".toRegex()
            regex.findAll(html).forEach { match ->
                val id = match.groupValues[1].trim()
                val title = match.groupValues[2].trim()
                if (id.isNotEmpty() && title.isNotEmpty()) {
                    stations.add(Station(id, title))
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to read index.html from assets", e)
        }
    }

    // =====================================================================
    // STEP 2 — PUBLISH THE STATION LIST TO THE CAR DASHBOARD
    // =====================================================================

    override fun onGetRoot(
        clientPackageName: String,
        clientUid: Int,
        rootHints: Bundle?
    ): BrowserRoot {
        // Private, sideloaded build: grant browse access to any system client
        // (Android Auto / AAOS / Bluetooth) without a package allow-list.
        return BrowserRoot(ROOT_ID, null)
    }

    override fun onLoadChildren(
        parentId: String,
        result: Result<MutableList<MediaBrowserCompat.MediaItem>>
    ) {
        val items = mutableListOf<MediaBrowserCompat.MediaItem>()
        try {
            stations.forEach { station ->
                val description = MediaDescriptionCompat.Builder()
                    .setMediaId(station.id)
                    .setTitle(station.title)
                    .setSubtitle(getString(R.string.app_name))
                    .setIconBitmap(stationArt)
                    .build()
                items.add(
                    MediaBrowserCompat.MediaItem(
                        description,
                        MediaBrowserCompat.MediaItem.FLAG_PLAYABLE
                    )
                )
            }
        } finally {
            // Always deliver a result — never leave the car UI hanging.
            result.sendResult(items)
        }
    }

    // =====================================================================
    // STEP 3 — TRANSPORT CONTROLS (car screen + steering wheel buttons)
    // =====================================================================

    private inner class SessionCallback : MediaSessionCompat.Callback() {
        override fun onPlay() {
            if (stations.isEmpty()) return
            if (currentIndex == -1) currentIndex = 0
            playCurrent()
        }

        override fun onPlayFromMediaId(mediaId: String?, extras: Bundle?) {
            val index = stations.indexOfFirst { it.id == mediaId }
            if (index >= 0) {
                currentIndex = index
                playCurrent()
            }
        }

        override fun onPause() = pausePlayback()

        override fun onSkipToNext() = skip(1)

        override fun onSkipToPrevious() = skip(-1)

        override fun onStop() = stopPlayback()
    }

    /** Radio-style auto-advance: when a jingle ends, roll to the next station. */
    private fun skip(delta: Int) {
        if (stations.isEmpty()) return
        currentIndex = ((currentIndex.takeIf { it >= 0 } ?: 0) + delta)
            .mod(stations.size)
        playCurrent()
    }

    // =====================================================================
    // STEP 4 — PLAYBACK (bundled, offline audio via MediaPlayer)
    // =====================================================================

    private fun playCurrent() {
        val station = stations.getOrNull(currentIndex) ?: return

        requestAudioFocus()
        releasePlayer()

        val descriptor = openStationAudio(station.id)
        if (descriptor != null) {
            mediaPlayer = MediaPlayer().apply {
                setWakeMode(applicationContext, PowerManager.PARTIAL_WAKE_LOCK)
                setAudioAttributes(playbackAttributes())
                try {
                    setDataSource(
                        descriptor.fileDescriptor,
                        descriptor.startOffset,
                        descriptor.length
                    )
                } finally {
                    descriptor.close()
                }
                setOnCompletionListener {
                    // Continuous-station behaviour: keep the show rolling.
                    skip(1)
                }
                setOnErrorListener { _, what, extra ->
                    Log.e(TAG, "MediaPlayer error what=$what extra=$extra")
                    true
                }
                prepare()
                start()
            }
        } else {
            Log.w(TAG, "No audio asset found — publishing metadata only for ${station.id}")
        }

        mediaSession.isActive = true
        mediaSession.setMetadata(buildMetadata(station))
        updatePlaybackState(PlaybackStateCompat.STATE_PLAYING)
        startInForeground(station)
    }

    private fun pausePlayback() {
        mediaPlayer?.takeIf { it.isPlaying }?.pause()
        updatePlaybackState(PlaybackStateCompat.STATE_PAUSED)
    }

    private fun stopPlayback() {
        releasePlayer()
        mediaSession.isActive = false
        updatePlaybackState(PlaybackStateCompat.STATE_STOPPED)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            stopForeground(android.app.Service.STOP_FOREGROUND_REMOVE)
        } else {
            @Suppress("DEPRECATION")
            stopForeground(true)
        }
        stopSelf()
    }

    /** Per-station audio: assets/audio/<id>.<ext>, else the shared jingle. */
    private fun openStationAudio(stationId: String): AssetFileDescriptor? {
        for (extension in AUDIO_EXTENSIONS) {
            try {
                return assets.openFd("audio/$stationId.$extension")
            } catch (_: java.io.FileNotFoundException) {
                // try the next extension
            }
        }
        return try {
            assets.openFd(AUDIO_FALLBACK)
        } catch (e: Exception) {
            Log.e(TAG, "Fallback jingle missing — did assets/audio/ get packaged?", e)
            null
        }
    }

    private fun releasePlayer() {
        mediaPlayer?.let { player ->
            try {
                if (player.isPlaying) player.stop()
            } catch (_: IllegalStateException) {
                // already stopped — nothing to do
            }
            player.release()
        }
        mediaPlayer = null
    }

    // =====================================================================
    // MEDIA SESSION METADATA + STATE (what the car screen renders)
    // =====================================================================

    private fun buildMetadata(station: Station): MediaMetadataCompat =
        MediaMetadataCompat.Builder()
            .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, station.id)
            .putString(MediaMetadataCompat.METADATA_KEY_TITLE, station.title)
            .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, getString(R.string.app_name))
            .putString(MediaMetadataCompat.METADATA_KEY_ALBUM, getString(R.string.station_tagline))
            .putLong(
                MediaMetadataCompat.METADATA_KEY_DURATION,
                (mediaPlayer?.duration ?: 0).toLong()
            )
            .putBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART, stationArt)
            .build()

    private fun buildPlaybackState(state: Int): PlaybackStateCompat =
        PlaybackStateCompat.Builder()
            .setActions(
                PlaybackStateCompat.ACTION_PLAY
                    or PlaybackStateCompat.ACTION_PAUSE
                    or PlaybackStateCompat.ACTION_PLAY_PAUSE
                    or PlaybackStateCompat.ACTION_SKIP_TO_NEXT
                    or PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS
                    or PlaybackStateCompat.ACTION_STOP
                    or PlaybackStateCompat.ACTION_SEEK_TO
            )
            .setState(
                state,
                (mediaPlayer?.currentPosition ?: 0).toLong(),
                1.0f
            )
            .build()

    private fun updatePlaybackState(state: Int) {
        mediaSession.setPlaybackState(buildPlaybackState(state))
    }

    // =====================================================================
    // AUDIO FOCUS (required so the car routes FED-Radio correctly)
    // =====================================================================

    private val focusListener = AudioManager.OnAudioFocusChangeListener { change ->
        when (change) {
            AudioManager.AUDIOFOCUS_LOSS -> {
                resumeOnFocusGain = false
                pausePlayback()
            }
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT -> {
                resumeOnFocusGain = true
                pausePlayback()
            }
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK -> {
                mediaPlayer?.setVolume(0.2f, 0.2f)
            }
            AudioManager.AUDIOFOCUS_GAIN -> {
                if (resumeOnFocusGain) {
                    resumeOnFocusGain = false
                    playCurrent()
                } else {
                    mediaPlayer?.setVolume(1.0f, 1.0f)
                }
            }
        }
    }

    private fun playbackAttributes(): AudioAttributes =
        AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_MEDIA)
            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
            .build()

    private fun requestAudioFocus() {
        if (focusRequest == null) {
            focusRequest = AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
                .setAudioAttributes(playbackAttributes())
                .setOnAudioFocusChangeListener(focusListener)
                .build()
        }
        audioManager.requestAudioFocus(focusRequest!!)
    }

    private fun abandonAudioFocus() {
        focusRequest?.let { audioManager.abandonAudioFocusRequest(it) }
    }

    // =====================================================================
    // FOREGROUND NOTIFICATION (Android 10+ requirement while playing)
    // =====================================================================

    private fun startInForeground(station: Station) {
        val manager = getSystemService(NotificationManager::class.java)
        if (manager.getNotificationChannel(CHANNEL_ID) == null) {
            manager.createNotificationChannel(
                NotificationChannel(
                    CHANNEL_ID,
                    "FED-Radio playback",
                    NotificationManager.IMPORTANCE_LOW
                )
            )
        }

        val contentIntent = PendingIntent.getActivity(
            this,
            0,
            Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification: Notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(station.title)
            .setContentText(getString(R.string.app_name) + " · " + getString(R.string.station_tagline))
            .setSmallIcon(R.drawable.ic_stat_fed)
            .setContentIntent(contentIntent)
            .setOngoing(true)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .build()

        startForeground(NOTIFICATION_ID, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK)
    }

    // =====================================================================
    // ART
    // =====================================================================

    private fun loadStationArt(): Bitmap? = try {
        BitmapFactory.decodeResource(resources, R.drawable.ic_station_art)
    } catch (e: Exception) {
        Log.w(TAG, "Station art unavailable", e)
        null
    }
}
