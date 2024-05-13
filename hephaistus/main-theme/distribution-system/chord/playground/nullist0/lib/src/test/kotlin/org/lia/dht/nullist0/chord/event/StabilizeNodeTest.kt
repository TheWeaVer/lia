package org.lia.dht.nullist0.chord.event

import kotlinx.coroutines.test.runTest
import org.lia.dht.nullist0.chord.ChordConfiguration
import org.lia.dht.nullist0.chord.ChordProtocol
import org.lia.dht.nullist0.chord.ChordRepository
import org.lia.dht.nullist0.chord.fake.TestChordIdentifierScope
import org.lia.dht.nullist0.chord.model.ChordNode
import org.mockito.Mockito
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.stub
import org.mockito.kotlin.verify
import kotlin.test.Test
import kotlin.test.assertEquals

class StabilizeNodeTest {
    @Test
    fun testStabilizeNode() = runTest {
        // given
        val predecessorOfSuccessor = ChordNode.NetworkNode<UByte>("predecessor")
        val successor = ChordNode.NetworkNode<UByte>("successor", predecessor = predecessorOfSuccessor)
        val node = ChordNode.NetworkNode("my", successor = successor)
        val protocol = mock<ChordProtocol<UByte, UByte>> {
            on { findNode(successor) } doReturn successor
            on { findNode(predecessorOfSuccessor) } doReturn node
        }
        val repository = mock<ChordRepository<UByte, UByte>>()
        val configuration = ChordConfiguration(
            node = node,
            identifierScope = TestChordIdentifierScope(
                nodeToIdMap = mapOf(
                    node to NODE_ID,
                    successor to SUCCESSOR_ID
                )
            ),
            protocol = protocol,
            repository = repository
        )
        val stabilizeNode = StabilizeNode<UByte, UByte>()

        // when
        val actual = stabilizeNode(configuration)

        // then
        val expected = node
        assertEquals(actual, expected)
        verify(protocol).notify(node, node)
    }

    @Test
    fun testStabilizeNode_toBeFixed_newNodeAdded() = runTest {
        // given
        val predecessorOfSuccessor = ChordNode.NetworkNode<UByte>("predecessor")
        val successor = ChordNode.NetworkNode<UByte>("successor", predecessor = predecessorOfSuccessor)
        val node = ChordNode.NetworkNode("my", successor = successor)
        val protocol = mock<ChordProtocol<UByte, UByte>> {
            on { findNode(successor) } doReturn successor
            on { findNode(predecessorOfSuccessor) } doReturn predecessorOfSuccessor
        }
        val repository = mock<ChordRepository<UByte, UByte>>()
        val configuration = ChordConfiguration(
                node = node,
                identifierScope = TestChordIdentifierScope(
                    nodeToIdMap = mapOf(
                        node to NODE_ID,
                        successor to SUCCESSOR_ID,
                        predecessorOfSuccessor to PREDECESSOR_OF_SUCCESSOR_ID
                    )
                ),
                protocol = protocol,
                repository = repository
        )
        val stabilizeNode = StabilizeNode<UByte, UByte>()

        // when
        val actual = stabilizeNode(configuration)

        // then
        val expected = node.copy(successor = predecessorOfSuccessor)
        assertEquals(actual, expected)
        verify(protocol).notify(node, predecessorOfSuccessor)
    }

    companion object {
        private val NODE_ID = 0.toUByte()
        private val SUCCESSOR_ID = 3.toUByte()
        private val PREDECESSOR_OF_SUCCESSOR_ID = 2.toUByte()
    }
}
