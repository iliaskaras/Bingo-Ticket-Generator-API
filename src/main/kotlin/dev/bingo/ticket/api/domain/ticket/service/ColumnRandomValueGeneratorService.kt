package dev.bingo.ticket.api.domain.ticket.service

import dev.bingo.ticket.api.domain.ticket.model.TicketColumnEnum
import dev.bingo.ticket.api.domain.ticket.model.TicketColumn
import dev.bingo.ticket.api.domain.ticket.model.TicketColumns
import org.springframework.stereotype.Service

/**
 * Service responsible for generating random column values for a Bingo ticket while ensuring adherence
 * to Bingo rules and preventing duplicate allocations within the same column across tickets.
 *
 * Key Rules:
 * - Each column must have between `MIN_VALUES_PER_COLUMN` and `MAX_VALUES_PER_COLUMN` numbers.
 * - Exactly `TOTAL_NUMBERS_TO_GENERATE` numbers are distributed across the ticket columns.
 * - Previously allocated numbers are excluded from future allocations in the same column.
 */
@Service
class ColumnRandomValueGeneratorService {

    companion object {
        /**
         * The minimum and maximum number of values a column can have.
         */
        const val MIN_VALUES_PER_COLUMN = 1
        const val MAX_VALUES_PER_COLUMN = 3

        /**
         * The total number of numbers to allocate across all columns in a single Bingo ticket.
         */
        const val TOTAL_NUMBERS_TO_GENERATE = 15
    }

    /**
     * Generates column values for a single Bingo ticket, ensuring no overlap with
     * previously allocated numbers for the same column.
     *
     * @param previouslyAllocatedNumbers A map where keys are column indices (0-8) and values
     *        are sets of numbers already used in that column.
     * @return A `TicketColumns` object containing the new ticket's column values.
     */
    fun generateColumnValues(previouslyAllocatedNumbers: Map<Int, Set<Int>>): TicketColumns {
        val allColumnRanges = TicketColumnEnum.allRanges()

        val columnAllocations = allocateNumbersAcrossColumns(previouslyAllocatedNumbers)

        val columns = allColumnRanges.mapIndexed { columnIndex, range ->
            val availableNumbers = getAvailableForAllocationColumnNumbers(
                range, previouslyAllocatedNumbers, columnIndex
            )
            val allocatedNumbers = allocateNumbersForColumn(availableNumbers, columnAllocations[columnIndex])
            TicketColumn(allocatedNumbers)
        }

        return TicketColumns(columns)
    }

    /**
     * Distributes numbers across columns, ensuring that each column adheres to constraints.
     *
     * Handles the final ticket allocation differently to ensure exact compliance with
     * the total number of required numbers.
     *
     * @param previouslyAllocatedNumbers Map of column indices to their allocated numbers.
     * @return List of numbers to allocate per column.
     */
    private fun allocateNumbersAcrossColumns(previouslyAllocatedNumbers: Map<Int, Set<Int>>): List<Int> {
        val columnCount = TicketColumnEnum.entries.size
        val isLastTicketGeneration = isLastIteration(previouslyAllocatedNumbers)

        val columnAllocations = if (isLastTicketGeneration) {
            allocateForLastTicket(previouslyAllocatedNumbers)
        } else {
            initializeColumnAllocations(columnCount, previouslyAllocatedNumbers)
        }.toMutableList()

        var remainingNumbersToAllocate = TOTAL_NUMBERS_TO_GENERATE - columnAllocations.sum()

        while (remainingNumbersToAllocate > 0) {
            val columnIndex = (0 until columnCount).random()
            val columnRange = TicketColumnEnum.entries[columnIndex].valuesRange

            val availableNumbers = getAvailableForAllocationColumnNumbers(columnRange, previouslyAllocatedNumbers, columnIndex)

            if (canAllocateToColumn(columnAllocations, columnIndex, availableNumbers.size)) {
                columnAllocations[columnIndex]++
                remainingNumbersToAllocate--
            }
        }

        return columnAllocations
    }

