package dev.bingo.ticket.api

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class BingoTicketGeneratorApplication

fun main(args: Array<String>) {
	runApplication<BingoTicketGeneratorApplication>(*args)
}
