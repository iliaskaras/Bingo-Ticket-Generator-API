package dev.bingo.ticket.api.domain.strip.model

import dev.bingo.ticket.api.domain.ticket.model.Ticket

data class Strip(
    val tickets: List<Ticket>
)