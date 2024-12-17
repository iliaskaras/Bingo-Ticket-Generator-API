package dev.bingo.ticket.api.domain.strip.model

import spock.lang.Specification

class AllocatedNumbersSpec extends Specification {

    def "should add numbers to specific column"() {
        given: "An empty AllocatedNumbers model"
            def allocatedNumbers = new AllocatedNumbers()

        when: "We add numbers to specific columns"
            allocatedNumbers.addNumberToColumn(0, 1)
            allocatedNumbers.addNumberToColumn(1, 10)
            allocatedNumbers.addNumberToColumn(2, 20)

        then: "The numbers should be correctly added to their respective columns"
            allocatedNumbers.columnNumbers[0].allocatedNumbers == [1] as Set
            allocatedNumbers.columnNumbers[1].allocatedNumbers == [10] as Set
            allocatedNumbers.columnNumbers[2].allocatedNumbers == [20] as Set
    }

    def "should prevent adding duplicate numbers in the same column"() {
        given: "An AllocatedNumbers model with numbers added"
            def allocatedNumbers = new AllocatedNumbers()
            allocatedNumbers.addNumberToColumn(0, 1)

        when: "We try to add a duplicate number to the same column"
            allocatedNumbers.addNumberToColumn(0, 1)

        then: "The column should still only contain one instance of the number"
            allocatedNumbers.columnNumbers[0].allocatedNumbers == [1] as Set
    }

    def "should retrieve correct allocated numbers for a column"() {
        given: "An AllocatedNumbers model with several allocated numbers"
            def allocatedNumbers = new AllocatedNumbers()
            allocatedNumbers.addNumberToColumn(0, 1)
            allocatedNumbers.addNumberToColumn(0, 2)
            allocatedNumbers.addNumberToColumn(1, 10)

        when: "We retrieve the allocated numbers for column 0 and column 1"
            def column0Numbers = allocatedNumbers.columnNumbers[0].allocatedNumbers
            def column1Numbers = allocatedNumbers.columnNumbers[1].allocatedNumbers

        then: "The correct numbers are retrieved"
            column0Numbers == [1, 2] as Set
            column1Numbers == [10] as Set
    }

    def "should throw exception when trying to add number to a column with invalid index"() {
        given: "An AllocatedNumbers model"
            def allocatedNumbers = new AllocatedNumbers()

        when: "We try to add a number to an invalid column index"
            allocatedNumbers.addNumberToColumn(9, 100)

        then: "An exception is thrown"
            def e = thrown(IllegalArgumentException)
            e.message == "Invalid column index: 9"
    }

    def "should return all allocated numbers across all columns"() {
        given: "An AllocatedNumbers model with numbers added"
            def allocatedNumbers = new AllocatedNumbers()
            allocatedNumbers.addNumberToColumn(0, 1)
            allocatedNumbers.addNumberToColumn(1, 10)
            allocatedNumbers.addNumberToColumn(2, 20)

        when: "We retrieve all allocated numbers"
            def allNumbers = allocatedNumbers.getAllAllocatedNumbers()

        then: "The correct total numbers are returned"
            allNumbers == [1, 10, 20] as Set
    }

    def "should return total count of allocated numbers across all columns"() {
        given: "An AllocatedNumbers model with numbers added"
            def allocatedNumbers = new AllocatedNumbers()
            allocatedNumbers.addNumberToColumn(0, 1)
            allocatedNumbers.addNumberToColumn(1, 10)
            allocatedNumbers.addNumberToColumn(1, 11)
            allocatedNumbers.addNumberToColumn(2, 20)

        when: "We calculate the total number of allocated numbers"
            def totalAllocated = allocatedNumbers.getTotalAllocatedNumbers()

        then: "The correct total is returned"
            totalAllocated == 4
    }

    def "should return allocated count for a specific column"() {
        given: "An AllocatedNumbers model with numbers added"
            def allocatedNumbers = new AllocatedNumbers()
            allocatedNumbers.addNumberToColumn(0, 1)
            allocatedNumbers.addNumberToColumn(1, 10)
            allocatedNumbers.addNumberToColumn(1, 11)

        when: "We retrieve the allocated count for column 0 and column 1"
            def countColumn0 = allocatedNumbers.getAllocatedCountForColumn(0)
            def countColumn1 = allocatedNumbers.getAllocatedCountForColumn(1)

        then: "The correct count of allocated numbers is returned"
            countColumn0 == 1
            countColumn1 == 2
    }

    def "should check if a number is already allocated in a specific column"() {
        given: "An AllocatedNumbers model with numbers added"
            def allocatedNumbers = new AllocatedNumbers()
            allocatedNumbers.addNumberToColumn(0, 1)
            allocatedNumbers.addNumberToColumn(1, 10)

        when: "We check if numbers are already allocated"
            def isAllocatedColumn0 = allocatedNumbers.isNumberAlreadyAllocated(0, 1)
            def isAllocatedColumn1 = allocatedNumbers.isNumberAlreadyAllocated(1, 10)
            def isNotAllocatedColumn1 = allocatedNumbers.isNumberAlreadyAllocated(1, 20)

        then: "The correct result is returned"
            isAllocatedColumn0 == true
            isAllocatedColumn1 == true
            isNotAllocatedColumn1 == false
    }

    def "should return empty set for unallocated columns"() {
        given: "An empty AllocatedNumbers model"
            def allocatedNumbers = new AllocatedNumbers()

        when: "We retrieve a column that has no numbers allocated"
            def columnNumbers = allocatedNumbers.columnNumbers[0].allocatedNumbers

        then: "It should return an empty set"
            columnNumbers == [] as Set
    }

    def "should throw exception when generated numbers overlap with allocated numbers"() {
        given: "An AllocatedNumbers model with allocated numbers"
            def allocatedNumbers = new AllocatedNumbers()
            allocatedNumbers.addNumberToColumn(0, 1)
            allocatedNumbers.addNumberToColumn(1, 10)

        and: "A set of new numbers that overlap with already allocated numbers"
            def newNumbers = [1, 10, 11]

        when: "We check for overlaps"
            def overlapNumbers = allocatedNumbers.columnNumbers.findAll {
                it.allocatedNumbers.any { it in newNumbers }
            }.collect { it.allocatedNumbers }

        then: "An exception is thrown due to overlap"
            overlapNumbers.flatten() == [1, 10]
    }
}