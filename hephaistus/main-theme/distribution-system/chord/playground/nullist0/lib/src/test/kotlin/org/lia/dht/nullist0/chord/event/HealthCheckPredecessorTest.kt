package org.lia.dht.nullist0.chord.event

import kotlinx.coroutines.test.runTest
import org.lia.dht.nullist0.chord.ChordConfiguration
import org.lia.dht.nullist0.chord.ChordProtocol
import org.lia.dht.nullist0.chord.ChordRepository
import org.lia.dht.nullist0.chord.fake.TestChordIdentifierScope
import org.lia.dht.nullist0.chord.model.ChordNode
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import kotlin.test.Test
import kotlin.test.assertEquals

class HealthCheckPredecessorTest{
    @Test
    fun testHealthCheckPredecessor() = runTest {
        // given
        val healthCheckPredecessor = HealthCheckPredecessor<UByte, UByte>()
        val predecessor = ChordNode.NetworkNode<UByte>("predecessor")
        val node = ChordNode.NetworkNode(
            "my",
            fingers = List(UByte.SIZE_BITS) { ChordNode.InvalidChordNode() },
            predecessor = predecessor
        )
        val scope = TestChordIdentifierScope(nodeToIdMap = mapOf(node to NODE_ID))
        val protocol = mock<ChordProtocol<UByte, UByte>> {
            on { healthCheck(predecessor) } doReturn true
        }
        val repository = mock<ChordRepository<UByte, UByte>>()
        val configuration = ChordConfiguration(
            node = node,
            identifierScope = scope,
            protocol = protocol,
            repository = repository
        )

        // when
        val actual = healthCheckPredecessor(configuration)

        // then
        assertEquals(node, actual)
    }

    @Test
    fun testHealthCheckPredecessor_failed() = runTest {
        // given
        val healthCheckPredecessor = HealthCheckPredecessor<UByte, UByte>()
        val predecessor = ChordNode.NetworkNode<UByte>("predecessor")
        val node = ChordNode.NetworkNode(
                "my",
                fingers = List(UByte.SIZE_BITS) { ChordNode.InvalidChordNode() },
                predecessor = predecessor
        )
        val scope = TestChordIdentifierScope(nodeToIdMap = mapOf(node to NODE_ID))
        val protocol = mock<ChordProtocol<UByte, UByte>> {
            on { healthCheck(predecessor) } doReturn false
        }
        val repository = mock<ChordRepository<UByte, UByte>>()
        val configuration = ChordConfiguration(
                node = node,
                identifierScope = scope,
                protocol = protocol,
                repository = repository
        )

        // when
        val actual = healthCheckPredecessor(configuration)

        // then
        val expected = node.copy(predecessor = ChordNode.InvalidChordNode())
        assertEquals(expected, actual)
    }


    companion object {
        private val NODE_ID = 0.toUByte()
    }
}