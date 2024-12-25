package dev.bingo.ticket.api.application.port.input.usecase.ticket.converter

import dev.bingo.ticket.api.application.port.input.usecase.ticket.model.TicketRowCellDto
import dev.bingo.ticket.api.domain.ticket.model.Ticket
import dev.bingo.ticket.api.domain.ticket.model.TicketRow
import dev.bingo.ticket.api.domain.ticket.model.TicketRowCell
import dev.bingo.ticket.api.application.port.input.usecase.ticket.model.TicketDto
import dev.bingo.ticket.api.application.port.input.usecase.ticket.model.TicketRowDto
import spock.lang.Specification
import spock.lang.Subject

class TicketToDtoConverterSpec extends Specification {

    TicketRowToDtoConverter ticketRowToDtoConverter = Mock(TicketRowToDtoConverter)

    @Subject
    TicketToDtoConverter ticketToDtoConverter = new TicketToDtoConverter(ticketRowToDtoConverter)

    def "should convert Ticket with TicketRow to TicketDto"() {
        given:
            def ticketRow = new TicketRow([new TicketRowCell.NumberRowCell(42)])
            def ticketRowDto = new TicketRowDto([new TicketRowCellDto.NumberRowCellDto(42)])

            ticketRowToDtoConverter.invoke(ticketRow) >> ticketRowDto

            def ticket = new Ticket([ticketRow])

        when:
            def result = ticketToDtoConverter.invoke(ticket)

        then:
            result instanceof TicketDto
            result.rows.size() == 1
            result.rows[0] instanceof TicketRowDto
            result.rows[0].cells.size() == 1
            result.rows[0].cells[0] instanceof TicketRowCellDto.NumberRowCellDto
            result.rows[0].cells[0].number == 42
    }

    def "should convert Ticket with multiple TicketRows to TicketDto"() {
        given:
            def ticketRow1 = new TicketRow([new TicketRowCell.NumberRowCell(42)])
            def ticketRow2 = new TicketRow([TicketRowCell.BlankRowCell.INSTANCE])
            def ticketRowDto1 = new TicketRowDto([new TicketRowCellDto.NumberRowCellDto(42)])
            def ticketRowDto2 = new TicketRowDto([TicketRowCellDto.BlankRowCellDto.INSTANCE])

            ticketRowToDtoConverter.invoke(ticketRow1) >> ticketRowDto1
            ticketRowToDtoConverter.invoke(ticketRow2) >> ticketRowDto2

            def ticket = new Ticket([ticketRow1, ticketRow2])

        when:
            def result = ticketToDtoConverter.invoke(ticket)

        then:
            result instanceof TicketDto
            result.rows.size() == 2
            result.rows[0] instanceof TicketRowDto
            result.rows[0].cells.size() == 1
            result.rows[0].cells[0] instanceof TicketRowCellDto.NumberRowCellDto
            result.rows[0].cells[0].number == 42
            result.rows[1] instanceof TicketRowDto
            result.rows[1].cells.size() == 1
            result.rows[1].cells[0] == TicketRowCellDto.BlankRowCellDto.INSTANCE
    }

    def "should return empty rows when Ticket has no TicketRows"() {
        given:
            def ticket = new Ticket([])

        when:
            def result = ticketToDtoConverter.invoke(ticket)

        then:
            result instanceof TicketDto
            result.rows.isEmpty()
    }
}