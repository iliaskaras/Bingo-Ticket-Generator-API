package dev.bingo.ticket.api.infrastructure.adapter.input.strip.response

import dev.bingo.ticket.api.application.port.input.usecase.strip.model.StripDto
import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "Response for bulk strips creation")
data class CreateStripsResponse(
    @Schema(description = "List of generated strips")
    val strips: List<StripDto>
)
