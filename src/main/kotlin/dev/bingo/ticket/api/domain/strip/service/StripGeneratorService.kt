package dev.bingo.ticket.api.domain.strip.service

import dev.bingo.ticket.api.domain.strip.model.AllocatedNumbers
import dev.bingo.ticket.api.domain.strip.model.Strip
import dev.bingo.ticket.api.domain.ticket.model.Ticket
import dev.bingo.ticket.api.domain.ticket.model.TicketRowCell
import dev.bingo.ticket.api.domain.ticket.service.TicketGeneratorService
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class StripGeneratorService(
    private val ticketGeneratorService: TicketGeneratorService
) {

    private val logger = LoggerFactory.getLogger(StripGeneratorService::class.java)

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

        val strip = Strip(tickets.toList())

        logStrip(strip)

        return strip
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


    /**
     * Logs the generated strip in a human-readable "best bingo" format for debugging and review purposes.
     *
     * The format includes each ticket, its rows, and the cell values:
     * - Numbers are right-aligned and padded for clarity.
     * - Blank cells are represented as "XX".
     *
     * Example:
     * ```
     * Generated Strip:
     * Ticket 1:
     * Row 1:  12  XX  34  45  XX
     * Row 2:  XX  11  XX  XX  50
     * Row 3:  XX  XX  25  XX  XX
     *
     * Ticket 2:
     * Row 1:  ...
     * ```
     *
     * @param strip The Strip object containing the tickets to be logged.
     */
    private fun logStrip(strip: Strip) {
        val stripString = buildString {
            appendLine("Generated Strip:")

            strip.tickets.forEachIndexed { ticketIndex, ticket ->
                appendLine("Ticket ${ticketIndex + 1}:")

                ticket.rows.forEachIndexed { rowIndex, row ->
                    append("Row ${rowIndex + 1}: ")

                    row.cells.forEach { cell ->
                        append(
                            when (cell) {
                                is TicketRowCell.NumberRowCell -> cell.number.toString().padStart(2, ' ') + "  "
                                TicketRowCell.BlankRowCell -> "XX  "
                            }
                        )
                    }

                    appendLine()
                }

                appendLine()
            }
        }

        logger.info(stripString)
    }
}