package dev.bingo.ticket.api.application.usecase.strip

import dev.bingo.ticket.api.application.port.input.usecase.strip.converter.StripToDtoConverter
import dev.bingo.ticket.api.application.port.input.usecase.strip.model.StripDto
import dev.bingo.ticket.api.domain.strip.model.Strip
import dev.bingo.ticket.api.domain.strip.service.StripGeneratorService
import dev.bingo.ticket.api.domain.ticket.model.Ticket
import spock.lang.Specification
import spock.lang.Subject
import spock.lang.Unroll

class StripsCreationUseCaseSpec extends Specification {

    def stripGeneratorService = Mock(StripGeneratorService)
    def stripToDtoConverter = Mock(StripToDtoConverter)

    @Subject
    def stripsCreationUseCase = new StripsCreationUseCaseImpl(stripGeneratorService, stripToDtoConverter)

    @Unroll
    def "execute should throw IllegalArgumentException when number is #number"() {
        when:
            stripsCreationUseCase.execute(number)

        then:
            def exception = thrown(IllegalArgumentException)
            exception.message == "The number of strips must be a positive integer."

        where:
            number << [0, -1, -10]
    }

    def "execute should generate strips and convert them to DTOs"() {
        given:
            def ticket1 = GroovyMock(Ticket)
            def ticket2 = GroovyMock(Ticket)
            def ticket3 = GroovyMock(Ticket)
            def ticket4 = GroovyMock(Ticket)
            def ticket5 = GroovyMock(Ticket)
            def ticket6 = GroovyMock(Ticket)

            def strip1 = new Strip([ticket1, ticket2, ticket3, ticket4, ticket5, ticket6])
            def strip2 = new Strip([ticket1, ticket2, ticket3, ticket4, ticket5, ticket6])
            def stripDto1 = GroovyMock(StripDto)
            def stripDto2 = GroovyMock(StripDto)

        and:
            def number = 2

        and:
            def expectedResult = [stripDto1, stripDto2]

        when:
            def result = stripsCreationUseCase.execute(number)

        then:
            number * stripGeneratorService.generateStrip() >>> [strip1, strip2]
            1 * stripToDtoConverter.invoke(strip1) >> stripDto1
            1 * stripToDtoConverter.invoke(strip2) >> stripDto2

        and:
            result == expectedResult
    }

    def "execute should throw IllegalArgumentException when strip tickets is not 6"() {
        given:
            def ticket1 = GroovyMock(Ticket)
            def ticket2 = GroovyMock(Ticket)

            def strip = new Strip([ticket1, ticket2])

        when:
            stripsCreationUseCase.execute(1)

        then:
            1 * stripGeneratorService.generateStrip() >>> [strip]
            def exception = thrown(IllegalArgumentException)
            exception.message == "The number of strip's tickets must be 6."
    }
}