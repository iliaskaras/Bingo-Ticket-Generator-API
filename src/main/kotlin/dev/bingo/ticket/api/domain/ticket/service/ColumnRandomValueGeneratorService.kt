package dev.bingo.ticket.api.domain.ticket.service

import dev.bingo.ticket.api.domain.ticket.model.TicketColumnEnum
import dev.bingo.ticket.api.domain.ticket.model.TicketColumn
import dev.bingo.ticket.api.domain.ticket.model.TicketColumns
import org.springframework.stereotype.Service

@Service
class ColumnRandomValueGeneratorService {

    companion object {
        /**
         * The minimum and maximum number of values a column can have.
         */
        const val MIN_VALUES_PER_COLUMN = 1
        const val MAX_VALUES_PER_COLUMN = 3
    }

    fun generateColumnValues(): TicketColumns {
        /**
         * Generates a `TicketColumns` object containing Bingo ticket columns with randomized values.
         *
         * The method creates a collection of ticket columns, where each column is associated with
         * a predefined range of numbers (as defined in `TicketColumnEnum`). For each column,
         * between 1 and 3 random numbers are selected from its respective range. These numbers are
         * encapsulated within a `TicketColumn` object, and all columns are returned as part of a
         * `TicketColumns` instance.
         *
         * @return a `TicketColumns` object that represents a complete set of columns for a Bingo ticket.
         *
         * Example:
         * If the predefined ranges are:
         * - Column 1: 1–9
         * - Column 2: 10–19
         * - ...
         *
         * This method may generate a result like:
         * - Column 1: [3, 7] (2 numbers randomly selected from range 1–9)
         * - Column 2: [12] (1 number randomly selected from range 10–19)
         * - ...
         *
         * Each column contains up to 3 numbers, randomly chosen from its range.
         */
        val columns = TicketColumnEnum.allRanges().map { valuesRange ->
            TicketColumn(
                valuesRange.shuffled().take((MIN_VALUES_PER_COLUMN..MAX_VALUES_PER_COLUMN).random())
            )
        }

        return TicketColumns(columns)
    }
}