package com.naman14.accord.library

import java.lang.Exception

/**
 * Wrapper class for us to be able to identify crashes during test run
 * starting instrumentation using adb shell am instrument always results with a zero exit code
 * We need to fail the gradle task according to if the test run was successful or not.
 * In the plugin, we check if there are any AccordException and then fail the gradle task.
 */
class AccordException(message: String): Exception(message)