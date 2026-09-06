package com.fedradio

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.Assert.*

/**
 * Placeholder instrumented tests — verify the app package identity on device.
 */
@RunWith(AndroidJUnit4::class)
class MainActivityInstrumentedTest {

    @Test
    fun `placeholder - app package is com dot fedradio`() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("com.fedradio", context.packageName)
    }
}
