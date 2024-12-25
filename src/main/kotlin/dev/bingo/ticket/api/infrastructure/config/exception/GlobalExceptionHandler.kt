package dev.bingo.ticket.api.infrastructure.config.exception

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.context.request.WebRequest

@ControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationExceptions(
        ex: MethodArgumentNotValidException,
        request: WebRequest
    ): ResponseEntity<Any> {
        val errors = ex.bindingResult.fieldErrors
            .map { fieldError ->
                mapOf(
                    "field" to fieldError.field,
                    "rejectedValue" to fieldError.rejectedValue,
                    "message" to (fieldError.defaultMessage ?: "Invalid value")
                )
            }

        val body = mapOf(
            "error" to "Validation failed",
            "details" to errors
        )

        return ResponseEntity(body, HttpStatus.BAD_REQUEST)
    }
}