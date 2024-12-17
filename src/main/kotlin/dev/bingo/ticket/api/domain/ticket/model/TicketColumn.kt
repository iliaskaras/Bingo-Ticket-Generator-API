package dev.bingo.ticket.api.domain.ticket.model

data class TicketColumn(
    val numbers: List<Int>
)

data class TicketColumns(
    val columns: List<TicketColumn>
)

data class ColumnAllocationTracker(
    val allocations: IntArray = IntArray(9) { 0 },
    val remainingNumbers: Array<MutableList<Int>>
)