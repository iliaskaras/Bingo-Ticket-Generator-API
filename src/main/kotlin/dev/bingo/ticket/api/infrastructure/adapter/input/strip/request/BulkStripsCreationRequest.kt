package dev.bingo.ticket.api.infrastructure.adapter.input.strip.request

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotNull

@Schema(description = "Request body for bulk strips creation")
data class BulkStripsCreationRequest(
    @field:Schema(
        description = "The number of strips to generate. Must be a positive integer and not exceed 50,000.",
        example = "1000",
        minimum = "1",
        maximum = "50000"
    )
    @field:Min(1, message = "Number of strips must be a positive integer.")
    @field:Max(50000, message = "Number of strips cannot exceed 50,000.")
    @field:NotNull(message = "Number of strips is required.")
    val number: Int
)