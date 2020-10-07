package com.naman14.accord.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * Gradle plugin for running performance tasks on multiple devices and
 * easy reporting of the success or failure of the tasks
 *
 * use with ./gradlew accordTest
 */
class AccordPlugin: Plugin<Project> {

    override fun apply(project: Project) {
        project.extensions.create("accord", AccordExtension::class.java)
        project.dependencies.add("androidTestImplementation", "com.naman14.accord:library:0.1")
        project.afterEvaluate {
            val accordTask = project.tasks.create("accordTest", AccordTestTask::class.java)
            accordTask.createAccordTestTasks()
        }
    }
}