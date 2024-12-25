package dev.bingo.ticket.api.application.port.input.usecase.ticket.model

data class TicketRowDto(
    val cells: List<TicketRowCellDto>
)

sealed class TicketRowCellDto {
    data class NumberRowCellDto(val number: Int) : TicketRowCellDto()
    object BlankRowCellDto : TicketRowCellDto()
}