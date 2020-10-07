package com.naman14.accord.plugin

/**
 * Configuration for plugin
 */
open class AccordExtension {

    // tasks that should be run before the tests
    // this would include assembling and installing the debug app and test app
    var preTestTasks: List<String>? = null

    // tasks that should be run after the tests
    var postTestTasks: List<String>? = null

    // instrumentation command for running the tests, specified in array format
    // ['adb', 'shell', 'am', 'instrument' ... ]
    var instrumentationCommand: List<String>? = null

    // application id for the test app
    var testApplicationId: String? = null
}