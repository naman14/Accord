package com.naman14.accord.library.annotation

/**
 * Annotation class for marking a test as a TimeTest. TimeTest are used to benchmarks individual
 * tests by measuring the average time to do a operation over a repeat count.
 *
 * @param threshold threshold time to benchmark against
 * @param repeatCount count over which the test will be repeated
 */
annotation class TimeTest(val threshold: Int = -1,
                          val repeatCount: Int = 1)