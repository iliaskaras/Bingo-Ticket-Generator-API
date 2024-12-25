package dev.bingo.ticket.api.application.port.input.usecase.ticket.converter

import dev.bingo.ticket.api.application.port.input.usecase.ticket.model.TicketDto
import dev.bingo.ticket.api.application.port.input.usecase.ticket.model.TicketRowDto
import dev.bingo.ticket.api.domain.ticket.model.Ticket
import dev.bingo.ticket.api.domain.ticket.model.TicketRow
import org.springframework.stereotype.Component

@Component
class TicketToDtoConverter(
    private val ticketRowToDtoConverter: TicketRowToDtoConverter,
) : Function1<Ticket, TicketDto> {

    override fun invoke(ticket: Ticket): TicketDto =
        TicketDto(
            rows = ticket.rows.map { it.toDto() }
        )

    private fun TicketRow.toDto(): TicketRowDto =
        ticketRowToDtoConverter.invoke(this)
}