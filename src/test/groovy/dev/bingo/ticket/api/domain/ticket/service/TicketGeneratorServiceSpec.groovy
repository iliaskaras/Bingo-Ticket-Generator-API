package dev.bingo.ticket.api.domain.ticket.service

import dev.bingo.ticket.api.domain.strip.model.AllocatedNumbers
import dev.bingo.ticket.api.domain.ticket.model.TicketColumn
import dev.bingo.ticket.api.domain.ticket.model.TicketColumnEnum
import dev.bingo.ticket.api.domain.ticket.model.TicketColumns
import dev.bingo.ticket.api.domain.ticket.model.TicketRowCell
import spock.lang.Specification

class TicketGeneratorServiceSpec extends Specification {

    def columnRandomValueGeneratorService = Mock(ColumnRandomValueGeneratorService)
    def ticketGeneratorService = new TicketGeneratorService(columnRandomValueGeneratorService)

    def "should generate a valid Bingo ticket with 15 new numbers"() {
        given: "A map of previously allocated numbers for each column"
            def previouslyAllocatedNumbers = new AllocatedNumbers()

            // Adding previously allocated numbers to columns
            previouslyAllocatedNumbers.addNumberToColumn(0, 1)
            previouslyAllocatedNumbers.addNumberToColumn(0, 2)
            previouslyAllocatedNumbers.addNumberToColumn(0, 3)
            previouslyAllocatedNumbers.addNumberToColumn(1, 11)
            previouslyAllocatedNumbers.addNumberToColumn(1, 12)
            previouslyAllocatedNumbers.addNumberToColumn(2, 21)
            previouslyAllocatedNumbers.addNumberToColumn(3, 31)
            previouslyAllocatedNumbers.addNumberToColumn(3, 32)
            previouslyAllocatedNumbers.addNumberToColumn(4, 41)
            previouslyAllocatedNumbers.addNumberToColumn(4, 42)
            previouslyAllocatedNumbers.addNumberToColumn(5, 50)
            previouslyAllocatedNumbers.addNumberToColumn(6, 60)
            previouslyAllocatedNumbers.addNumberToColumn(7, 70)
            previouslyAllocatedNumbers.addNumberToColumn(8, 80)
            previouslyAllocatedNumbers.addNumberToColumn(8, 81)

        and: "The columnRandomValueGeneratorService is mocked to return a valid ticket"
            // Mocking the service to return a generated TicketColumns with exactly 15 new numbers
            def generatedTicketColumns = [
                    new TicketColumn([4, 5, 6] as List),          // 3 numbers in column 0
                    new TicketColumn([13, 14] as List),           // 2 numbers in column 1
                    new TicketColumn([22] as List),               // 1 number in column 2
                    new TicketColumn([33, 34] as List),           // 2 numbers in column 3
                    new TicketColumn([43, 44] as List),           // 2 numbers in column 4
                    new TicketColumn([51] as List),               // 1 new numbers in column 5
                    new TicketColumn([61] as List),               // 1 new numbers in column 6
                    new TicketColumn([71] as List),               // 1 new numbers in column 7
                    new TicketColumn([82, 83] as List)            // 2 new numbers in column 8
            ]
            columnRandomValueGeneratorService.generateColumnValues(_) >> new TicketColumns(generatedTicketColumns)

        when: "We generate a new Bingo ticket"
            def ticket = ticketGeneratorService.generateTicket(previouslyAllocatedNumbers)

        then: "The generated rows should contain exactly 5 numbers each"
            ticket.rows .each { row ->
                row.cells.count { it instanceof TicketRowCell.NumberRowCell } == 5
            }

        and: "The generated rows should contain exactly 4 blanks each"
            ticket.rows.each { row ->
                row.cells.count { it instanceof TicketRowCell.BlankRowCell } == 4
            }

        and: "Each column in the ticket should contain numbers within the correct range for its index"
            (0..8).collect { columnIndex ->
                def columnValues = extractColumnValues(ticket.rows, columnIndex)
                def columnValidValues = TicketColumnEnum.getByIndex(columnIndex)
                assert columnValidValues.valuesRange.toList().containsAll(columnValues)
            }

        and: "No number should appear in more than one column"
            def allNumbers = ticket.rows
                    .collectMany { row ->
                        row.cells.findAll { it instanceof TicketRowCell.NumberRowCell }
                    }
                    .collect { (it as TicketRowCell.NumberRowCell).number }
            assert allNumbers.toSet().size() == 15

        and: "The ticket contain correct number of blanks"
            def allBlanks = ticket.rows
                    .collectMany { row ->
                        row.cells.findAll { it instanceof TicketRowCell.BlankRowCell }
                    }
            assert allBlanks.toList().size() == 12
    }

    def "should throw exception when generated numbers are not exactly 15"() {
        given: "A mocked columnRandomValueGeneratorService returning less than 15 numbers"
            def invalidTicketColumns = new TicketColumns([
                    new TicketColumn([1, 2, 3]),     // Column 0
                    new TicketColumn([10, 11]),      // Column 1
                    new TicketColumn([20])           // Column 2 (only 6 numbers total)
            ])
            columnRandomValueGeneratorService.generateColumnValues(_) >> invalidTicketColumns

        when: "Generating a ticket"
            ticketGeneratorService.generateTicket(new AllocatedNumbers())

        then: "An exception is thrown due to incorrect number of generated numbers"
            def e = thrown(IllegalStateException)
            e.message == "Invalid number of generated numbers. Expected 15, but found 6."
    }

    def "should throw exception when generated numbers overlap with previously allocated numbers"() {
        given: "An AllocatedNumbers model with previously allocated numbers and overlapping generated numbers"
            def previouslyAllocatedNumbers = new AllocatedNumbers()

            // Adding previously allocated numbers to columns
            previouslyAllocatedNumbers.addNumberToColumn(0, 1)
            previouslyAllocatedNumbers.addNumberToColumn(0, 2)
            previouslyAllocatedNumbers.addNumberToColumn(0, 3)
            previouslyAllocatedNumbers.addNumberToColumn(1, 10)
            previouslyAllocatedNumbers.addNumberToColumn(1, 11)

        and: "Overlapping generated ticket columns"
            def overlappingTicketColumns = new TicketColumns([
                    new TicketColumn([4, 5, 1]),     // Overlaps with 1 in Column 0
                    new TicketColumn([12, 13, 10]),  // Overlaps with 10 in Column 1
                    new TicketColumn([20, 21, 22]),  // No overlap here
                    new TicketColumn([30, 31, 32]),  // No overlap here
                    new TicketColumn([40, 41, 42])   // No overlap here
            ])
            columnRandomValueGeneratorService.generateColumnValues(_) >> overlappingTicketColumns

        when: "Generating a ticket"
            ticketGeneratorService.generateTicket(previouslyAllocatedNumbers)

        then: "An exception is thrown due to overlapping numbers"
            def e = thrown(IllegalStateException)
            e.message == "The generated numbers overlap with previously allocated numbers: [1, 10]"
    }

    private static def extractColumnValues(ticketRows, columnIndex) {
        ticketRows.collect { row ->
            def cell = row.cells[columnIndex]
            cell instanceof TicketRowCell.NumberRowCell ? cell.number : null
        }.findAll { it != null }
    }
}