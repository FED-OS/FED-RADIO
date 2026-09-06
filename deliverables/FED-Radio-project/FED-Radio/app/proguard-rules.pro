# FED-Radio ProGuard/R8 rules
# Minification is disabled for the baseline build, but these keeps protect
# the Android Auto bridge if you enable isMinifyEnabled later.

# The car bridge is discovered by the system via the manifest intent filter
-keep class com.fedradio.CarAudioEngine { *; }

# MediaSession / MediaBrowserService internals are referenced reflectively
-keep class android.support.v4.media.** { *; }
-keep class androidx.media.** { *; }
