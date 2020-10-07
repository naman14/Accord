package com.naman14.accord

import androidx.test.filters.SmallTest
import androidx.test.rule.ActivityTestRule
import androidx.test.runner.AndroidJUnit4
import com.naman14.accord.library.annotation.PerfTest
import com.naman14.accord.library.annotation.AccordTest
import com.naman14.accord.library.annotation.TimeTest
import com.naman14.accord.library.AccordRule
import com.naman14.accord.library.AccordConfig
import com.naman14.accord.library.tracers.MemoryTracer
import org.junit.Rule
import java.lang.Thread

import org.junit.Test
import org.junit.runner.RunWith

@SmallTest
@AccordTest
@RunWith(AndroidJUnit4::class)
class MainTest {

    @get: Rule
    val accordRule = AccordRule(BuildConfig.APPLICATION_ID, AccordConfig(
        max_jank_percent = 100.0,
        frame_time_50th_percentile = 5000,
        frame_time_90th_percentile = 5000,
        frame_time_95th_percentile = 5000,
        frame_time_99th_percentile = 5000,
        max_memory_usage = 100000
    ))

    @get: Rule
    val activityRule = ActivityTestRule(MainActivity::class.java)

    @Test
    @PerfTest
    fun mainTest() {
        Thread.sleep(2000)
    }

    @Test
    @TimeTest(
        threshold = 3000
    )
    fun mainTest2() {
        Thread.sleep(2000)
    }
}