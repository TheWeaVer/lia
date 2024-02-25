package org.lia.dht.nullist0.chord.model

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class ChordScopeTest {
    private val scope = TestChordScope()

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
        )

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
        )

        testCases.forEach { (v, pair), expected ->
            val value = with(scope) { v inOpenCloseInterval pair }

            assertEquals(expected, value, "$v is failed with $pair: expected=$expected")
        }
    }

    class TestChordScope: ChordScope<Int, Int>() {
        override fun AbstractChordNode<Int, Int>.toId(): Int  = error("Not supported")
        override fun Int.toId(): Int  = error("Not supported")
        override fun bitSize(): Int  = error("Not supported")
        override fun Int.compareTo(other: Int): Int  = compareTo(other)
        override fun Int.plus(other: Int): Int = error("Not supported")
    }
}
