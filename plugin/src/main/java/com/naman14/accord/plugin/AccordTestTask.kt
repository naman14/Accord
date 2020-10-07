package com.naman14.accord.plugin

import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.Task

/**
 * Base task that checks for connected devices and creates device specific tasks
 * Also manages pre test tasks and post test tasks to run
 */
open class AccordTestTask : DefaultTask() {

    companion object {

        val DEFAULT_PRE_TEST_TASKS = listOf<String>(
            "assembleDebug",
            "assembleDebugAndroidTest",
            "installDebug",
            "installDebugAndroidTest"
        )

        val DEFAULT_POST_TEST_TASKS = listOf<String>(
            "uninstallAll"
        )
    }

    init {
        group = "verification"
        description = "Setup accord performance tests"
    }

    fun createAccordTestTasks() {
        val createdTasks = arrayListOf<Task>()
        val connectedDevices = Utils.getConnectedDevices(project)

        if (connectedDevices.isEmpty()) {
            println("No connected devices found to run performance tests")
        }

        val extensions = project.extensions.findByName("accord") as AccordExtension

        val preTestTasks:List<String> = extensions.preTestTasks ?: DEFAULT_PRE_TEST_TASKS
        val postTestTasks: List<String> = extensions.postTestTasks ?: DEFAULT_POST_TEST_TASKS

        //create a wrapper task for all device specific tasks
        val accordTests = project.tasks.create("runAccordTests", DefaultTask::class.java)

        connectedDevices.forEach { deviceId ->
            val devicePerfTask =
                project.tasks.create("runAccordTest_" + deviceId, AccordPerformanceTask::class.java)
            devicePerfTask.deviceId = deviceId
            accordTests.dependsOn(devicePerfTask)
            devicePerfTask.dependsOn(preTestTasks)

            createdTasks.add(devicePerfTask)
        }

        createdTasks.forEach { task ->
            postTestTasks.forEach {
                project.tasks.findByName(it)!!.mustRunAfter(task)
            }
        }

        dependsOn(accordTests)
    }
}