package com.naman14.accord.library

/**
 * Class to store data abou the verdict from the analysers.
 * @param passed if the test was marked as passed by the analyser
 * @param message message provided by the analyser
 */
data class TraceVerdict(val passed: Boolean = false,
                        val message: String = "")