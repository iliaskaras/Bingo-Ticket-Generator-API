package dev.bingo.ticket.api.domain.ticket.model

data class TicketRow(
    val cells: List<TicketRowCell>
)

sealed class TicketRowCell {
    data class NumberRowCell(val number: Int) : TicketRowCell()
    object BlankRowCell : TicketRowCell()
}