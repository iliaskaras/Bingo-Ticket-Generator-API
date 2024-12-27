package dev.bingo.ticket.api.infrastructure.adapter.input.strip

import dev.bingo.ticket.api.application.port.input.usecase.strip.StripsCreationUseCase
import dev.bingo.ticket.api.application.port.input.usecase.strip.model.StripDto
import dev.bingo.ticket.api.infrastructure.config.exception.GlobalExceptionHandler
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.HttpStatus
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.MvcResult
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import spock.lang.Specification

@WebMvcTest(StripsController)
class StripsControllerSpec extends Specification {

    private MockMvc mockMvc
    private StripsCreationUseCase stripsCreationUseCase = Mock()

    def setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(new StripsController(stripsCreationUseCase))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build()
    }

    def "createStrips should return 201 when valid input is provided"() {
        given:
            def number = 5
            def stripDto = new StripDto([])
            def strips = [stripDto] * number

            def requestBody = """
            {
                "number": $number
            }
            """

        when:
            MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/strips")
                .contentType("application/json")
                .content(requestBody))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andReturn()

        then:
            1 * stripsCreationUseCase.execute(number) >> strips

        and:
            result.response.contentAsString == '{"strips":[' + strips.collect { '{"tickets":[]}' }.join(',') + ']}'
    }

    def "createStrips should return 400 when negative number is provided"() {
        given:
            def requestBody = """
            {
                "number": -1
            }
            """

        when:
            MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/strips")
                    .contentType("application/json")
                    .content(requestBody))
                    .andExpect(MockMvcResultMatchers.status().isBadRequest())
                    .andReturn()

        then:
            result.response.status == HttpStatus.BAD_REQUEST.value()

            def responseContent = result.response.contentAsString
            responseContent.contains('"error":"Validation failed"')
            responseContent.contains('"field":"number"')
            responseContent.contains('"rejectedValue":-1')
            responseContent.contains('"message":"Number of strips must be a positive integer."')
    }

    def "createStrips should return 400 when number is provided as a number above the maximum allowed"() {
        given:
            def requestBody = """
                {
                    "number": 10001
                }
                """

        when:
            MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/strips")
                    .contentType("application/json")
                    .content(requestBody))
                    .andExpect(MockMvcResultMatchers.status().isBadRequest())
                    .andReturn()

        then:
            result.response.status == HttpStatus.BAD_REQUEST.value()

            def responseContent = result.response.contentAsString
            responseContent.contains('"error":"Validation failed"')
            responseContent.contains('"field":"number"')
            responseContent.contains('"rejectedValue":10001')
            responseContent.contains('"message":"Number of strips cannot exceed 10,000."')
    }

    def "createStrips should return 400 when number is missing"() {
        given:
            def requestBody = """
            {}
            """

        when:
            MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/strips")
                .contentType("application/json")
                .content(requestBody))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andReturn()

        then:
            def responseContent = result.response.contentAsString
            responseContent.contains('"error":"Validation failed"')
            responseContent.contains('"field":"number"')
            responseContent.contains('"rejectedValue":0')
            responseContent.contains('"message":"Number of strips must be a positive integer."')
    }
}