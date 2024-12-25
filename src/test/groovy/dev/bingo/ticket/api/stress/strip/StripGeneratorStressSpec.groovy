package dev.bingo.ticket.api.stress.strip

import dev.bingo.ticket.api.BingoTicketGeneratorApplication
import dev.bingo.ticket.api.domain.strip.service.StripGeneratorService
import dev.bingo.ticket.api.domain.ticket.service.ColumnRandomValueGeneratorService
import dev.bingo.ticket.api.domain.ticket.service.TicketGeneratorService
import dev.bingo.ticket.api.domain.validation.validator.TicketColumnsValidator
import dev.bingo.ticket.api.stress.StressTestSpecification
import org.springframework.boot.test.context.SpringBootTest
import spock.lang.Subject
import spock.lang.Timeout

import java.util.concurrent.TimeUnit

@SpringBootTest(classes = BingoTicketGeneratorApplication.class)
class StripGeneratorStressSpec extends StressTestSpecification {

    def columnRandomValueGeneratorService = new ColumnRandomValueGeneratorService()
    def ticketColumnsValidator = new TicketColumnsValidator()
    def ticketGeneratorService = new TicketGeneratorService(columnRandomValueGeneratorService, ticketColumnsValidator)

    @Subject
    def stripGeneratorService = new StripGeneratorService(ticketGeneratorService)

    @Timeout(value = 600, unit = TimeUnit.MILLISECONDS)
    def "should generate 1k strips without error under 2 seconds"() {
        given: "A target number of strips to generate"
            def numberOfStrips = 1000
            def generatedStrips = []

        when: "The StripGeneratorService generates strips"
            (1..numberOfStrips).each {
                def strip = stripGeneratorService.generateStrip()
                generatedStrips.add(strip)
            }

        then: "No exception occurs during generation"
            generatedStrips.size() == numberOfStrips
            println "Successfully generated $numberOfStrips strips!"
    }

    @Timeout(value = 1600, unit = TimeUnit.MILLISECONDS)
    def "should generate 10k strips without error under 3 seconds"() {
        given: "A target number of strips to generate"
            def numberOfStrips = 10000
            def generatedStrips = []

        when: "The StripGeneratorService generates strips"
            (1..numberOfStrips).each {
                def strip = stripGeneratorService.generateStrip()
                generatedStrips.add(strip)
            }

        then: "No exception occurs during generation"
            generatedStrips.size() == numberOfStrips
            println "Successfully generated $numberOfStrips strips!"
    }

    @Timeout(value = 8, unit = TimeUnit.SECONDS)
    def "should generate 100k strips without error under 5 seconds"() {
        given: "A target number of strips to generate"
            def numberOfStrips = 100000
            def generatedStrips = []

        when: "The StripGeneratorService generates strips"
            (1..numberOfStrips).each {
                def strip = stripGeneratorService.generateStrip()
                generatedStrips.add(strip)
            }

        then: "No exception occurs during generation"
            generatedStrips.size() == numberOfStrips
            println "Successfully generated $numberOfStrips strips!"
    }

    @Timeout(value = 15, unit = TimeUnit.MILLISECONDS)
    def "should generate 1 strip without error"() {
        when: "The StripGeneratorService generates strip"
            stripGeneratorService.generateStrip()

        then: "No exception occurs during generation"
            println "Successfully generated 1 strip"
    }
}