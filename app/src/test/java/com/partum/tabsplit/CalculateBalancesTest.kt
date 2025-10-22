package com.partum.tabsplit

import com.partum.tabsplit.data.model.Expense
import com.partum.tabsplit.data.model.Participant
import com.partum.tabsplit.utils.calculateBalances
import org.junit.Assert.*
import org.junit.Test

class CalculateBalancesTest {

    @Test
    fun testSingleExpenseSharedEqually() {
        val sessionId = "s1"
        val participants = mapOf(
            sessionId to listOf(
                Participant("u1", "alice"),
                Participant("u2", "bob"),
                Participant("u3", "charlie"),
            )
        )

        val expenses = mapOf(
            sessionId to listOf(
                Expense(
                    id = "e1",
                    sessionId = sessionId,
                    payerId = "u1",
                    memo = "Dinner",
                    amount = 90.00,
                    createdAt = "String",
                    payerUsername = "u1",
                    payerParticipantId = "u1"
                )
            )
        )

        val balances = calculateBalances(sessionId, participants, expenses)

        // Each participants owes 30. Alice paid 90
        // Alice: +60 (paid 90, subtract her share -> 90 - 30 = +60)
        // Bob: -30 (owes his share of -30)
        // Charlie: -30 (owes his share of -30)

        assertEquals(60.0, balances["u1"]!!, 0.001)
        assertEquals(-30.0, balances["u2"]!!, 0.001)
        assertEquals(-30.0, balances["u3"]!!, 0.001)
    }

    @Test
    fun testMultipleExpenses() {
        val sessionId = "s1"
        val participants = mapOf(
            sessionId to listOf(
                Participant("u1", "alice"),
                Participant("u2", "bob")
            )
        )

        val expenses = mapOf(
            sessionId to listOf(
                Expense(
                    id = "e1",
                    sessionId = sessionId,
                    memo = "Breakfast",
                    amount = 40.00,
                    payerId = "u1",
                    createdAt = "String",
                    payerUsername = "u1",
                    payerParticipantId = "u1"
                ), // Alice paid 40
                Expense(
                    id = "e2",
                    sessionId = sessionId,
                    payerId = "u2",
                    memo = "Lodging/Hotel",
                    amount = 220.00,
                    createdAt = "String",
                    payerUsername = "u2",
                    payerParticipantId = "u2"
                ) // Bob paid 220
            )

        )

        val balances = calculateBalances(sessionId, participants, expenses)

        // Each expense is split equally (2 people).
        // Breakfast: 40 (Alice paid)
        // Lodging/Hotel: 220 (Bob paid)

        // Total and Equal Share
        // Total spent = 40 + 220 = 260
        // Total participants = 2
        // Equal Share = 260 / 2 = 130
        // Alice paid 40, her fair share: 130 -> 40 - 130 = -90 (owes 90)
        // Bob paid 220, his fair share: 130 -> 220 - 130 = 90 (owned 90)

        assertEquals(-90.0, balances["u1"]!!, 0.001)
        assertEquals(90.0, balances["u2"]!!, 0.001)

    }

    @Test
    fun testNoExpense() {
        val sessionId = "s1"
        val participants = mapOf(
            sessionId to listOf(
                Participant("u1", "alice"),
                Participant("u2", "bob"),
            )
        )
        val expenses = mapOf(sessionId to emptyList<Expense>())

        val balances = calculateBalances(sessionId, participants, expenses)

        // No expense -> everyone at 0
        assertEquals(0.0, balances["u1"]!!, 0.001)
        assertEquals(0.0, balances["u2"]!!, 0.001)
    }

    @Test
    fun testZeroExpenseAmount() {
        val sessionId = "s1"
        val participants = mapOf(
            sessionId to listOf(
                Participant("u1", "alice"),
                Participant("u2", "bob"),
            )
        )
        val expenses = mapOf(
            sessionId to listOf(
                Expense(
                    id = "e1",
                    sessionId = sessionId,
                    payerId = "u1",
                    memo = "Free snack",
                    amount = 0.00,
                    createdAt = "String",
                    payerUsername = "u1",
                    payerParticipantId = "u1"
                )
            )
        )

        val balances = calculateBalances(sessionId, participants, expenses)

        // Zero expense -> no effect
        assertEquals(0.0, balances["u2"]!!, 0.001)
        assertEquals(0.0, balances["u2"]!!, 0.001)

    }

    @Test
    fun testNoParticipants() {
        val sessionId = "s1"
        val participants = mapOf(sessionId to emptyList<Participant>())
        val expenses = mapOf(
            sessionId to listOf(
                Expense(
                    id = "e1",
                    sessionId = sessionId,
                    payerId = "u1",
                    memo = "Random",
                    amount = 50.00,
                    createdAt = "String",
                    payerUsername = "u1",
                    payerParticipantId = "u1"
                )
            )
        )

        val balances = calculateBalances(sessionId, participants, expenses)

        // No participants -> balance map should be empty
        assertTrue(balances.isEmpty())
    }

    @Test
    fun testSingleParticipantSession() {
        val sessionId = "s1"
        val participants = mapOf(sessionId to listOf<Participant>(Participant("u1", "Alice")))

        val expenses = mapOf(
            sessionId to listOf(
                Expense(
                    id = "e1",
                    sessionId = sessionId,
                    payerId = "u1",
                    memo = "Solo lunch",
                    amount = 120.00,
                    createdAt = "String",
                    payerUsername = "u1",
                    payerParticipantId = "u1"
                )
            )
        )

        val balances = calculateBalances(sessionId, participants, expenses)

        // Only Alice exists, so she paid and owes herself -> 0 net/balance
        assertEquals(0.0, balances["u1"]!!, 0.001)

    }
}