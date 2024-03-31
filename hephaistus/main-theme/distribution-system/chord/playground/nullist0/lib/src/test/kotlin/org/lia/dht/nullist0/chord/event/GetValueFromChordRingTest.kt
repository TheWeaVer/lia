package org.lia.dht.nullist0.chord.event

import kotlinx.coroutines.test.runTest
import org.lia.dht.nullist0.chord.ChordConfiguration
import org.lia.dht.nullist0.chord.ChordProtocol
import org.lia.dht.nullist0.chord.ChordRepository
import org.lia.dht.nullist0.chord.fake.TestChordIdentifierScope
import org.lia.dht.nullist0.chord.fake.TestChordProtocol
import org.lia.dht.nullist0.chord.model.ChordNode
import org.mockito.Answers
import org.mockito.Mockito
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.stub
import kotlin.test.Test
import kotlin.test.assertEquals

class GetValueFromChordRingTest {
    @Test
    fun testGetValueToChordRing() = runTest {
        // given
        val key = 3.toUByte()
        val dataNode = ChordNode.DataNode(key, 1.toUByte())
        val getValueToChordRing = GetValueFromChordRing<UByte, UByte>(key)
        val node = ChordNode.NetworkNode<UByte>(
            "my",
            fingers = List(UByte.SIZE_BITS) { ChordNode.InvalidChordNode() }
        ).let { it.copy(successor = it) }
        val scope = TestChordIdentifierScope(nodeToIdMap = mapOf(node to NODE_ID))
        val protocol = mock<ChordProtocol<UByte, UByte>>()
        val repository = mock<ChordRepository<UByte, UByte>> {
            on { getData(key) } doReturn dataNode
        }
        val configuration = ChordConfiguration(
                node = node,
                identifierScope = scope,
                protocol = protocol,
                repository = repository
        )

        // when
        val actual = Mockito.mockConstruction(
            FindSuccessorById::class.java
        ) { findSuccessorById: FindSuccessorById<*, *>, context ->
            assertEquals(key, context.arguments()[0])
            val mocked = findSuccessorById as FindSuccessorById<UByte, UByte>
            mocked.stub {
                onBlocking { invoke(configuration) } doReturn node
            }
        }.use {
            getValueToChordRing(configuration)
        }

        // then
        assertEquals(dataNode, actual)
    }

    @Test
    fun testGetValueToChordRing_other() = runTest {
        // given
        val key = 3.toUByte()
        val value = 1.toUByte()
        val dataNode = ChordNode.DataNode(key, value)
        val getValueToChordRing = GetValueFromChordRing<UByte, UByte>(key)
        val node = ChordNode.NetworkNode<UByte>(
            "my",
            fingers = List(UByte.SIZE_BITS) { ChordNode.InvalidChordNode() }
        ).let { it.copy(successor = it) }
        val otherNode = ChordNode.NetworkNode<UByte>(
            "other",
            fingers = List(UByte.SIZE_BITS) { ChordNode.InvalidChordNode() }
        ).let { it.copy(successor = it) }
        val scope = TestChordIdentifierScope(nodeToIdMap = mapOf(node to NODE_ID))
        val protocol = TestChordProtocol(
            scope,
            nodeToDataMap = mapOf(
                node to emptyMap(),
                otherNode to mapOf(
                    key to value
                )
            )
        )
        val repository = mock<ChordRepository<UByte, UByte>>()
        val configuration = ChordConfiguration(
            node = node,
            identifierScope = scope,
            protocol = protocol,
            repository = repository
        )

        // when
        val actual = Mockito.mockConstruction(
            FindSuccessorById::class.java
        ) { findSuccessorById: FindSuccessorById<*, *>, context ->
            assertEquals(key, context.arguments()[0])
            val mocked = findSuccessorById as FindSuccessorById<UByte, UByte>
            mocked.stub {
                onBlocking { invoke(configuration) } doReturn otherNode
            }
        }.use {
            getValueToChordRing(configuration)
        }

        // then
        assertEquals(dataNode, actual)
    }


    companion object {
        private val NODE_ID = 0.toUByte()
    }
}