package dev.bingo.ticket.api.domain.ticket.service

import dev.bingo.ticket.api.domain.strip.model.AllocatedNumbers
import dev.bingo.ticket.api.domain.ticket.model.ColumnAllocationTracker
import dev.bingo.ticket.api.domain.ticket.model.TicketColumn
import dev.bingo.ticket.api.domain.ticket.model.TicketColumnEnum
import dev.bingo.ticket.api.domain.ticket.model.TicketColumns
import org.springframework.stereotype.Service
import kotlin.random.Random

/**
 * Service responsible for generating random column values for a Bingo ticket, adhering to Bingo rules.
 *
 * This service ensures:
 * - No duplicate numbers within the same column across tickets.
 * - Column allocations follow the defined minimum and maximum constraints.
 * - Previously allocated numbers are excluded from future allocations.
 */
@Service
class ColumnRandomValueGeneratorService {

    companion object {
        const val MAX_VALUES_PER_COLUMN = 3
        const val TICKET_TOTAL_COLUMN_ALLOCATIONS = 15
    }

    /**
     * Generates column values for a Bingo ticket while ensuring no duplicates across tickets.
     *
     * @param previouslyAllocatedNumbers [AllocatedNumbers] representing numbers already allocated.
     * @return A [TicketColumns] object containing the generated numbers for each column.
     */
    fun generateColumnValues(previouslyAllocatedNumbers: AllocatedNumbers): TicketColumns {
        val remainingNumbersPerColumn = getAvailableNumbersPool(previouslyAllocatedNumbers)
        val columnsFullyAllocated = previouslyAllocatedNumbers.getFullyAllocatedColumns().toMutableList()
        val columnAllocationTracker = ColumnAllocationTracker(
            allocations = IntArray(9) { 0 },
            remainingNumbers = remainingNumbersPerColumn,
            skipAllocation = columnsFullyAllocated
        )

        val ticketColumns = MutableList(9) { mutableListOf<Int>() }
        var allocations = 0
        val remainingIterations = 6 - (previouslyAllocatedNumbers.getAllAllocatedNumbers().size / 15)

        while (allocations < TICKET_TOTAL_COLUMN_ALLOCATIONS) {
            val column = selectUnderpopulatedColumn(columnAllocationTracker, remainingIterations)
            allocateNumberToColumn(columnAllocationTracker, column, ticketColumns)
            allocations++
        }

        return TicketColumns(ticketColumns.map { TicketColumn(it) })
    }

    /**
     * Allocates a random number to the specified column.
     *
     * @param tracker [ColumnAllocationTracker] to track allocations and remaining numbers.
     * @param column The index of the column (0-8) to which a number will be allocated.
     * @param ticketColumns Mutable list representing the ticket's columns and their allocated numbers.
     * @throws IllegalStateException If there are no numbers left to allocate.
     */
    private fun allocateNumberToColumn(
        tracker: ColumnAllocationTracker,
        column: Int,
        ticketColumns: MutableList<MutableList<Int>>
    ) {
        require(column in 0..8) { "Invalid column index: $column. Must be between 0 and 8." }

        val remainingNumbers = tracker.remainingNumbers[column]
        if (remainingNumbers.isEmpty() || tracker.allocations[column] >= MAX_VALUES_PER_COLUMN) {
            throw IllegalStateException("No remaining numbers or column $column is fully allocated.")
        }

        val number = remainingNumbers.removeAt(Random.nextInt(remainingNumbers.size))
        ticketColumns[column].add(number)
        tracker.allocations[column]++

        if (tracker.allocations[column] == MAX_VALUES_PER_COLUMN) {
            tracker.skipAllocation.add(column)
        }
    }

    /**
     * Creates a pool of available numbers for each column based on column ranges and allocations.
     *
     * @param previouslyAllocatedNumbers [AllocatedNumbers] object containing previously allocated numbers.
     * @return A list of mutable lists, where each list contains numbers available for allocation per column.
     */
    private fun getAvailableNumbersPool(previouslyAllocatedNumbers: AllocatedNumbers): List<MutableList<Int>> {
        val columnRanges = TicketColumnEnum.allRanges()
        val initialPool = columnRanges.map { it.toMutableList() }
        previouslyAllocatedNumbers.columnNumbers.forEach { column ->
            column.allocatedNumbers.forEach { number ->
                initialPool[column.columnIndex].remove(number)
            }
        }
        return initialPool
    }

    /**
     * Selects a column for allocation based on underpopulation and remaining iterations.
     *
     * @param tracker [ColumnAllocationTracker] tracking allocations and available numbers.
     * @param remainingIterations Remaining iterations for allocation.
     * @return The index of the selected column (0-8).
     * @throws IllegalStateException If no valid column is available for allocation.
     */
    private fun selectUnderpopulatedColumn(
        tracker: ColumnAllocationTracker,
        remainingIterations: Int
    ): Int {
        val laggingColumns = tracker.remainingNumbers
            .mapIndexed { index, sublist -> index to sublist.size }
            .filter { it.second >= (remainingIterations * MAX_VALUES_PER_COLUMN - 2) }
            .map { it.first }

        val underpopulatedColumns = getUnderpopulatedColumns(tracker)
            .filterNot { it in tracker.skipAllocation }

        val viableColumns = (laggingColumns.ifEmpty { underpopulatedColumns })
            .filter { tracker.remainingNumbers[it].isNotEmpty() }

        return viableColumns.randomOrNull() ?: throw IllegalStateException("No valid columns left for allocation.")
    }

    /**
     * Identifies underpopulated columns that are not fully allocated.
     *
     * @param tracker [ColumnAllocationTracker] tracking allocation counts and skipped columns.
     * @return A list of column indexes that are underpopulated.
     */
    private fun getUnderpopulatedColumns(tracker: ColumnAllocationTracker): List<Int> {
        return (0..8).filter { tracker.allocations[it] < MAX_VALUES_PER_COLUMN }
    }
}