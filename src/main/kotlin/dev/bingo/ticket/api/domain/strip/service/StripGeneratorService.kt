package dev.bingo.ticket.api.domain.strip.service

import dev.bingo.ticket.api.domain.strip.model.AllocatedNumbers
import dev.bingo.ticket.api.domain.strip.model.Strip
import dev.bingo.ticket.api.domain.ticket.model.Ticket
import dev.bingo.ticket.api.domain.ticket.model.TicketRowCell
import dev.bingo.ticket.api.domain.ticket.service.TicketGeneratorService
import org.springframework.stereotype.Service

@Service
class StripGeneratorService(
    private val ticketGeneratorService: TicketGeneratorService
) {

    /**
     * Generates a strip of 6 Bingo tickets, ensuring that the previously allocated numbers are passed to each new ticket.
     *
     * @return A list of 6 TicketRow objects, each representing a Bingo ticket.
     */
    fun generateStrip(): Strip {
        val tickets = mutableListOf<Ticket>()
        var previouslyAllocatedNumbers = AllocatedNumbers()

        repeat(6) {
            val ticket = ticketGeneratorService.generateTicket(previouslyAllocatedNumbers)
            tickets.add(ticket)

            previouslyAllocatedNumbers = updatePreviouslyAllocatedNumbers(previouslyAllocatedNumbers, ticket)
        }

        return Strip(tickets.toList())
    }

    /**
     * Initializes the previously allocated numbers, which is a map of column index to sets of numbers.
     * This would represent the initial state before generating any tickets.
     *
     * @return A map of previously allocated numbers by column index.
     */
    private fun initializePreviouslyAllocatedNumbers(): Map<Int, Set<Int>> {
        return (0..8).associateWith { mutableSetOf() }
    }

    /**
     * Updates the previously allocated numbers by adding the new numbers generated in the current ticket.
     *
     * @param previouslyAllocatedNumbers The existing `AllocatedNumbers` containing allocated numbers by column index.
     * @param ticket The current ticket, used to extract newly generated numbers.
     * @return An updated `AllocatedNumbers` object with the new allocated numbers.
     */
    private fun updatePreviouslyAllocatedNumbers(
        previouslyAllocatedNumbers: AllocatedNumbers,
        ticket: Ticket
    ): AllocatedNumbers {
        ticket.rows.forEach { row ->
            row.cells.forEachIndexed { columnIndex, cell ->
                if (cell is TicketRowCell.NumberRowCell) {
                    previouslyAllocatedNumbers.addNumberToColumn(columnIndex, cell.number)
                }
            }
        }

        return previouslyAllocatedNumbers
    }
}