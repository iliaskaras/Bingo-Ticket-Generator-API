package dev.bingo.ticket.api.application.port.input.usecase.strip.converter

import dev.bingo.ticket.api.application.port.input.usecase.strip.model.StripDto
import dev.bingo.ticket.api.application.port.input.usecase.ticket.converter.TicketToDtoConverter
import dev.bingo.ticket.api.domain.strip.model.Strip
import org.springframework.stereotype.Component

@Component
class StripToDtoConverter(
    private val ticketToDtoConverter: TicketToDtoConverter
) : Function1<Strip, StripDto> {

    override fun invoke(strip: Strip): StripDto {
        val tickets = strip.tickets.map { ticketToDtoConverter.invoke(it) }

        return StripDto(tickets)
    }
}