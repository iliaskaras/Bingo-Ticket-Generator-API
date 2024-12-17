package dev.bingo.ticket.api.domain.ticket.service

import dev.bingo.ticket.api.domain.strip.model.AllocatedNumbers
import dev.bingo.ticket.api.domain.ticket.model.ColumnAllocationTracker
import dev.bingo.ticket.api.domain.ticket.model.TicketColumn
import dev.bingo.ticket.api.domain.ticket.model.TicketColumnEnum
import dev.bingo.ticket.api.domain.ticket.model.TicketColumns
import org.springframework.stereotype.Service
import kotlin.random.Random

/**
 * Service responsible for generating random column values for a Bingo ticket while ensuring adherence
 * to Bingo rules and preventing duplicate allocations within the same column across tickets.
 *
 * Key Rules:
 * - Each column must have between [MIN_VALUES_PER_COLUMN] and [MAX_VALUES_PER_COLUMN] numbers.
 * - Exactly [TICKET_TOTAL_COLUMN_ALLOCATIONS] numbers are distributed across the ticket columns.
 * - Previously allocated numbers are excluded from future allocations in the same column.
 */
@Service
class ColumnRandomValueGeneratorService {

    companion object {
        /**
         * The minimum number of values a column can have.
         */
        const val MIN_VALUES_PER_COLUMN = 1

        /**
         * The maximum number of values a column can have.
         */
        const val MAX_VALUES_PER_COLUMN = 3

        /**
         * The total number of column allocations for a single Bingo ticket.
         */
        const val TICKET_TOTAL_COLUMN_ALLOCATIONS = 15
    }

    /**
     * Generates column values for a single Bingo ticket while ensuring that previously allocated numbers
     * are not reused. Each column should receive a number of allocations between [MIN_VALUES_PER_COLUMN]
     * and [MAX_VALUES_PER_COLUMN].
     *
     * @param previouslyAllocatedNumbers An [AllocatedNumbers] object that holds the already
     *        allocated numbers for each column, to avoid reallocation.
     * @return A list of [TicketColumns] representing the generated Bingo tickets.
     */
    fun generateColumnValues(previouslyAllocatedNumbers: AllocatedNumbers): TicketColumns {
        val remainingNumbersPerColumn = getAvailableNumbersPool(previouslyAllocatedNumbers)
        val columnAllocationTracker = ColumnAllocationTracker(
            allocations = IntArray(9) { 0 },
            remainingNumbers = remainingNumbersPerColumn
        )

        // List to hold the numbers for each column of the current ticket.
        val ticketColumns = MutableList(9) { mutableListOf<Int>() }

        repeat(TICKET_TOTAL_COLUMN_ALLOCATIONS) {
            val column = selectColumn(columnAllocationTracker, remainingNumbersPerColumn)
            allocateNumberToColumn(columnAllocationTracker, column, ticketColumns)
        }

        return TicketColumns(ticketColumns.map { TicketColumn(it) })
    }

    /**
     * Allocates a number to the specified column in the ticket, ensuring the number is removed from
     * the pool of available numbers for that column and updating the column's allocation count.
     *
     * @param tracker A [ColumnAllocationTracker] object that holds allocation counts and remaining numbers.
     * @param column The index of the column (0 to 8) to which the number will be allocated.
     * @param ticketColumns A list of lists representing the ticket's columns and their allocated numbers.
     * @throws IllegalStateException If there are no remaining numbers in the selected column.
     * @throws IllegalArgumentException If the column index is invalid.
     */
    private fun allocateNumberToColumn(
        tracker: ColumnAllocationTracker,
        column: Int,
        ticketColumns: MutableList<MutableList<Int>>,
    ) {
        require(column in 0..8) { "Invalid column index: $column. Must be between 0 and 8." }

        if (tracker.remainingNumbers[column].isNotEmpty()) {
            val randomIndex = Random.nextInt(tracker.remainingNumbers[column].size)
            val number = tracker.remainingNumbers[column].removeAt(randomIndex)

            ticketColumns[column].add(number)
            tracker.allocations[column]++
        } else {
            throw IllegalStateException("No remaining numbers in column $column to allocate.")
        }
    }

