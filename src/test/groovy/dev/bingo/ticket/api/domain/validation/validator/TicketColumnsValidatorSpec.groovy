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

    def "should validate new column numbers size"() {
        given: "A valid ticket with the correct number of generated columns"
            def previouslyAllocatedNumbers = new AllocatedNumbers()  // No previously allocated numbers
            def ticketColumns = new TicketColumns([
                    new TicketColumn([1, 2, 3]),
                    new TicketColumn([4, 5, 6]),
                    new TicketColumn([7, 8, 9]),
                    new TicketColumn([10, 11, 12]),
                    new TicketColumn([13, 14, 15]),
            ])

        when: "The validator is invoked to check the ticket columns"
            validator.invoke(previouslyAllocatedNumbers, ticketColumns)

        then: "No exception should be thrown"
            notThrown(IllegalStateException)
    }

    def "should throw exception for invalid number of generated columns"() {
        given: "A ticket with an invalid number of columns"
            def previouslyAllocatedNumbers = new AllocatedNumbers()  // No previously allocated numbers
            def ticketColumns = new TicketColumns([
                    new TicketColumn([1, 2, 3]),
                    new TicketColumn([4, 5, 6]),
                    new TicketColumn([7, 8, 9]),
                    new TicketColumn([10, 11, 12]),
                    new TicketColumn([13, 14, 15]),
                    new TicketColumn([16, 17, 18]),
                    new TicketColumn([19, 20, 21]),
                    new TicketColumn([22, 23, 24]),
                    new TicketColumn([25, 26, 27]),
                    new TicketColumn([28, 29, 30])
            ])

        when: "The validator is invoked"
            validator.invoke(previouslyAllocatedNumbers, ticketColumns)

        then: "An exception should be thrown due to invalid number of generated columns"
            def e = thrown(IllegalStateException)
            e.message == "Invalid number of generated numbers. Expected 15, but found 30."
    }

    def "should throw exception for overlap with previously allocated numbers"() {
        given: "A ticket with previously allocated numbers and new columns that overlap"
            def previouslyAllocatedNumbers = new AllocatedNumbers()
            previouslyAllocatedNumbers.addNumberToColumn(0, 1)  // Column 0 already has number 1
            previouslyAllocatedNumbers.addNumberToColumn(1, 10) // Column 1 already has number 10

            def ticketColumns = new TicketColumns([
                    new TicketColumn([1, 2, 3]),   // This column overlaps with number 1 from previously allocated
                    new TicketColumn([10, 11, 12]), // This column overlaps with number 10 from previously allocated
                    new TicketColumn([13, 14, 15]),
                    new TicketColumn([16, 17, 18]),
                    new TicketColumn([19, 20, 21])
            ])

        when: "The validator is invoked"
            validator.invoke(previouslyAllocatedNumbers, ticketColumns)

        then: "An exception should be thrown due to the overlap"
            def e = thrown(IllegalStateException)
            e.message == "The generated numbers overlap with previously allocated numbers: [1, 10]"
    }

    def "should not throw exception when no overlap occurs"() {
        given: "A ticket with no overlap with previously allocated numbers"
            def previouslyAllocatedNumbers = new AllocatedNumbers()
            previouslyAllocatedNumbers.addNumberToColumn(0, 1)  // Column 0 already has number 1
            previouslyAllocatedNumbers.addNumberToColumn(1, 10) // Column 1 already has number 10

            def ticketColumns = new TicketColumns([
                    new TicketColumn([2, 3, 4]),  // No overlap with previously allocated numbers
                    new TicketColumn([11, 12, 13]), // No overlap with previously allocated numbers
                    new TicketColumn([13, 14, 15]),
                    new TicketColumn([16, 17, 18]),
                    new TicketColumn([19, 20, 21])
            ])

        when: "The validator is invoked"
            validator.invoke(previouslyAllocatedNumbers, ticketColumns)

        then: "No exception should be thrown"
            notThrown(IllegalStateException)
    }
}