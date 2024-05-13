package org.lia.dht.nullist0.chord.model

import org.junit.jupiter.api.Test
import org.lia.dht.nullist0.chord.fake.TestChordIdentifierScope
import kotlin.test.assertEquals

class ChordIdentifierScopeTest {
    private val scope = TestChordIdentifierScope()

    @Test
    fun testOpenIntervals() {
        val testCases = mapOf(
            (2 to (2 to 4)) to false,
            (3 to (2 to 4)) to true,
            (4 to (2 to 4)) to false,
            (1 to (4 to 2)) to true,
            (2 to (4 to 2)) to false,
            (3 to (4 to 2)) to false,
            (4 to (4 to 2)) to false,
            (5 to (4 to 2)) to true
        ).mapKeys { it.key.first.toUByte() to (it.key.second.first.toUByte() to it.key.second.second.toUByte()) }

        testCases.forEach { (v, pair), expected ->
            val value = with(scope) { v inOpenInterval pair }

            assertEquals(expected, value, "$v is failed with $pair: expected=$expected")
        }
    }

    @Test
    fun testOpenClosedIntervals() {
        val testCases = mapOf(
            (2 to (2 to 4)) to false,
            (3 to (2 to 4)) to true,
            (4 to (2 to 4)) to true,
            (1 to (4 to 2)) to true,
            (2 to (4 to 2)) to true,
            (3 to (4 to 2)) to false,
            (4 to (4 to 2)) to false,
            (5 to (4 to 2)) to true
        ).mapKeys { it.key.first.toUByte() to (it.key.second.first.toUByte() to it.key.second.second.toUByte()) }

        testCases.forEach { (v, pair), expected ->
            val value = with(scope) { v inOpenCloseInterval pair }

            assertEquals(expected, value, "$v is failed with $pair: expected=$expected")
        }
    }
}