    /**
     * Allocates numbers specifically for the last ticket, ensuring fair distribution
     * while adhering to constraints.
     */
    private fun allocateForLastTicket(previouslyAllocatedNumbers: Map<Int, Set<Int>>): List<Int> {
        val remainingNumbersToAllocate = TOTAL_NUMBERS_TO_GENERATE
        val remainingAvailableNumbersPerColumn = TicketColumnEnum.entries.mapIndexed { index, column ->
            column.valuesRange.count() - (previouslyAllocatedNumbers[index]?.size ?: 0)
        }

        return remainingAvailableNumbersPerColumn.map { availableNumbers ->
            minOf(availableNumbers, MAX_VALUES_PER_COLUMN)
        }.let { allocations ->
            val totalAllocated = allocations.sum()
            if (totalAllocated < remainingNumbersToAllocate) {
                distributeRemainingNumbers(allocations, remainingNumbersToAllocate - totalAllocated)
            } else {
                allocations
            }
        }
    }

    /**
     * Distributes any remaining numbers fairly across columns, respecting the maximum constraint.
     */
    private fun distributeRemainingNumbers(allocations: List<Int>, remaining: Int): List<Int> {
        val mutableAllocations = allocations.toMutableList()
        var remainingNumbers = remaining

        while (remainingNumbers > 0) {
            for (i in mutableAllocations.indices) {
                if (mutableAllocations[i] < MAX_VALUES_PER_COLUMN && remainingNumbers > 0) {
                    mutableAllocations[i]++
                    remainingNumbers--
                }
            }
        }

        return mutableAllocations
    }

    /**
     * Initializes column allocations by assigning initial values based on the number of available numbers.
     *
     * @param columnCount Total number of columns.
     * @param previouslyAllocatedNumbers Map of column indices to their allocated numbers.
     * @return List of initial allocations per column.
     */
    private fun initializeColumnAllocations(
        columnCount: Int,
        previouslyAllocatedNumbers: Map<Int, Set<Int>>
    ): MutableList<Int> {
        return MutableList(columnCount) { columnIndex ->
            val columnRange = TicketColumnEnum.entries[columnIndex].valuesRange
            val totalPreviouslyAllocatedNumbers = previouslyAllocatedNumbers[columnIndex]?.size ?: 0
            val remainingAvailableNumbers = columnRange.count() - totalPreviouslyAllocatedNumbers

            if (remainingAvailableNumbers <= 0) {
                0
            } else {
                (1..minOf(remainingAvailableNumbers, MAX_VALUES_PER_COLUMN)).random()
            }
        }
    }

    /**
     * Checks if the current allocation is for the last ticket generation.
     *
     * @param previouslyAllocatedNumbers Map of column indices to their allocated numbers.
     * @return True if this is the last ticket to be generated, false otherwise.
     */
    private fun isLastIteration(previouslyAllocatedNumbers: Map<Int, Set<Int>>): Boolean {
        val totalAllocatedNumbers = previouslyAllocatedNumbers.values.sumOf { it.size }
        return (90 - totalAllocatedNumbers) <= TOTAL_NUMBERS_TO_GENERATE
    }

    /**
     * Verifies if more numbers can be allocated to a specific column.
     */
    private fun canAllocateToColumn(
        columnAllocations: List<Int>,
        columnIndex: Int,
        availableNumbersSize: Int
    ): Boolean {
        val currentAllocation = columnAllocations[columnIndex]
        return currentAllocation < MAX_VALUES_PER_COLUMN && availableNumbersSize > 0
    }

    /**
     * Filters out numbers that have already been allocated from the range of available numbers for a column.
     */
    private fun getAvailableForAllocationColumnNumbers(
        columnRange: IntRange,
        previouslyAllocatedNumbers: Map<Int, Set<Int>>,
        columnIndex: Int
    ): List<Int> {
        return columnRange.filterNot { previouslyAllocatedNumbers[columnIndex]?.contains(it) == true }
    }

    /**
     * Randomly selects the specified number of values from the available numbers for a column.
     *
     * @param availableNumbers List of numbers available for allocation.
     * @param count Number of values to allocate.
     * @throws IllegalStateException If there are not enough available numbers to meet the allocation.
     * @return List of allocated numbers.
     */
    private fun allocateNumbersForColumn(availableNumbers: List<Int>, count: Int): List<Int> {
        if (availableNumbers.size < count) {
            throw IllegalStateException("Not enough numbers available for column to allocate $count values.")
        }
        return availableNumbers.shuffled().take(count)
    }
}