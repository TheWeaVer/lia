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

class SetValueToChordRingTest {
    @Test
    fun testSetValueToChordRing() = runTest {
        // given
        val key = 3.toUByte()
        val dataNode = ChordNode.DataNode(key, 1.toUByte())
        val setValueToChordRing = SetValueToChordRing(dataNode)
        val node = ChordNode.NetworkNode<UByte>(
                "my",
                fingers = List(UByte.SIZE_BITS) { ChordNode.InvalidChordNode() }
        ).let { it.copy(successor = it) }
        val scope = TestChordIdentifierScope(nodeToIdMap = mapOf(node to NODE_ID))
        val protocol = mock<ChordProtocol<UByte, UByte>>()
        val repository = mock<ChordRepository<UByte, UByte>>()
        val configuration = ChordConfiguration(
            node = node,
            identifierScope = scope,
            protocol = protocol,
            repository = repository
        )

        Mockito.mockConstruction(
            FindSuccessorById::class.java
        ) { findSuccessorById: FindSuccessorById<*, *>, context ->
            assertEquals(key, context.arguments()[0])
            val mocked = findSuccessorById as FindSuccessorById<UByte, UByte>
            mocked.stub {
                onBlocking { invoke(configuration) } doReturn node
            }
        }.use {
            setValueToChordRing(configuration)
        }

        verify(repository).addData(dataNode)
    }

    @Test
    fun testSetValueToChordRing_other() = runTest {
        // given
        val key = 3.toUByte()
        val dataNode = ChordNode.DataNode(key, 1.toUByte())
        val setValueToChordRing = SetValueToChordRing(dataNode)
        val node = ChordNode.NetworkNode<UByte>(
                "my",
                fingers = List(UByte.SIZE_BITS) { ChordNode.InvalidChordNode() }
        ).let { it.copy(successor = it) }
        val otherNode = ChordNode.NetworkNode<UByte>(
                "other",
                fingers = List(UByte.SIZE_BITS) { ChordNode.InvalidChordNode() }
        ).let { it.copy(successor = it) }
        val scope = TestChordIdentifierScope(nodeToIdMap = mapOf(node to NODE_ID))
        val protocol = mock<ChordProtocol<UByte, UByte>>()
        val repository = mock<ChordRepository<UByte, UByte>>()
        val configuration = ChordConfiguration(
                node = node,
                identifierScope = scope,
                protocol = protocol,
                repository = repository
        )

        Mockito.mockConstruction(
                FindSuccessorById::class.java
        ) { findSuccessorById: FindSuccessorById<*, *>, context ->
            assertEquals(key, context.arguments()[0])
            val mocked = findSuccessorById as FindSuccessorById<UByte, UByte>
            mocked.stub {
                onBlocking { invoke(configuration) } doReturn otherNode
            }
        }.use {
            setValueToChordRing(configuration)
        }

        verify(protocol).putValue(otherNode, dataNode)
    }


    companion object {
        private val NODE_ID = 0.toUByte()
    }
}