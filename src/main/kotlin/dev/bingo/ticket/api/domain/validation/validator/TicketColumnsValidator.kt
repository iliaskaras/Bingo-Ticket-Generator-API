package dev.bingo.ticket.api.domain.validation.validator

import dev.bingo.ticket.api.domain.strip.model.AllocatedNumbers
import dev.bingo.ticket.api.domain.ticket.model.TicketColumnEnum
import dev.bingo.ticket.api.domain.ticket.model.TicketColumns
import org.springframework.stereotype.Component

/**
 * A validator that checks newly generated Bingo ticket columns to ensure they comply with Bingo rules.
 * This includes verifying the number of numbers per column, range constraints, uniqueness, and consistency
 * with previously allocated numbers.
 */
@Component
class TicketColumnsValidator : Function2<AllocatedNumbers, TicketColumns, Unit> {

    companion object {
        /**
         * The minimum number of numbers allowed in a single column.
         */
        const val MIN_VALUES_PER_COLUMN = 1

        /**
         * The maximum number of numbers allowed in a single column.
         */
        const val MAX_VALUES_PER_COLUMN = 3
    }

    /**
     * Validates newly generated Bingo ticket columns against multiple rules:
     * - Ensures exactly 15 numbers are generated across all columns.
     * - Prevents overlap with previously allocated numbers.
     * - Confirms that column values are within the correct range for their index.
     * - Ensures no duplicate values exist within a column.
     * - Checks that each column has an allowable number of allocations.
     *
     * @param previouslyAllocatedNumbers A record of numbers that were already allocated.
     * @param ticketColumns The newly generated Bingo ticket columns to validate.
     * @throws IllegalStateException If any validation rule is violated.
     */
    override fun invoke(previouslyAllocatedNumbers: AllocatedNumbers, ticketColumns: TicketColumns) {
        val allGeneratedNumbers = ticketColumns.columns.flatMap { it.numbers }

        newColumnNumbersSizeValidation(allGeneratedNumbers)
        newColumnNumbersOverlapWithPreviousValidation(previouslyAllocatedNumbers, allGeneratedNumbers)
        validateColumnRanges(ticketColumns)
        validateUniqueNumbersPerColumn(ticketColumns)
        validateColumnAllocations(ticketColumns)
    }

    /**
     * Validates that the total number of generated numbers is exactly 15.
     *
     * @param allGeneratedNumbers A list of all numbers generated across all columns.
     * @throws IllegalStateException If the total number of generated numbers is not 15.
     */
    private fun newColumnNumbersSizeValidation(allGeneratedNumbers: List<Int>) {
        if (allGeneratedNumbers.size != 15) {
            throw IllegalStateException("Invalid number of generated numbers. Expected 15, but found ${allGeneratedNumbers.size}.")
        }
    }

    /**
     * Ensures that newly generated numbers do not overlap with previously allocated numbers.
     *
     * @param previouslyAllocatedNumbers The previously allocated numbers, grouped by column index.
     * @param allGeneratedNumbers A list of all newly generated numbers across columns.
     * @throws IllegalStateException If any of the newly generated numbers overlap with previously allocated numbers.
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

    /**
     * Validates that all numbers in each column fall within the allowed range for their respective column.
     *
     * @param ticketColumns The Bingo ticket columns to validate.
     * @throws IllegalStateException If any number falls outside the valid range for its column.
     */
    private fun validateColumnRanges(ticketColumns: TicketColumns) {
        ticketColumns.columns.forEachIndexed { columnIndex, column ->
            val validRange = TicketColumnEnum.getByIndex(columnIndex).valuesRange
            val invalidNumbers = column.numbers.filterNot { it in validRange }

            if (invalidNumbers.isNotEmpty()) {
                throw IllegalStateException("Column ${columnIndex + 1} contains invalid numbers: $invalidNumbers. Valid range: ${validRange.first}–${validRange.last}")
            }
        }
    }

    /**
     * Ensures that there are no duplicate numbers within each column.
     *
     * @param ticketColumns The Bingo ticket columns to validate.
     * @throws IllegalStateException If any column contains duplicate numbers.
     */
    private fun validateUniqueNumbersPerColumn(ticketColumns: TicketColumns) {
        ticketColumns.columns.forEachIndexed { columnIndex, column ->
            val duplicates = column.numbers.groupBy { it }
                .filter { it.value.size > 1 }
                .keys
            if (duplicates.isNotEmpty()) {
                throw IllegalStateException("Column ${columnIndex + 1} contains duplicate numbers: $duplicates")
            }
        }
    }

    /**
     * Validates that the number of numbers allocated in each column falls within the allowed range.
     *
     * @param ticketColumns The Bingo ticket columns to validate.
     * @throws IllegalStateException If any column contains fewer than the minimum or more than the maximum allowed numbers.
     */
    private fun validateColumnAllocations(ticketColumns: TicketColumns) {
        ticketColumns.columns.forEachIndexed { columnIndex, column ->
            val numberOfAllocations = column.numbers.size
            if (numberOfAllocations == 0)
                return@forEachIndexed
            if (numberOfAllocations > MAX_VALUES_PER_COLUMN) {
                throw IllegalStateException("Column ${columnIndex + 1} has an invalid number of allocations. Expected between $MIN_VALUES_PER_COLUMN and $MAX_VALUES_PER_COLUMN, but found $numberOfAllocations.")
            }
        }
    }
}