package dev.bingo.ticket.api.domain.ticket.model

data class TicketColumn(
    val numbers: List<Int>
)

data class TicketColumns(
    val columns: List<TicketColumn>
)