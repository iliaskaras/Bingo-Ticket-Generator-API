package dev.bingo.ticket.api.application.usecase.strip

import dev.bingo.ticket.api.application.port.input.usecase.strip.StripsCreationUseCase
import dev.bingo.ticket.api.application.port.input.usecase.strip.converter.StripToDtoConverter
import dev.bingo.ticket.api.application.port.input.usecase.strip.model.StripDto
import dev.bingo.ticket.api.domain.strip.service.StripGeneratorService
import org.springframework.stereotype.Service

@Service
class StripsCreationUseCaseImpl(
    private val stripGeneratorService: StripGeneratorService,
    private val stripToDtoConverter: StripToDtoConverter,
) : StripsCreationUseCase {

    override fun execute(number: Int): List<StripDto> {
        require(number > 0) { "The number of strips must be a positive integer." }

        return (1..number).map {
            val strip = stripGeneratorService.generateStrip()

            require(strip.tickets.size == 6) { "The number of strip's tickets must be 6." }

            stripToDtoConverter.invoke(strip)
        }
    }
}