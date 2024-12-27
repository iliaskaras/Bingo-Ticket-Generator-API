package dev.bingo.ticket.api.domain.ticket.service

import dev.bingo.ticket.api.domain.strip.model.AllocatedNumbers
import dev.bingo.ticket.api.domain.ticket.model.ColumnAllocationTracker
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
                def ticketColumns = columnRandomValueGeneratorService.generateColumnValues(new AllocatedNumbers())
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
            def previouslyAllocatedNumbers = new AllocatedNumbers()
            previouslyAllocatedNumbers.addNumberToColumn(0, 1)
            previouslyAllocatedNumbers.addNumberToColumn(0, 2)
            previouslyAllocatedNumbers.addNumberToColumn(0, 3)
            previouslyAllocatedNumbers.addNumberToColumn(0, 4)
            previouslyAllocatedNumbers.addNumberToColumn(0, 5)
            previouslyAllocatedNumbers.addNumberToColumn(0, 6)
            previouslyAllocatedNumbers.addNumberToColumn(0, 7)
            previouslyAllocatedNumbers.addNumberToColumn(0, 8)

            previouslyAllocatedNumbers.addNumberToColumn(1, 10)
            previouslyAllocatedNumbers.addNumberToColumn(1, 14)
            previouslyAllocatedNumbers.addNumberToColumn(1, 15)
            previouslyAllocatedNumbers.addNumberToColumn(1, 16)
            previouslyAllocatedNumbers.addNumberToColumn(1, 17)
            previouslyAllocatedNumbers.addNumberToColumn(1, 18)
            previouslyAllocatedNumbers.addNumberToColumn(1, 19)

            previouslyAllocatedNumbers.addNumberToColumn(2, 22)
            previouslyAllocatedNumbers.addNumberToColumn(2, 23)
            previouslyAllocatedNumbers.addNumberToColumn(2, 24)
            previouslyAllocatedNumbers.addNumberToColumn(2, 25)
            previouslyAllocatedNumbers.addNumberToColumn(2, 26)
            previouslyAllocatedNumbers.addNumberToColumn(2, 27)
            previouslyAllocatedNumbers.addNumberToColumn(2, 28)
            previouslyAllocatedNumbers.addNumberToColumn(2, 29)

            previouslyAllocatedNumbers.addNumberToColumn(3, 30)
            previouslyAllocatedNumbers.addNumberToColumn(3, 31)
            previouslyAllocatedNumbers.addNumberToColumn(3, 32)
            previouslyAllocatedNumbers.addNumberToColumn(3, 33)
            previouslyAllocatedNumbers.addNumberToColumn(3, 34)
            previouslyAllocatedNumbers.addNumberToColumn(3, 35)
            previouslyAllocatedNumbers.addNumberToColumn(3, 36)
            previouslyAllocatedNumbers.addNumberToColumn(3, 37)
            previouslyAllocatedNumbers.addNumberToColumn(3, 38)
            previouslyAllocatedNumbers.addNumberToColumn(3, 39)

            previouslyAllocatedNumbers.addNumberToColumn(4, 40)
            previouslyAllocatedNumbers.addNumberToColumn(4, 41)
            previouslyAllocatedNumbers.addNumberToColumn(4, 42)
            previouslyAllocatedNumbers.addNumberToColumn(4, 43)
            previouslyAllocatedNumbers.addNumberToColumn(4, 44)
            previouslyAllocatedNumbers.addNumberToColumn(4, 48)
            previouslyAllocatedNumbers.addNumberToColumn(4, 49)

            previouslyAllocatedNumbers.addNumberToColumn(5, 50)
            previouslyAllocatedNumbers.addNumberToColumn(5, 51)
            previouslyAllocatedNumbers.addNumberToColumn(5, 52)
            previouslyAllocatedNumbers.addNumberToColumn(5, 53)
            previouslyAllocatedNumbers.addNumberToColumn(5, 54)
            previouslyAllocatedNumbers.addNumberToColumn(5, 55)
            previouslyAllocatedNumbers.addNumberToColumn(5, 56)
            previouslyAllocatedNumbers.addNumberToColumn(5, 57)

            previouslyAllocatedNumbers.addNumberToColumn(6, 61)
            previouslyAllocatedNumbers.addNumberToColumn(6, 62)
            previouslyAllocatedNumbers.addNumberToColumn(6, 63)
            previouslyAllocatedNumbers.addNumberToColumn(6, 64)
            previouslyAllocatedNumbers.addNumberToColumn(6, 65)
            previouslyAllocatedNumbers.addNumberToColumn(6, 66)
            previouslyAllocatedNumbers.addNumberToColumn(6, 67)
            previouslyAllocatedNumbers.addNumberToColumn(6, 68)
            previouslyAllocatedNumbers.addNumberToColumn(6, 69)

            previouslyAllocatedNumbers.addNumberToColumn(7, 71)
            previouslyAllocatedNumbers.addNumberToColumn(7, 72)
            previouslyAllocatedNumbers.addNumberToColumn(7, 73)
            previouslyAllocatedNumbers.addNumberToColumn(7, 74)
            previouslyAllocatedNumbers.addNumberToColumn(7, 75)
            previouslyAllocatedNumbers.addNumberToColumn(7, 76)
            previouslyAllocatedNumbers.addNumberToColumn(7, 77)
            previouslyAllocatedNumbers.addNumberToColumn(7, 78)
            previouslyAllocatedNumbers.addNumberToColumn(7, 79)

            previouslyAllocatedNumbers.addNumberToColumn(8, 81)
            previouslyAllocatedNumbers.addNumberToColumn(8, 82)
            previouslyAllocatedNumbers.addNumberToColumn(8, 83)
            previouslyAllocatedNumbers.addNumberToColumn(8, 84)
            previouslyAllocatedNumbers.addNumberToColumn(8, 85)
            previouslyAllocatedNumbers.addNumberToColumn(8, 86)
            previouslyAllocatedNumbers.addNumberToColumn(8, 87)
            previouslyAllocatedNumbers.addNumberToColumn(8, 88)
            previouslyAllocatedNumbers.addNumberToColumn(8, 89)

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
            def previouslyAllocatedNumbers = new AllocatedNumbers()

            // Fully allocate columns
            (1..9).each { previouslyAllocatedNumbers.addNumberToColumn(0, it) }
            (10..19).each { previouslyAllocatedNumbers.addNumberToColumn(1, it) }
            (20..29).each { previouslyAllocatedNumbers.addNumberToColumn(2, it) }
            (30..39).each { previouslyAllocatedNumbers.addNumberToColumn(3, it) }
            (40..49).each { previouslyAllocatedNumbers.addNumberToColumn(4, it) }
            (50..59).each { previouslyAllocatedNumbers.addNumberToColumn(5, it) }
            (60..69).each { previouslyAllocatedNumbers.addNumberToColumn(6, it) }
            (70..79).each { previouslyAllocatedNumbers.addNumberToColumn(7, it) }

            // Column 8 has only one number left (89)
            previouslyAllocatedNumbers.addNumberToColumn(8, 80)
            previouslyAllocatedNumbers.addNumberToColumn(8, 81)
            previouslyAllocatedNumbers.addNumberToColumn(8, 82)
            previouslyAllocatedNumbers.addNumberToColumn(8, 83)
            previouslyAllocatedNumbers.addNumberToColumn(8, 84)
            previouslyAllocatedNumbers.addNumberToColumn(8, 85)
            previouslyAllocatedNumbers.addNumberToColumn(8, 86)
            previouslyAllocatedNumbers.addNumberToColumn(8, 87)
            previouslyAllocatedNumbers.addNumberToColumn(8, 88)

        when: "We attempt to generate column values for a new ticket"
            columnRandomValueGeneratorService.generateColumnValues(previouslyAllocatedNumbers)

        then: "An IllegalStateException is thrown due to insufficient numbers in column 8"
            def exception = thrown(IllegalStateException)
            exception.message == "No valid columns left for allocation."
    }

    def "should throw IllegalArgumentException when invalid column index is used for allocation"() {
        given: "A ColumnAllocationTracker with mock data"
            def allocations = new int[9]
            def remainingNumbers = (0..8).collect { [1, 2, 3] as ArrayList }
            def ticketColumns = (0..8).collect { [] as List }

            def columnAllocationTracker = new ColumnAllocationTracker(
                allocations,
                remainingNumbers,
                List.of()
            )

        when: "Trying to allocate a number to an invalid column index"
            columnRandomValueGeneratorService.allocateNumberToColumn(
                columnAllocationTracker, 9, ticketColumns
            )

        then: "An IllegalArgumentException is thrown"
            def exception = thrown(IllegalArgumentException)
            exception.message.contains("Invalid column index")
    }

    def "should throw IllegalArgumentException when invalid column index is used for allocation"() {
        given: "A ColumnAllocationTracker with mock data"
            def allocations = new int[9]
            def remainingNumbers = (0..8).collect { [1, 2, 3] as ArrayList }
            def ticketColumns = (0..8).collect { [] as List }

            def columnAllocationTracker = new ColumnAllocationTracker(
                    allocations,
                    remainingNumbers,
                    List.of()
            )

        when: "Trying to allocate a number to an invalid column index"
            columnRandomValueGeneratorService.allocateNumberToColumn(
                    columnAllocationTracker, 9, ticketColumns
            )

        then: "An IllegalArgumentException is thrown"
            def exception = thrown(IllegalArgumentException)
            exception.message == "Invalid column index: 9. Must be between 0 and 8."
    }

    def "should throw IllegalStateException when there are no valid columns left for allocation"() {
        given: "A ColumnAllocationTracker with fully allocated columns"
            def allocations = new int[9]
            def remainingNumbers = (0..8).collect { [] as ArrayList }  // No remaining numbers

            def columnAllocationTracker = new ColumnAllocationTracker(
                    allocations,
                    remainingNumbers,
                    List.of()
            )

        when: "Trying to select a column for allocation"
            columnRandomValueGeneratorService.selectUnderpopulatedColumn(
                    columnAllocationTracker, 5
            )

        then: "An IllegalStateException is thrown due to no available columns"
            def exception = thrown(IllegalStateException)
            exception.message == "No valid columns left for allocation."
    }

    def "should throw IllegalStateException when there are no remaining numbers for allocation in a column"() {
        given: "A ColumnAllocationTracker where column 0 is out of available numbers"
            def allocations = new int[9]
            def remainingNumbers = (0..8).collect { [] as ArrayList }  // Column 0 has no numbers left
            def ticketColumns = (0..8).collect { [] as List }

            def columnAllocationTracker = new ColumnAllocationTracker(
                    allocations,
                    remainingNumbers,
                    List.of()
            )

        when: "Trying to allocate a number to column 0 which has no numbers left"
            columnRandomValueGeneratorService.allocateNumberToColumn(
                    columnAllocationTracker, 0, ticketColumns
            )

        then: "An IllegalStateException is thrown"
            def exception = thrown(IllegalStateException)
            exception.message == "No remaining numbers or column 0 is fully allocated."
    }
}