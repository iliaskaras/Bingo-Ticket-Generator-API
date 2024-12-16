package dev.bingo.ticket.api.domain.ticket.service

import dev.bingo.ticket.api.domain.ticket.model.TicketColumnEnum
import dev.bingo.ticket.api.domain.ticket.model.TicketRowCell
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
                def ticketColumns = columnRandomValueGeneratorService.generateColumnValues(Map.of())
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

    def "when generate is called, the generated numbers should match the remaining unallocated numbers"() {
        given: "A map of previously allocated numbers"
            def previouslyAllocatedNumbers = [
                    0: [1, 2, 3, 4, 5, 6, 7, 8] as Set, // 9 is not allocated
                    1: [10, 14, 15, 16, 17, 18, 19] as Set, // 11, 12, 13 are not allocated
                    2: [22, 23, 24, 25, 26, 27, 28, 29] as Set, // 20, 21 are not allocated
                    3: [30, 31, 32, 33, 34, 35, 36, 37, 38, 39] as Set, // all are allocated
                    4: [40, 41, 42, 43, 44, 48, 49] as Set, // 45, 46, 47 are not allocated
                    5: [50, 51, 52, 53, 54, 55, 56, 57] as Set, // 58, 59 are not allocated
                    6: [61, 62, 63, 64, 65, 66, 67, 68, 69] as Set, // 60 is not allocated
                    7: [71, 72, 73, 74, 75, 76, 77, 78, 79] as Set, // 70 is not allocated
                    8: [81, 82, 83, 84, 85, 86, 87, 88, 89] as Set // 80, 90 are not allocated
            ]

        and: "Expected TicketRow objects based on the remaining unallocated numbers"
            def expectedTicketRows = [
                    // Column 0 has number 9
                    [new TicketRowCell.NumberRowCell(9), TicketRowCell.BlankRowCell, TicketRowCell.BlankRowCell],
                    // Column 1 has numbers 11, 12, 13
                    [new TicketRowCell.NumberRowCell(11), new TicketRowCell.NumberRowCell(12), new TicketRowCell.NumberRowCell(13)],
                    // Column 2 has numbers 20, 21
                    [new TicketRowCell.NumberRowCell(20), new TicketRowCell.NumberRowCell(21), TicketRowCell.BlankRowCell],
                    // Column 3 has no numbers
                    [TicketRowCell.BlankRowCell, TicketRowCell.BlankRowCell, TicketRowCell.BlankRowCell],
                    // Column 4 has numbers 45, 46, 47
                    [new TicketRowCell.NumberRowCell(45), new TicketRowCell.NumberRowCell(46), new TicketRowCell.NumberRowCell(47)],
                    // Column 5 has numbers 58, 59
                    [new TicketRowCell.NumberRowCell(58), new TicketRowCell.NumberRowCell(59), TicketRowCell.BlankRowCell],
                    // Column 6 has number 60
                    [new TicketRowCell.NumberRowCell(60), TicketRowCell.BlankRowCell, TicketRowCell.BlankRowCell],
                    // Column 7 has number 70
                    [new TicketRowCell.NumberRowCell(70), TicketRowCell.BlankRowCell, TicketRowCell.BlankRowCell],
                    // Column 8 has numbers 80, 90
                    [new TicketRowCell.NumberRowCell(80), new TicketRowCell.NumberRowCell(90), TicketRowCell.BlankRowCell]
            ]

        when: "The generate method is called with previously allocated numbers"
            def ticketColumns = columnRandomValueGeneratorService.generateColumnValues(previouslyAllocatedNumbers)

        then: "The generated numbers should match the remaining unallocated numbers"
            expectedTicketRows.eachWithIndex { expectedColumn, columnIndex ->
                def column = ticketColumns.columns[columnIndex]
                assert column.numbers.sort() == expectedColumn.findAll { it instanceof TicketRowCell.NumberRowCell }.sort { it.number }.collect { it.number }
            }
    }


    def "should throw IllegalStateException when there aren't enough numbers available for allocation"() {
        given: "A map of previously allocated numbers that makes one column unavailable"
            def previouslyAllocatedNumbers = [
                    0: (1..9).toSet(),   // Column 0 is fully allocated (1-9)
                    1: (10..19).toSet(), // Column 1 is fully allocated (10-19)
                    2: (20..29).toSet(), // Column 2 is fully allocated (20-29)
                    3: (30..39).toSet(), // Column 3 is fully allocated (30-39)
                    4: (40..49).toSet(), // Column 4 is fully allocated (40-49)
                    5: (50..59).toSet(), // Column 5 is fully allocated (50-59)
                    6: (60..69).toSet(), // Column 6 is fully allocated (60-69)
                    7: (70..79).toSet(), // Column 7 is fully allocated (70-79)
                    8: (80..88).toSet()  // Column 8 has only one number left available (89)
            ]

        when: "We attempt to generate column values for a new ticket"
            columnRandomValueGeneratorService.generateColumnValues(previouslyAllocatedNumbers)

        then: "An IllegalStateException is thrown due to insufficient numbers in column 8"
            def exception = thrown(IllegalStateException)
            exception.message == "Not enough numbers available for column to allocate 2 values."
    }
}