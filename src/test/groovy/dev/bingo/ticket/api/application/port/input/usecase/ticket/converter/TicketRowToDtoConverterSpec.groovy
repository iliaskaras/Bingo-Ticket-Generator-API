package dev.bingo.ticket.api.application.port.input.usecase.ticket.converter

import dev.bingo.ticket.api.application.port.input.usecase.ticket.model.TicketRowCellDto
import dev.bingo.ticket.api.application.port.input.usecase.ticket.model.TicketRowDto
import dev.bingo.ticket.api.domain.ticket.model.TicketRow
import dev.bingo.ticket.api.domain.ticket.model.TicketRowCell
import spock.lang.Specification
import spock.lang.Subject

class TicketRowToDtoConverterSpec extends Specification {

    @Subject
    TicketRowToDtoConverter ticketRowToDtoConverter = new TicketRowToDtoConverter()

    def "should convert TicketRow with NumberRowCell and BlankRowCell to TicketRowDto"() {
        given:
            def numberRowCell = new TicketRowCell.NumberRowCell(42)
            def blankRowCell = TicketRowCell.BlankRowCell.INSTANCE

            def ticketRow = new TicketRow([numberRowCell, blankRowCell])

        when:
            def result = ticketRowToDtoConverter.invoke(ticketRow)

        then:
            result instanceof TicketRowDto
            result.cells.size() == 2

            result.cells[0] instanceof TicketRowCellDto.NumberRowCellDto
            result.cells[0].number == 42

            result.cells[1] == TicketRowCellDto.BlankRowCellDto.INSTANCE
    }

    def "should handle an empty TicketRow and return an empty TicketRowDto"() {
        given:
            def ticketRow = new TicketRow([])

        when:
            def result = ticketRowToDtoConverter.invoke(ticketRow)

        then:
            result instanceof TicketRowDto
            result.cells.isEmpty()
    }

    def "should convert a single NumberRowCell to TicketRowDto"() {
        given:
            def numberRowCell = new TicketRowCell.NumberRowCell(99)
            def ticketRow = new TicketRow([numberRowCell])

        when:
            def result = ticketRowToDtoConverter.invoke(ticketRow)

        then:
            result instanceof TicketRowDto
            result.cells.size() == 1
            result.cells[0] instanceof TicketRowCellDto.NumberRowCellDto
            result.cells[0].number == 99
    }

    def "should convert a single BlankRowCell to TicketRowDto"() {
        given:
            def blankRowCell = TicketRowCell.BlankRowCell.INSTANCE
            def ticketRow = new TicketRow([blankRowCell])

        when:
            def result = ticketRowToDtoConverter.invoke(ticketRow)

        then:
            result instanceof TicketRowDto
            result.cells.size() == 1
            result.cells[0] == TicketRowCellDto.BlankRowCellDto.INSTANCE
    }
}