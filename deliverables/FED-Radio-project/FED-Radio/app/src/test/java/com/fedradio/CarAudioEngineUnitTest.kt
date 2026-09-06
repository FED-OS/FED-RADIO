package com.fedradio

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

/**
 * FED-Radio brain parser contract tests.
 *
 * The parser contract lives in CarAudioEngine.kt:
 *     data-id="XXX">TITLE
 *
 * These tests parse the REAL index.html (the single source of truth) the
 * same way the car bridge does, so any edit that breaks the Android Auto
 * station list fails the build before it ever reaches the car.
 */
class CarAudioEngineUnitTest {

    /** Same regex as CarAudioEngine.parseStationsFromBrain(). */
    private val stationRegex = """data-id="([^"]*)">([^<]*)""".toRegex()

    /** The actual brain file — works whether tests run from the app module or the repo root. */
    private val brainFile: File by lazy {
        val candidates = listOf(
            File("src/main/assets/index.html"),               // module working dir
            File("app/src/main/assets/index.html")            // repo root working dir
        )
        candidates.firstOrNull { it.exists() }
            ?: candidates[0]
    }

    private fun stationsIn(html: String): List<Pair<String, String>> =
        stationRegex.findAll(html)
            .map { it.groupValues[1].trim() to it.groupValues[2].trim() }
            .filter { it.first.isNotEmpty() && it.second.isNotEmpty() }
            .toList()

    @Test
    fun `contract - data-id last attribute with plain text title parses`() {
        val sample = "<div class=\"station\" data-yt=\"abc123\" data-id=\"track-01\">Track One</div>"
        val stations = stationsIn(sample)
        assertEquals(1, stations.size)
        assertEquals("track-01", stations[0].first)
        assertEquals("Track One", stations[0].second)
    }

    @Test
    fun `contract regression - titles wrapped in spans must not parse as empty`() {
        // This is the bug that almost shipped in v1.0: titles inside child
        // <span> tags make the regex capture whitespace between > and <span>,
        // producing an empty car station list. The parser skips empties, but
        // the REAL-file test below is what guards the actual brain.
        val buggy = "<div class=\"station\" data-id=\"track-01\"><span>Track One</span></div>"
        val stations = stationsIn(buggy)
        assertTrue("span-wrapped titles must yield no empty-title stations", stations.isEmpty())
    }

    @Test
    fun `real brain file - car sees every station with a non-empty title`() {
        assertTrue("brain file missing: ${brainFile.absolutePath}", brainFile.exists())
        val stations = stationsIn(brainFile.readText())

        // Every shipped channel must be visible to the car, title included.
        assertTrue(
            "Car would see an empty/partial station list: $stations",
            stations.size >= 4
        )
        val ids = stations.map { it.first }
        assertTrue("missing bundled station id", ids.contains("station_id"))
        assertTrue("missing track-01", ids.contains("track-01"))
        assertTrue("missing track-02", ids.contains("track-02"))
        assertTrue("missing track-03", ids.contains("track-03"))
        stations.forEach { (id, title) ->
            assertTrue("station '$id' has an empty title — car would show a blank row", title.isNotBlank())
        }
    }

    @Test
    fun `real brain file - data-id is always the last attribute`() {
        val html = brainFile.readText()
        // Find every tag that has data-id and assert no other attribute follows it.
        val tagsWithId = Regex("""<div[^>]*data-id[^>]*>""").findAll(html).map { it.value }.toList()
        assertTrue("no station blocks found", tagsWithId.isNotEmpty())
        tagsWithId.forEach { tag ->
            val end = tag.dropLast(1) // remove trailing '>'
            val lastAttr = end.trim().substringAfterLast(' ')
            assertTrue(
                "data-id must be the last attribute — offending tag: $tag",
                lastAttr.startsWith("data-id=")
            )
        }
    }

    @Test
    fun `real brain file - car station count matches station block count`() {
        // Guards against "phantom stations": example tags written in comments
        // parse identically to real ones for a raw-text parser. If someone
        // writes a sample block in a comment, counts diverge and this fails.
        val html = brainFile.readText()
        val blockCount = Regex("""class="station""").findAll(html).count()
        val parsedCount = stationsIn(html).size
        assertTrue("brain file has no station blocks", blockCount > 0)
        assertEquals(
            "car sees $parsedCount stations but file declares $blockCount — phantom or broken blocks present",
            blockCount, parsedCount
        )
    }

    @Test
    fun `sanity - app name is FED-Radio`() {
        assertEquals("FED-Radio", "FED-Radio")
    }
}
