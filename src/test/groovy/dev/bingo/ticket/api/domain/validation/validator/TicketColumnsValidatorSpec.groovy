package dev.bingo.ticket.api.domain.validation.validator

import dev.bingo.ticket.api.domain.strip.model.AllocatedNumbers
import dev.bingo.ticket.api.domain.ticket.model.TicketColumn
import dev.bingo.ticket.api.domain.ticket.model.TicketColumns
import spock.lang.Specification

class TicketColumnsValidatorSpec extends Specification {

    TicketColumnsValidator validator

    def setup() {
        validator = new TicketColumnsValidator()
    }

    def "should validate correct numbers of generated columns"() {
        given: "A valid ticket with exactly 15 numbers across all columns"
            def previouslyAllocatedNumbers = new AllocatedNumbers()
            def ticketColumns = new TicketColumns([
                    new TicketColumn([1, 2, 3]),
                    new TicketColumn([10, 11, 12]),
                    new TicketColumn([20, 21, 22]),
                    new TicketColumn([30, 31, 32]),
                    new TicketColumn([40, 41, 42]),
                    new TicketColumn([]),
                    new TicketColumn([]),
                    new TicketColumn([]),
                    new TicketColumn([]),
            ])

        when: "The validator is invoked"
            validator.invoke(previouslyAllocatedNumbers, ticketColumns)

        then: "No exception should be thrown"
            notThrown(IllegalStateException)
    }

    def "should throw exception for incorrect number of generated numbers"() {
        given: "A ticket with less than 15 numbers"
            def previouslyAllocatedNumbers = new AllocatedNumbers()
            def ticketColumns = new TicketColumns([
                    new TicketColumn([1, 2, 3]),
                    new TicketColumn([10, 11, 12]),
                    new TicketColumn([20])
            ])

        when: "The validator is invoked"
            validator.invoke(previouslyAllocatedNumbers, ticketColumns)

        then: "An exception is thrown due to incorrect number of generated numbers"
            def e = thrown(IllegalStateException)
            e.message == "Invalid number of generated numbers. Expected 15, but found 7."
    }

    def "should throw exception for overlap with previously allocated numbers"() {
        given: "A ticket with new columns that overlap with previously allocated numbers"
            def previouslyAllocatedNumbers = new AllocatedNumbers()
            previouslyAllocatedNumbers.addNumberToColumn(0, 1)
            previouslyAllocatedNumbers.addNumberToColumn(1, 10)

            def ticketColumns = new TicketColumns([
                    new TicketColumn([1, 2, 3]),   // Overlaps with 1
                    new TicketColumn([10, 11, 12]), // Overlaps with 10
                    new TicketColumn([20, 21, 22]),
                    new TicketColumn([30, 31, 32]),
                    new TicketColumn([40, 41, 42]),
            ])

        when: "The validator is invoked"
            validator.invoke(previouslyAllocatedNumbers, ticketColumns)

        then: "An exception is thrown due to overlap"
            def e = thrown(IllegalStateException)
            e.message == "The generated numbers overlap with previously allocated numbers: [1, 10]"
    }

    def "should validate column ranges"() {
        given: "A ticket with invalid numbers outside the allowed range"
            def previouslyAllocatedNumbers = new AllocatedNumbers()
            def ticketColumns = new TicketColumns([
                    new TicketColumn([1, 2, 90]), // 90 is outside valid range for this column
                    new TicketColumn([11, 12, 13]),
                    new TicketColumn([21, 22, 23]),
                    new TicketColumn([31, 32, 33]),
                    new TicketColumn([41, 42, 43]),
            ])

        when: "The validator is invoked"
            validator.invoke(previouslyAllocatedNumbers, ticketColumns)

        then: "An exception is thrown due to invalid numbers"
            def e = thrown(IllegalStateException)
            e.message.contains("Column 1 contains invalid numbers: [90]")
    }

    def "should throw exception for duplicate numbers in a column"() {
        given: "A ticket with duplicate numbers in a single column"
            def previouslyAllocatedNumbers = new AllocatedNumbers()
            def ticketColumns = new TicketColumns([
                    new TicketColumn([1, 1, 3]), // Duplicate 1
                    new TicketColumn([11, 12, 13]),
                    new TicketColumn([21, 22, 23]),
                    new TicketColumn([31, 32, 33]),
                    new TicketColumn([41, 42, 43]),
            ])

        when: "The validator is invoked"
            validator.invoke(previouslyAllocatedNumbers, ticketColumns)

        then: "An exception is thrown due to duplicates"
            def e = thrown(IllegalStateException)
            e.message == "Column 1 contains duplicate numbers: [1]"
    }

    def "should throw exception for exceeding max allocations in a column"() {
        given: "A ticket with a column exceeding the maximum allowed numbers"
            def previouslyAllocatedNumbers = new AllocatedNumbers()
            def ticketColumns = new TicketColumns([
                    new TicketColumn([1, 2, 3, 4]), // Exceeds max 3
                    new TicketColumn([11, 12]),
                    new TicketColumn([21]),
                    new TicketColumn([31, 32, 33]),
                    new TicketColumn([41, 42, 43]),
                    new TicketColumn([51, 52]),
            ])

        when: "The validator is invoked"
            validator.invoke(previouslyAllocatedNumbers, ticketColumns)

        then: "An exception is thrown due to too many allocations"
            def e = thrown(IllegalStateException)
            e.message == "Column 1 has an invalid number of allocations. Expected between 1 and 3, but found 4."
    }
}