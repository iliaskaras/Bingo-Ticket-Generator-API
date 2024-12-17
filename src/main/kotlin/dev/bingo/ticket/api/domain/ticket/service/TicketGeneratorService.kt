package dev.bingo.ticket.api.domain.ticket.service

import dev.bingo.ticket.api.domain.strip.model.AllocatedNumbers
import dev.bingo.ticket.api.domain.ticket.model.*
import org.springframework.stereotype.Service

@Service
class TicketGeneratorService(
    private val columnRandomValueGeneratorService: ColumnRandomValueGeneratorService
) {

    /**
     * Generates a Bingo ticket based on the previously allocated numbers and ensures
     * the columns adhere to Bingo rules.
     *
     * @param previouslyAllocatedNumbers A map of column indices (0-8) to sets of numbers
     *        that have already been allocated in those columns.
     * @return A Ticket with TicketRows adhering to Bingo rules.
     */
    fun generateTicket(previouslyAllocatedNumbers: AllocatedNumbers): Ticket {
        val ticketColumns = columnRandomValueGeneratorService.generateColumnValues(previouslyAllocatedNumbers)

        validateNewColumnNumbers(previouslyAllocatedNumbers, ticketColumns)

        return Ticket(createTicketRows(ticketColumns))
    }

    /**
     * Validates the newly generated column numbers to ensure they follow Bingo rules.
     *
     * @param previouslyAllocatedNumbers Previously allocated numbers by column index.
     * @param ticketColumns The newly generated ticket columns.
     */
    private fun validateNewColumnNumbers(previouslyAllocatedNumbers: AllocatedNumbers, ticketColumns: TicketColumns) {
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

    /**
     * Creates Bingo ticket rows from the generated ticket columns.
     *
     * @param ticketColumns The generated ticket columns.
     * @return A list of 3 TicketRows adhering to Bingo rules.
     */
    private fun createTicketRows(ticketColumns: TicketColumns): List<TicketRow> {
        val rowsMap = mutableMapOf(
            0 to mutableMapOf<Int, Int>(),
            1 to mutableMapOf(),
            2 to mutableMapOf()
        )

        val (columnsWith3Values, columnsWith2Values, columnsWith1Value) = groupColumnsByTotalGeneratedNumbers(ticketColumns)

        assignColumnValuesToRows(rowsMap, columnsWith3Values, columnsWith2Values, columnsWith1Value)

        return constructTicketRows(rowsMap)
    }

    /**
     * Constructs TicketRows from a map of row indices to column-indexed numbers.
     *
     * @param rowsMap A map where the key is the row index and the value is a map of
     *        column indices to numbers.
     * @return A list of 3 TicketRows with numbers and blanks.
     */
    private fun constructTicketRows(rowsMap: MutableMap<Int, MutableMap<Int, Int>>): List<TicketRow> =
        rowsMap.map { (_, rowData) ->
            val cells = (0 until 9).map { columnIndex ->
                if (rowData.containsKey(columnIndex)) {
                    TicketRowCell.NumberRowCell(rowData[columnIndex]!!)
                } else {
                    TicketRowCell.BlankRowCell
                }
            }
            TicketRow(cells)
        }

    /**
     * Assigns column values to rows based on the number of values in each column.
     *
     * @param rowsMap A map where the key is the row index and the value is a map of
     *        column indices to numbers.
     * @param columnsWith3Values Columns containing 3 numbers.
     * @param columnsWith2Values Columns containing 2 numbers.
     * @param columnsWith1Value Columns containing 1 number.
     */
    private fun assignColumnValuesToRows(
        rowsMap: MutableMap<Int, MutableMap<Int, Int>>,
        columnsWith3Values: List<IndexedValue<TicketColumn>>,
        columnsWith2Values: List<IndexedValue<TicketColumn>>,
        columnsWith1Value: List<IndexedValue<TicketColumn>>
    ) {
        assign3ValueColumnsToRows(columnsWith3Values, rowsMap)
        assign2ValueColumnsToRows(columnsWith2Values, rowsMap)
        assign1ValueColumnsToRows(columnsWith1Value, rowsMap)
    }

    /**
     * Assigns 3-value columns directly to rows in order.
     *
     * @param columnsWith3Values Columns containing 3 numbers.
     * @param rowsMap A map where the key is the row index and the value is a map of
     *        column indices to numbers.
     */
    private fun assign3ValueColumnsToRows(
        columnsWith3Values: List<IndexedValue<TicketColumn>>,
        rowsMap: MutableMap<Int, MutableMap<Int, Int>>
    ) {
        columnsWith3Values.forEach { (columnIndex, column) ->
            rowsMap[0]!![columnIndex] = column.numbers[0]
            rowsMap[1]!![columnIndex] = column.numbers[1]
            rowsMap[2]!![columnIndex] = column.numbers[2]
        }
    }

    /**
     * Randomly assigns 2-value columns to two distinct rows.
     *
     * @param columnsWith2Values Columns containing 2 numbers.
     * @param rowsMap A map where the key is the row index and the value is a map of
     *        column indices to numbers.
     */
    private fun assign2ValueColumnsToRows(
        columnsWith2Values: List<IndexedValue<TicketColumn>>,
        rowsMap: MutableMap<Int, MutableMap<Int, Int>>
    ) {
        columnsWith2Values.forEach { (columnIndex, column) ->
            val rows = (0..2).shuffled() // Shuffle rows to assign values randomly
            rowsMap[rows[0]]!![columnIndex] = column.numbers[0]
            rowsMap[rows[1]]!![columnIndex] = column.numbers[1]
        }
    }

    /**
     * Randomly assigns 1-value columns to one of the rows.
     *
     * @param columnsWith1Value Columns containing 1 number.
     * @param rowsMap A map where the key is the row index and the value is a map of
     *        column indices to numbers.
     */
    private fun assign1ValueColumnsToRows(
        columnsWith1Value: List<IndexedValue<TicketColumn>>,
        rowsMap: MutableMap<Int, MutableMap<Int, Int>>
    ) {
        columnsWith1Value.forEach { (columnIndex, column) ->
            val availableRows = (0..2).filter { !rowsMap[it]!!.containsKey(columnIndex) }.shuffled()
            rowsMap[availableRows.first()]!![columnIndex] = column.numbers[0]
        }
    }

    /**
     * Groups columns by the number of generated numbers per column.
     *
     * @param ticketColumns The generated ticket columns.
     * @return A triple of lists for columns with 3, 2, and 1 numbers respectively.
     */
    private fun groupColumnsByTotalGeneratedNumbers(ticketColumns: TicketColumns): Triple<List<IndexedValue<TicketColumn>>, List<IndexedValue<TicketColumn>>, List<IndexedValue<TicketColumn>>> {
        val columnsWith3Values = ticketColumns.columns.withIndex().filter { it.value.numbers.size == 3 }
        val columnsWith2Values = ticketColumns.columns.withIndex().filter { it.value.numbers.size == 2 }
        val columnsWith1Value = ticketColumns.columns.withIndex().filter { it.value.numbers.size == 1 }
        return Triple(columnsWith3Values, columnsWith2Values, columnsWith1Value)
    }
}