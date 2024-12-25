package dev.bingo.ticket.api.stress

import spock.lang.Specification
import spock.lang.Stepwise
import java.util.concurrent.TimeUnit

/**
 * Base class for stress tests to handle timing logic.
 */
@Stepwise
abstract class StressTestSpecification extends Specification {

    long startTime
    long endTime

    def setup() {
        startTime = System.nanoTime()
    }

    def cleanup() {
        endTime = System.nanoTime()
        long duration = TimeUnit.NANOSECONDS.toMillis(endTime - startTime)

        println "Execution time for '${getSpecificationContext().currentIteration.name}': ${duration} ms"
    }
}