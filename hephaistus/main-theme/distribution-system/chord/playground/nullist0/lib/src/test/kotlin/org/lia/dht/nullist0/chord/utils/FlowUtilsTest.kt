package org.lia.dht.nullist0.chord.utils

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.time.Duration.Companion.seconds

@OptIn(ExperimentalCoroutinesApi::class)
class FlowUtilsTest {
    @Test
    fun testInfiniteFlowWithDelay() = runTest {
        // given
        val duration = 1.seconds
        val flowList = mutableListOf<Int>()

        // when
        val flow = FlowUtils.infiniteFlowWithDelay(duration) { it }
        backgroundScope.launch { flow.toList(flowList) }
        advanceTimeBy(duration * 5)

        // then
        assertEquals(listOf(0, 1, 2, 3, 4), flowList)
    }
}