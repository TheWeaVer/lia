package org.lia.dht.nullist0.chord.event

import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import org.lia.dht.nullist0.chord.ChordConfiguration
import org.lia.dht.nullist0.chord.ChordRepository
import org.lia.dht.nullist0.chord.fake.TestChordIdentifierScope
import org.lia.dht.nullist0.chord.fake.TestChordProtocol
import org.lia.dht.nullist0.chord.model.ChordNode
import org.mockito.Mockito
import org.mockito.Mockito.mock
import org.mockito.Mockito.withSettings
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.stub
import org.mockito.kotlin.verify
import kotlin.test.assertEquals

class PerformStabilizationTest {
    @Test
    fun testPerformStabilization() = runTest {
        // given
        val index = 1
        val node = ChordNode.NetworkNode<UByte>(
                "my",
                fingers = List(UByte.SIZE_BITS) { ChordNode.InvalidChordNode() }
        ).let { it.copy(successor = it) }
        val stabilizedNode = ChordNode.NetworkNode(
            "my",
            fingers = listOf(node) + List(UByte.SIZE_BITS - 1) { ChordNode.InvalidChordNode() }
        ).let { it.copy(successor = it) }
        val performStabilization = PerformStabilization<UByte, UByte>(index)
        val scope = TestChordIdentifierScope(nodeToIdMap = mapOf(node to NODE_ID))
        val protocol = TestChordProtocol(
            scope,
            nodeToDataMap = mapOf(node to emptyMap())
        )
        val repository = mock<ChordRepository<UByte, UByte>>()
        val configuration = ChordConfiguration(
            node = node,
            identifierScope = scope,
            protocol = protocol,
            repository = repository
        )
        val mockConstructions = listOf(
            Mockito.mockConstruction(StabilizeNode::class.java, withSettings()) { mock, _ ->
                val chordEvent = mock as ChordEvent<UByte, UByte, ChordNode.NetworkNode<UByte>>
                chordEvent.stub {
                    onBlocking { invoke(any()) } doReturn stabilizedNode
                }
            },
            Mockito.mockConstruction(FixFingers::class.java, withSettings()) { mock, _ ->
                val chordEvent = mock as ChordEvent<UByte, UByte, ChordNode.NetworkNode<UByte>>
                chordEvent.stub {
                    onBlocking { invoke(any()) } doReturn stabilizedNode
                }
            },
            Mockito.mockConstruction(HealthCheckPredecessor::class.java, withSettings()) { mock, _ ->
                val chordEvent = mock as ChordEvent<UByte, UByte, ChordNode.NetworkNode<UByte>>
                chordEvent.stub {
                    onBlocking { invoke(any()) } doReturn stabilizedNode
                }
            }
        )

        // when
        val actual = performStabilization(configuration)

        // then
        val stabilizedConfiguration = ChordConfiguration(
            node = stabilizedNode,
            identifierScope = scope,
            protocol = protocol,
            repository = repository
        )
        assertEquals(actual, stabilizedConfiguration)
        mockConstructions.forEach {
            val chordEvent = it.constructed().first() as ChordEvent<UByte, UByte, ChordConfiguration<UByte, UByte>>
            verify(chordEvent).invoke(any())
            it.close()
        }
    }

    companion object {
        private val NODE_ID = 0.toUByte()
    }
}
