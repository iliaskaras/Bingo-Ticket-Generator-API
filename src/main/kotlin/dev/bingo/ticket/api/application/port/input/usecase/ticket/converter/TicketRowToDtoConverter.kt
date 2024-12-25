package dev.bingo.ticket.api.application.port.input.usecase.ticket.converter

import dev.bingo.ticket.api.application.port.input.usecase.ticket.model.TicketRowCellDto
import dev.bingo.ticket.api.application.port.input.usecase.ticket.model.TicketRowDto
import dev.bingo.ticket.api.domain.ticket.model.TicketRow
import dev.bingo.ticket.api.domain.ticket.model.TicketRowCell
import org.springframework.stereotype.Component

@Component
class TicketRowToDtoConverter : Function1<TicketRow, TicketRowDto> {

    override fun invoke(ticketRow: TicketRow): TicketRowDto =
        TicketRowDto(
            cells = ticketRow.cells.map { it.toDto() }
        )

    private fun TicketRowCell.toDto(): TicketRowCellDto =
        when (this) {
            is TicketRowCell.NumberRowCell -> TicketRowCellDto.NumberRowCellDto(this.number)
            is TicketRowCell.BlankRowCell -> TicketRowCellDto.BlankRowCellDto
        }
}