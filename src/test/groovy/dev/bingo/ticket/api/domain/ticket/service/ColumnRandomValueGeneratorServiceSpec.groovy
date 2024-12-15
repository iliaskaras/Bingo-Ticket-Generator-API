package dev.bingo.ticket.api.domain.ticket.service

import dev.bingo.ticket.api.domain.ticket.model.TicketColumnEnum
import spock.lang.Specification
import spock.lang.Subject

class ColumnRandomValueGeneratorServiceSpec extends Specification {

    @Subject
    def columnRandomValueGeneratorService = new ColumnRandomValueGeneratorService()

    def "when generateColumnValues is called then it should return TicketColumns with valid random numbers in each column"() {
        given: "A set to track all seen numbers across all columns"
            def seenNumbers = [] as Set
            def allNumbers = TicketColumnEnum.allValues()
            def maxTries = 1_000
            def tryCount = 0

        and: "Expected numbers range per column"
            def columnRanges = TicketColumnEnum.allRanges()

        when: "The generate method is called"
            while (seenNumbers.size() < allNumbers.size() && tryCount < maxTries) {
                def ticketColumns = columnRandomValueGeneratorService.generateColumnValues()
                tryCount++

                // Add generated numbers to the set of seen numbers
                ticketColumns.columns.eachWithIndex { column, columnIndex ->
                    // Validate each number is within the correct range for the column
                    column.numbers.each { number ->
                        assert columnRanges[columnIndex].contains(number) : "Number $number in column ${columnIndex + 1} is out of range"
                        seenNumbers.add(number)
                    }
                }
            }

        then: "All possible values from each range are eventually seen or max tries are reached"
            assert seenNumbers.size() == allNumbers.size()

        and: "The number of tries to generate all possible numbers should not exceed the max limit"
            assert tryCount < maxTries
    }
}