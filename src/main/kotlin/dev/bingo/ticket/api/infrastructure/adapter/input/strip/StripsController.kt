package dev.bingo.ticket.api.infrastructure.adapter.input.strip

import dev.bingo.ticket.api.application.port.input.usecase.strip.StripsCreationUseCase
import dev.bingo.ticket.api.application.port.input.usecase.strip.model.StripDto
import dev.bingo.ticket.api.infrastructure.adapter.input.strip.request.BulkStripsCreationRequest
import dev.bingo.ticket.api.infrastructure.adapter.input.strip.response.CreateStripsResponse
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import jakarta.validation.Valid
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.RequestBody

@RestController
@RequestMapping("/api/v1/strips")
@Validated
class StripsController(
    private val stripsCreationUseCase: StripsCreationUseCase
) {
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
        summary = "Create bulk strips",
        description = "Generates a bulk of strips based on the provided number.",
        responses = [
            ApiResponse(
                responseCode = "201",
                description = "Strips successfully created",
                content = [
                    Content(
                        mediaType = "application/json",
                        schema = Schema(implementation = Array<StripDto>::class)
                    )
                ]
            ),
            ApiResponse(
                responseCode = "400",
                description = "Invalid input",
                content = [
                    Content(mediaType = "application/json")
                ]
            )
        ]
    )
    fun createStrips(@Valid @RequestBody request: BulkStripsCreationRequest): CreateStripsResponse =
        CreateStripsResponse(stripsCreationUseCase.execute(request.number))
}