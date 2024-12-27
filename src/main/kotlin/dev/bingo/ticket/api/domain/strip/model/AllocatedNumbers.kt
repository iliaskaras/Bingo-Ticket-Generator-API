package dev.bingo.ticket.api.domain.strip.model

import dev.bingo.ticket.api.domain.ticket.model.TicketColumnEnum

/**
 * Represents the allocation of numbers for a single column in a Bingo ticket.
 *
 * @property columnIndex The index of the column (typically 0 to 8 for a Bingo ticket).
 * @property allocatedNumbers A set of unique numbers that have been allocated to this column.
 */
data class ColumnNumbers(
    val columnIndex: Int,
    val allocatedNumbers: MutableSet<Int>
)

/**
 * Represents all allocated numbers across multiple columns in a structured way.
 *
 * @property columnNumbers A list of [ColumnNumbers] objects, where each object represents
 * the numbers allocated to a specific column.
 */
data class AllocatedNumbers(
    val columnNumbers: List<ColumnNumbers> = listOf()
) {

    /**
     * Default constructor: Initializes 9 columns (indices 0 to 8) with empty allocated numbers.
     */
    constructor() : this(
        List(9) { index -> ColumnNumbers(columnIndex = index, allocatedNumbers = mutableSetOf()) }
    )

    /**
     * Adds a number to the allocated numbers for the specified column index.
     *
     * @param columnIndex The index of the column where the number will be added.
     * @param number The number to add.
     */
    fun addNumberToColumn(columnIndex: Int, number: Int) {
        columnNumbers.getOrNull(columnIndex)?.allocatedNumbers?.add(number)
            ?: throw IllegalArgumentException("Invalid column index: $columnIndex")
    }

    /**
     * Retrieves all numbers that have been allocated across all columns.
     *
     * @return A set containing all numbers allocated across all columns.
     */
    fun getAllAllocatedNumbers(): Set<Int> {
        return columnNumbers.flatMap { it.allocatedNumbers }.toSet()
    }

    /**
     * Returns the total count of all allocated numbers across all columns.
     *
     * @return The total number of allocated numbers.
     */
    fun getTotalAllocatedNumbers(): Int {
        return columnNumbers.sumOf { it.allocatedNumbers.size }
    }

    /**
     * Returns the count of allocated numbers for a specific column index.
     *
     * @param columnIndex The index of the column.
     * @return The count of allocated numbers in the specified column.
     */
    fun getAllocatedCountForColumn(columnIndex: Int): Int {
        return columnNumbers.getOrNull(columnIndex)?.allocatedNumbers?.size ?: 0
    }

    /**
     * Checks if a specific number is already allocated for a given column index.
     *
     * @param columnIndex The index of the column.
     * @param number The number to check.
     * @return `true` if the number is already allocated, otherwise `false`.
     */
    fun isNumberAlreadyAllocated(columnIndex: Int, number: Int): Boolean {
        return columnNumbers.getOrNull(columnIndex)?.allocatedNumbers?.contains(number) == true
    }

    /**
     * Retrieves the column indexes that are fully allocated.
     *
     * A column is considered fully allocated if all numbers in its range,
     * as defined in [TicketColumnEnum], are present in its allocated numbers.
     *
     * @return A list of column indexes that are fully allocated.
     */
    fun getFullyAllocatedColumns(): List<Int> {
        return columnNumbers.filter { column ->
            val expectedRange = TicketColumnEnum.getByIndex(column.columnIndex).valuesRange
            column.allocatedNumbers.containsAll(expectedRange.toList())
        }.map { it.columnIndex }
    }
}
