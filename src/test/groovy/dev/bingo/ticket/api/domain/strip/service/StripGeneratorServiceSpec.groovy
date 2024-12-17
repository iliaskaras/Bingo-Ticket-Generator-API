package dev.bingo.ticket.api.domain.strip.service

import dev.bingo.ticket.api.domain.ticket.model.TicketRow
import dev.bingo.ticket.api.domain.ticket.model.TicketRowCell
import dev.bingo.ticket.api.domain.ticket.service.TicketGeneratorService
import spock.lang.Specification

class StripGeneratorServiceSpec extends Specification {

    def ticketGeneratorService =  Mock(TicketGeneratorService)
    def stripGeneratorService = new StripGeneratorService(ticketGeneratorService)

    def "should generate a strip of 6 tickets with updated previously allocated numbers"() {
        given: "The ticket generator service is mocked to return a valid ticket"
            // Generate 6 valid Bingo tickets, each containing 3 TicketRows
            // with all the unique bingo numbers.
            def generatedTicketRowList = [
                // Ticket 1
                [
                    new TicketRow(
                        [
                            new TicketRowCell.NumberRowCell(1),
                            new TicketRowCell.NumberRowCell(10),
                            new TicketRowCell.NumberRowCell(20),
                            new TicketRowCell.NumberRowCell(30),
                            new TicketRowCell.NumberRowCell(40),
                            TicketRowCell.BlankRowCell.INSTANCE,
                            TicketRowCell.BlankRowCell.INSTANCE,
                            TicketRowCell.BlankRowCell.INSTANCE,
                            TicketRowCell.BlankRowCell.INSTANCE,
                        ]
                    ),
                    new TicketRow(
                        [
                            new TicketRowCell.NumberRowCell(2),
                            new TicketRowCell.NumberRowCell(11),
                            new TicketRowCell.NumberRowCell(21),
                            new TicketRowCell.NumberRowCell(31),
                            new TicketRowCell.NumberRowCell(41),
                            TicketRowCell.BlankRowCell.INSTANCE,
                            TicketRowCell.BlankRowCell.INSTANCE,
                            TicketRowCell.BlankRowCell.INSTANCE,
                            TicketRowCell.BlankRowCell.INSTANCE,
                        ]
                    ),
                    new TicketRow(
                        [
                            new TicketRowCell.NumberRowCell(3),
                            new TicketRowCell.NumberRowCell(12),
                            new TicketRowCell.NumberRowCell(22),
                            new TicketRowCell.NumberRowCell(32),
                            new TicketRowCell.NumberRowCell(42),
                            TicketRowCell.BlankRowCell.INSTANCE,
                            TicketRowCell.BlankRowCell.INSTANCE,
                            TicketRowCell.BlankRowCell.INSTANCE,
                            TicketRowCell.BlankRowCell.INSTANCE,
                        ]
                    ),
                ],
                // Ticket 2
                [
                    new TicketRow(
                        [
                            new TicketRowCell.NumberRowCell(4),
                            new TicketRowCell.NumberRowCell(13),
                            new TicketRowCell.NumberRowCell(23),
                            new TicketRowCell.NumberRowCell(33),
                            new TicketRowCell.NumberRowCell(43),
                            TicketRowCell.BlankRowCell.INSTANCE,
                            TicketRowCell.BlankRowCell.INSTANCE,
                            TicketRowCell.BlankRowCell.INSTANCE,
                            TicketRowCell.BlankRowCell.INSTANCE,
                        ]
                    ),
                    new TicketRow(
                        [
                            new TicketRowCell.NumberRowCell(5),
                            new TicketRowCell.NumberRowCell(14),
                            new TicketRowCell.NumberRowCell(24),
                            new TicketRowCell.NumberRowCell(34),
                            new TicketRowCell.NumberRowCell(44),
                            TicketRowCell.BlankRowCell.INSTANCE,
                            TicketRowCell.BlankRowCell.INSTANCE,
                            TicketRowCell.BlankRowCell.INSTANCE,
                            TicketRowCell.BlankRowCell.INSTANCE,
                        ]
                    ),
                    new TicketRow(
                        [
                            new TicketRowCell.NumberRowCell(6),
                            new TicketRowCell.NumberRowCell(15),
                            new TicketRowCell.NumberRowCell(25),
                            new TicketRowCell.NumberRowCell(35),
                            new TicketRowCell.NumberRowCell(45),
                            TicketRowCell.BlankRowCell.INSTANCE,
                            TicketRowCell.BlankRowCell.INSTANCE,
                            TicketRowCell.BlankRowCell.INSTANCE,
                            TicketRowCell.BlankRowCell.INSTANCE,
                        ]
                    ),
                ],
                // Ticket 3
                [
                    new TicketRow(
                        [
                            new TicketRowCell.NumberRowCell(7),
                            new TicketRowCell.NumberRowCell(16),
                            new TicketRowCell.NumberRowCell(26),
                            new TicketRowCell.NumberRowCell(36),
                            new TicketRowCell.NumberRowCell(46),
                            TicketRowCell.BlankRowCell.INSTANCE,
                            TicketRowCell.BlankRowCell.INSTANCE,
                            TicketRowCell.BlankRowCell.INSTANCE,
                            TicketRowCell.BlankRowCell.INSTANCE,
                        ]
                    ),
                    new TicketRow(
                        [
                            new TicketRowCell.NumberRowCell(8),
                            new TicketRowCell.NumberRowCell(17),
                            new TicketRowCell.NumberRowCell(27),
                            new TicketRowCell.NumberRowCell(37),
                            new TicketRowCell.NumberRowCell(47),
                            TicketRowCell.BlankRowCell.INSTANCE,
                            TicketRowCell.BlankRowCell.INSTANCE,
                            TicketRowCell.BlankRowCell.INSTANCE,
                            TicketRowCell.BlankRowCell.INSTANCE,
                        ]
                    ),
                    new TicketRow(
                        [
                            new TicketRowCell.NumberRowCell(9),
                            new TicketRowCell.NumberRowCell(18),
                            new TicketRowCell.NumberRowCell(28),
                            new TicketRowCell.NumberRowCell(38),
                            new TicketRowCell.NumberRowCell(48),
                            TicketRowCell.BlankRowCell.INSTANCE,
                            TicketRowCell.BlankRowCell.INSTANCE,
                            TicketRowCell.BlankRowCell.INSTANCE,
                            TicketRowCell.BlankRowCell.INSTANCE,
                        ]
                    ),
                ],
                // Ticket 4
                [
                    new TicketRow(
                        [
                            TicketRowCell.BlankRowCell.INSTANCE,
                            new TicketRowCell.NumberRowCell(19),
                            new TicketRowCell.NumberRowCell(29),
                            new TicketRowCell.NumberRowCell(39),
                            new TicketRowCell.NumberRowCell(49),
                            new TicketRowCell.NumberRowCell(50),
                            TicketRowCell.BlankRowCell.INSTANCE,
                            TicketRowCell.BlankRowCell.INSTANCE,
                            TicketRowCell.BlankRowCell.INSTANCE
                        ]
                    ),
                    new TicketRow(
                        [
                            TicketRowCell.BlankRowCell.INSTANCE,
                            TicketRowCell.BlankRowCell.INSTANCE,
                            TicketRowCell.BlankRowCell.INSTANCE,
                            TicketRowCell.BlankRowCell.INSTANCE,
                            new TicketRowCell.NumberRowCell(51),
                            new TicketRowCell.NumberRowCell(52),
                            new TicketRowCell.NumberRowCell(60),
                            new TicketRowCell.NumberRowCell(70),
                            new TicketRowCell.NumberRowCell(80),
                        ]
                    ),
                    new TicketRow(
                        [
                            TicketRowCell.BlankRowCell.INSTANCE,
                            TicketRowCell.BlankRowCell.INSTANCE,
                            TicketRowCell.BlankRowCell.INSTANCE,
                            TicketRowCell.BlankRowCell.INSTANCE,
                            new TicketRowCell.NumberRowCell(53),
                            new TicketRowCell.NumberRowCell(54),
                            new TicketRowCell.NumberRowCell(61),
                            new TicketRowCell.NumberRowCell(71),
                            new TicketRowCell.NumberRowCell(81),
                        ]
                    ),
                ],
                // Ticket 5
                [
                    new TicketRow(
                        [
                            TicketRowCell.BlankRowCell.INSTANCE,
                            TicketRowCell.BlankRowCell.INSTANCE,
                            TicketRowCell.BlankRowCell.INSTANCE,
                            TicketRowCell.BlankRowCell.INSTANCE,
                            new TicketRowCell.NumberRowCell(55),
                            new TicketRowCell.NumberRowCell(56),
                            new TicketRowCell.NumberRowCell(62),
                            new TicketRowCell.NumberRowCell(72),
                            new TicketRowCell.NumberRowCell(82),
                        ]
                    ),
                    new TicketRow(
                        [
                            TicketRowCell.BlankRowCell.INSTANCE,
                            TicketRowCell.BlankRowCell.INSTANCE,
                            TicketRowCell.BlankRowCell.INSTANCE,
                            TicketRowCell.BlankRowCell.INSTANCE,
                            new TicketRowCell.NumberRowCell(57),
                            new TicketRowCell.NumberRowCell(58),
                            new TicketRowCell.NumberRowCell(63),
                            new TicketRowCell.NumberRowCell(73),
                            new TicketRowCell.NumberRowCell(83),
                        ]
                    ),
                    new TicketRow(
                        [
                            TicketRowCell.BlankRowCell.INSTANCE,
                            TicketRowCell.BlankRowCell.INSTANCE,
                            TicketRowCell.BlankRowCell.INSTANCE,
                            TicketRowCell.BlankRowCell.INSTANCE,
                            new TicketRowCell.NumberRowCell(59),
                            new TicketRowCell.NumberRowCell(64),
                            new TicketRowCell.NumberRowCell(65),
                            new TicketRowCell.NumberRowCell(74),
                            new TicketRowCell.NumberRowCell(84),
                        ]
                    ),
                ],
                // Ticket 6
                [
                    new TicketRow(
                        [
                            TicketRowCell.BlankRowCell.INSTANCE,
                            TicketRowCell.BlankRowCell.INSTANCE,
                            TicketRowCell.BlankRowCell.INSTANCE,
                            TicketRowCell.BlankRowCell.INSTANCE,
                            new TicketRowCell.NumberRowCell(66),
                            new TicketRowCell.NumberRowCell(67),
                            new TicketRowCell.NumberRowCell(68),
                            new TicketRowCell.NumberRowCell(75),
                            new TicketRowCell.NumberRowCell(85),
                        ]
                    ),
                    new TicketRow(
                        [
                            TicketRowCell.BlankRowCell.INSTANCE,
                            TicketRowCell.BlankRowCell.INSTANCE,
                            TicketRowCell.BlankRowCell.INSTANCE,
                            TicketRowCell.BlankRowCell.INSTANCE,
                            new TicketRowCell.NumberRowCell(69),
                            new TicketRowCell.NumberRowCell(76),
                            new TicketRowCell.NumberRowCell(77),
                            new TicketRowCell.NumberRowCell(78),
                            new TicketRowCell.NumberRowCell(86),
                        ]
                    ),
                    new TicketRow(
                        [
                            TicketRowCell.BlankRowCell.INSTANCE,
                            TicketRowCell.BlankRowCell.INSTANCE,
                            TicketRowCell.BlankRowCell.INSTANCE,
                            TicketRowCell.BlankRowCell.INSTANCE,
                            new TicketRowCell.NumberRowCell(79),
                            new TicketRowCell.NumberRowCell(87),
                            new TicketRowCell.NumberRowCell(88),
                            new TicketRowCell.NumberRowCell(89),
                            new TicketRowCell.NumberRowCell(90),
                        ]
                    ),
                ],
            ]
    
            def ticketIndex = 0
            ticketGeneratorService.generateTicket(_) >> {
                generatedTicketRowList[ticketIndex++]
            }

        when: "We generate a strip of 6 tickets"
            def strip = stripGeneratorService.generateStrip()

        then: "We expect 6 tickets to be generated"
            strip.tickets.size() == 6

        and: "Each ticket contains 5 numbers and 4 blanks"
            strip.tickets.each { ticket ->
                ticket.rows.each {ticketRow ->
                    ticketRow.cells.count { it instanceof TicketRowCell.NumberRowCell } == 5
                    ticketRow.cells.count { it instanceof TicketRowCell.BlankRowCell } == 4
                }
            }
    }
}