package dev.bingo.ticket.api.application.port.input.usecase.strip.model

import dev.bingo.ticket.api.application.port.input.usecase.ticket.model.TicketDto

data class StripDto(
    val tickets: List<TicketDto>
)
