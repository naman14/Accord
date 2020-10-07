package com.naman14.accord.library

const val DEFAULT_MAX_JANK_PERCENT = 10.0
const val DEFAULT_50TH_PERCENTILE_FRAME_TIME = 16
const val DEFAULT_90TH_PERCENTILE_FRAME_TIME = 32
const val DEFAULT_95TH_PERCENTILE_FRAME_TIME = 48
const val DEFAULT_99TH_PERCENTILE_FRAME_TIME = 64
const val DEFAULT_MAX_MEMORY_USAGE = 300
const val DEFAULT_MAX_APP_CONTEXTS = 10

/**
 * Class to set different configurations against which the analysers will benchmark the
 * performance data.
 */
data class AccordConfig(
    var max_jank_percent: Double = DEFAULT_MAX_JANK_PERCENT,
    var frame_time_50th_percentile: Int = DEFAULT_50TH_PERCENTILE_FRAME_TIME,
    var frame_time_90th_percentile: Int = DEFAULT_90TH_PERCENTILE_FRAME_TIME,
    var frame_time_95th_percentile: Int = DEFAULT_95TH_PERCENTILE_FRAME_TIME,
    var frame_time_99th_percentile: Int = DEFAULT_99TH_PERCENTILE_FRAME_TIME,
    var max_memory_usage: Int = DEFAULT_MAX_MEMORY_USAGE,
    var should_fail_possible_memory_leaks: Boolean = false,
    var max_app_contexts: Int = DEFAULT_MAX_APP_CONTEXTS
)
