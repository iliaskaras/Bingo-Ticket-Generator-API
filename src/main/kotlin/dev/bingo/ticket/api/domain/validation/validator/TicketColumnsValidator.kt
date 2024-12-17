package dev.bingo.ticket.api.domain.validation.validator

import dev.bingo.ticket.api.domain.strip.model.AllocatedNumbers
import dev.bingo.ticket.api.domain.ticket.model.TicketColumns
import org.springframework.stereotype.Component

@Component
class TicketColumnsValidator : Function2<AllocatedNumbers, TicketColumns, Unit> {

    /**
     * Validates the newly generated column numbers to ensure they follow Bingo rules.
     *
     * @param previouslyAllocatedNumbers Previously allocated numbers by column index.
     * @param ticketColumns The newly generated ticket columns.
     */
    override fun invoke(previouslyAllocatedNumbers: AllocatedNumbers, ticketColumns: TicketColumns) {
        val allGeneratedNumbers = ticketColumns.columns.flatMap { it.numbers }

        newColumnNumbersSizeValidation(allGeneratedNumbers)
        newColumnNumbersOverlapWithPreviousValidation(previouslyAllocatedNumbers, allGeneratedNumbers)
    }

    /**
     * Ensures exactly 15 new numbers are generated.
     *
     * @param allGeneratedNumbers A flat list of all numbers generated across columns.
     */
    private fun newColumnNumbersSizeValidation(allGeneratedNumbers: List<Int>) {
        if (allGeneratedNumbers.size != 15) {
            throw IllegalStateException("Invalid number of generated numbers. Expected 15, but found ${allGeneratedNumbers.size}.")
        }
    }

    /**
     * Ensures the newly generated numbers do not overlap with previously allocated numbers.
     *
     * @param previouslyAllocatedNumbers Previously allocated numbers by column index.
     * @param allGeneratedNumbers A flat list of all numbers generated across columns.
     */
    private fun newColumnNumbersOverlapWithPreviousValidation(
        previouslyAllocatedNumbers: AllocatedNumbers,
        allGeneratedNumbers: List<Int>
    ) {
        val previouslyAllocated = previouslyAllocatedNumbers.getAllAllocatedNumbers()
        val overlap = allGeneratedNumbers.toSet().intersect(previouslyAllocated)

        if (overlap.isNotEmpty()) {
            throw IllegalStateException("The generated numbers overlap with previously allocated numbers: $overlap")
        }
    }
}