package dev.bingo.ticket.api.domain.ticket.model

enum class TicketColumnEnum(val valuesRange: IntRange) {
    COLUMN_1(1..9),
    COLUMN_2(10..19),
    COLUMN_3(20..29),
    COLUMN_4(30..39),
    COLUMN_5(40..49),
    COLUMN_6(50..59),
    COLUMN_7(60..69),
    COLUMN_8(70..79),
    COLUMN_9(80..90);

    companion object {
        @JvmStatic
        fun allRanges(): List<IntRange> = entries.map { it.valuesRange }

        @JvmStatic
        fun allValues(): List<Int> = allRanges().flatMap { it.toList() }

        @JvmStatic
        fun getByIndex(index: Int): TicketColumnEnum {
            return entries[index]
        }
    }
}