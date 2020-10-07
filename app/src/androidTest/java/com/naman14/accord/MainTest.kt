package com.naman14.accord

import androidx.test.filters.SmallTest
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.ActivityTestRule
import androidx.test.runner.AndroidJUnit4
import androidx.test.uiautomator.UiDevice
import com.naman14.accord.library.annotation.PerfTest
import com.naman14.accord.library.annotation.AccordTest
import com.naman14.accord.library.AccordRule
import com.naman14.accord.library.AccordConfig
import com.naman14.accord.library.tracers.JankyFramesTracer
import org.junit.Rule

import org.junit.Test
import org.junit.runner.RunWith

@SmallTest
@AccordTest
@RunWith(AndroidJUnit4::class)
class MainTest {

    @get: Rule
    val accordRule = AccordRule(BuildConfig.APPLICATION_ID, AccordConfig(
        max_jank_percent = 3.0
    ), tracers = arrayListOf(
        JankyFramesTracer(BuildConfig.APPLICATION_ID)
    ))

    @get: Rule
    val activityRule = ActivityTestRule(MainActivity::class.java)

    @PerfTest
    @Test
    fun scrollTest() {
        UiDevice.getInstance(InstrumentationRegistry.getInstrumentation()).apply {
            for (i in 0 until 10) {
                swipe(displayWidth / 2, displayHeight / 2, displayWidth / 2, displayHeight / 2 - 2000, 30)
            }
            for (i in 0 until 10) {
                swipe(displayWidth / 2, displayHeight / 2, displayWidth / 2, displayHeight / 2 + 2000, 30)
            }
        }
    }
}