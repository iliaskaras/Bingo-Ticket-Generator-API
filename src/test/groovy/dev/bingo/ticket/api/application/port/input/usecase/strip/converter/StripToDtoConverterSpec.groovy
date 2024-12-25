package dev.bingo.ticket.api.application.port.input.usecase.strip.converter

import dev.bingo.ticket.api.application.port.input.usecase.ticket.converter.TicketToDtoConverter
import dev.bingo.ticket.api.application.port.input.usecase.ticket.model.TicketDto
import dev.bingo.ticket.api.domain.strip.model.Strip
import dev.bingo.ticket.api.domain.ticket.model.Ticket
import spock.lang.Specification
import spock.lang.Subject

class StripToDtoConverterSpec extends Specification {

    def ticketToDtoConverter = Mock(TicketToDtoConverter)

    @Subject
    def stripToDtoConverter = new StripToDtoConverter(ticketToDtoConverter)

    def "should convert Strip with 6 tickets into StripDto"() {
        given: "A Strip with tickets"
            def tickets = (1..6).collect { GroovyMock(Ticket) }
            def strip = new Strip(tickets)

        and:
            def ticketDtos = (1..6).collect { GroovyMock(TicketDto) }

            6.times { index ->
                ticketToDtoConverter.invoke(tickets[index]) >> ticketDtos[index]
            }

        when: "The converter is invoked"
            def result = stripToDtoConverter.invoke(strip)

        then:
            result != null
            result.tickets.size() == 6
            result.tickets == ticketDtos
    }

    def "should throw exception if Strip is null"() {
        when: "A null Strip is passed to the converter"
            stripToDtoConverter.invoke(null)

        then: "An exception is thrown"
            thrown(NullPointerException)
    }
}