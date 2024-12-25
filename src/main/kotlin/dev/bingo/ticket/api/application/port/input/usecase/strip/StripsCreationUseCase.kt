package dev.bingo.ticket.api.application.port.input.usecase.strip

import dev.bingo.ticket.api.application.port.input.usecase.strip.model.StripDto

interface StripsCreationUseCase {
    /**
     * Creates strips in bulk.
     *
     * @return the strips created.
     */
    fun execute(number: Int): List<StripDto>
}