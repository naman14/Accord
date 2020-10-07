## Accord

Automated performance monitoring for Android

Accord is a library for automating performance measurements that easily integrates with existing CI infrastructure.

## Install

## Gradle plugin

Plugin integrates well with using gradle from the command line and allows us to run performance tests on multiple devices.

```
buildscript {
    repositories {
       mavenCentral()
    }
    dependencies {
        classpath("com.naman14.accord:plugin:0.1")
    }
}
```
```
apply plugin 'com.naman14.accord'
```

Plugin will only add the library dependency for the androidTest configuration and will not affect the release builds.

 Use `./gradlew accordTest` to run the performance tests

Alternatively, you can also use the library directly without the plugin.

```
androidTestImplementation 'com.naman14.accord:library:0.1'
```

## Usage

- Write espresso tests 
- Annotate test classes with `@AccordTest`
- Annotate individual tests with `@PerfTest`
- Create `AccordRule` with a set of tracers and the configuration to benchmark against.

```kotlin
@LargeTest
@AccordTest
@RunWith(AndroidJUnit4::class)
class MainTest {

    @get:Rule
    var mAccordRule = AccordRule(
            packageName = BuildConfig.APPLICATION_ID,
            config = AccordConfig(
                    max_memory_usage = 300 * 1024,
                    max_jank_percent = 20.0
            ),
            tracers = arrayListOf(
                    MemoryTracer(BuildConfig.APPLICATION_ID),
                    JankyFramesTracer(BuildConfig.APPLICATION_ID),
                    MemoryLeakTracer(BuildConfig.APPLICATION_ID)
            )
    )

    /**
     * swipe up and down on a page
     * performance will be measured and analysed by Accord (frame drops, frame times, memory...)
     */
    @PerfTest
    @Test
    fun mainTest() {
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
```

## Test results

```
Execution failed for task ':app:runAccordTest_92UAY04JJV'.
> com.naman14.accord.library.AccordException: Performance tests failed:
[Total memory usage exceeded threshold (threshold 102400 kb, found 420446 kb), Janky frames percent exceeded threshold (threshold 15.0 %, found 20.12 %)]
```

```
Janky frames analysis passed (threshold 15.0 %, found 11.0 %)
50th percentile frame time analysis passed (threshold 16 ms, found 12 ms)
90th percentile frame time analysis passed (threshold 32 ms, found 26 ms)
95th percentile frame time analysis passed (threshold 48 ms, found 30 ms)
99th percentile frame time analysis passed (threshold 64 ms, found 47 ms)
Memory usage analysis passed (threshold 100000 kb, found 57144 kb)
```

## Overview

`@PerfTest`: The tracers registered in the AccordRule will be run for these tests and will analyse the traces to mark the tests successful or not. Examples: Tests that automate the whole critical flow of the app. Tests that automate individual screens (scrolling a recyclerview, interacting with tabs etc)

`AccordConfig`: Configuration class for defining the thresholds and limits for your app. 

`Tracers`: Methods of performance data collection. Multiple tracers are used in a test. Currently the following tracers are available 
- `JankyFramesTracer` and `FrameTimesTracer`: Uses adb shell dumpsys gfxinfo to dump frame info and compare them with the configuration set.
- `MemoryTracer` and `MemoryLeakTracer`: Uses adb shell dumpsys meminfo to dump memory info. Memory leaks are analysed in a very raw manner and might not always be indicative of actual leaks. There is an option in AccordConfig to mark memory leak failures as non fatal.
- You can also write a custom tracer by implementing the `Tracer` interface and returning the `TraceVerdict`

## Instrumentation configuration

Accord allows you to set your own instrumentation command and pre and post test tasks.

### build.gradle
```groovy
apply plugin: 'com.naman14.accord'

accord {
    //required to be either set in defaultConfig or here to run instrumentation
    testApplicationId 'com.naman14.accord.sample.test'

    //tasks that should run before running the instrymentation
    preTestTasks = ['assembleDevDebug', 'assembleDevDebugAndroidTest', 'installDevDebug', 'installDevDebugAndroidTest']

    //tasks that should run after the instrumentation
    postTestTasks = ['uninstallAll']

    instrumentationCommand = ['adb', 'shell', 'am', 'instrument', '-w', '-e', ...]
}
```


Or if using Kotlin Gradle DSL
### build.gradle.kts
```groovy
import com.naman14.accord.plugin.AccordExtension

plugins.apply("com.naman14.accord")

configure<AccordExtension> {
    testApplicationId = "com.naman14.accord.sample.test"
    preTestTasks = listOf("assembleDevDebug", "assembleDevDebugAndroidTest", "installDevDebug", "installDevDebugAndroidTest")
    postTestTasks = listOf("uninstallAll")
    instrumentationCommand = listOf("adb", "shell", "am", "instrument", "-w", "-e", ...)
}
```

## Sample

The sample app has a laggy recyclerview and a corresponding performance test. To see Accord in action, run `MainTest` in sample app from Android studio directly or run test using `./gradlew sample:accordTest`

<img src="https://raw.githubusercontent.com/naman14/Accord/master/screen_sample_test.png">

Also see a demo of Accord being used in Grofers consumer app - https://youtu.be/nFiJXFoUwYM