    /**
     * Initializes the pool of numbers available for allocation for each column, based on the ranges
     * defined in [TicketColumnEnum], and removes any numbers that were previously allocated.
     *
     * @param previouslyAllocatedNumbers An [AllocatedNumbers] object containing numbers already allocated.
     * @return An array of mutable lists, where each list contains the numbers available for each column.
     */
    private fun getAvailableNumbersPool(previouslyAllocatedNumbers: AllocatedNumbers): Array<MutableList<Int>> {
        val columnRanges = TicketColumnEnum.allRanges()
        val initialPool = initializeFullPool(columnRanges)
        return removePreviouslyAllocatedNumbers(initialPool, previouslyAllocatedNumbers)
    }

    /**
     * Creates a pool of all possible numbers for each column based on their defined ranges.
     *
     * @param columnRanges A list of [IntRange] objects representing the range of numbers for each column.
     * @return An array of mutable lists, where each list contains all possible numbers for a column.
     */
    private fun initializeFullPool(columnRanges: List<IntRange>): Array<MutableList<Int>> =
        Array(9) { index -> columnRanges[index].toMutableList() }

    /**
     * Removes numbers that were previously allocated from the pool of available numbers for each column.
     *
     * @param pool The initial pool of numbers for each column.
     * @param previouslyAllocatedNumbers An [AllocatedNumbers] object containing numbers already allocated.
     * @return The updated pool with previously allocated numbers removed.
     */
    private fun removePreviouslyAllocatedNumbers(
        pool: Array<MutableList<Int>>,
        previouslyAllocatedNumbers: AllocatedNumbers
    ): Array<MutableList<Int>> {
        previouslyAllocatedNumbers.columnNumbers.forEach { column ->
            column.allocatedNumbers.forEach { number ->
                pool[column.columnIndex].remove(number)
            }
        }
        return pool
    }

    /**
     * Selects a column to allocate a number from, based on the number of values already allocated
     * to each column and the availability of remaining numbers.
     *
     * @param tracker A [ColumnAllocationTracker] object tracking allocations and remaining numbers.
     * @param remainingNumbersPerColumn An array of lists representing the available numbers for each column.
     * @return The index of the selected column (0-8).
     * @throws IllegalStateException If no columns have numbers left for allocation.
     */
    private fun selectColumn(
        tracker: ColumnAllocationTracker,
        remainingNumbersPerColumn: Array<MutableList<Int>>
    ): Int {
        val underpopulatedColumns = getUnderpopulatedColumns(tracker.allocations)
        val columnsWithRemainingNumbers = filterColumnsWithRemainingNumbers(underpopulatedColumns, remainingNumbersPerColumn)

        // If there are available columns with numbers remaining, select one randomly.
        if (columnsWithRemainingNumbers.isNotEmpty()) {
            return columnsWithRemainingNumbers.random()
        }

        // If no available columns are found, try to select from columns with any remaining numbers.
        val columnsWithRemaining = (0..8).filter { remainingNumbersPerColumn[it].isNotEmpty() }
        if (columnsWithRemaining.isNotEmpty()) {
            return columnsWithRemaining.random()
        }

        throw IllegalStateException("No available columns left to allocate numbers.")
    }

    /**
     * Filters columns to include only those with remaining numbers.
     *
     * @param columnIndexes A list of column indexes to check.
     * @param remainingNumbersPerColumn An array of lists representing the available numbers for each column.
     * @return A list of column indexes with remaining numbers.
     */
    private fun filterColumnsWithRemainingNumbers(
        columnIndexes: List<Int>,
        remainingNumbersPerColumn: Array<MutableList<Int>>
    ) = columnIndexes.filter { remainingNumbersPerColumn[it].isNotEmpty() }

    /**
     * Identifies columns that are underpopulated based on their current allocation counts.
     *
     * @param ticketColumnCounts An array tracking the current allocation counts for each column.
     * @return A list of column indexes (0-8) that are underpopulated.
     */
    private fun getUnderpopulatedColumns(
        ticketColumnCounts: IntArray
    ) = (0..8).filter {
        ticketColumnCounts[it] < MAX_VALUES_PER_COLUMN
    }
}