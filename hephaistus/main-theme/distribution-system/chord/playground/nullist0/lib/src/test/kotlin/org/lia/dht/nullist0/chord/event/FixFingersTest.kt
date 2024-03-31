package org.lia.dht.nullist0.chord.event

import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import org.lia.dht.nullist0.chord.ChordConfiguration
import org.lia.dht.nullist0.chord.ChordProtocol
import org.lia.dht.nullist0.chord.ChordRepository
import org.lia.dht.nullist0.chord.fake.TestChordIdentifierScope
import org.lia.dht.nullist0.chord.fake.TestChordProtocol
import org.lia.dht.nullist0.chord.fake.TestChordRepository
import org.lia.dht.nullist0.chord.model.ChordNode
import org.mockito.Mockito
import org.mockito.kotlin.*
import kotlin.test.assertEquals

class FixFingersTest {
    @Test
    fun testFixFingers() = runTest {
        val stubbedSuccessor = ChordNode.NetworkNode<UByte>("other")
        val id = 2.toUByte()
        val pow = 1

        // given
        val fixFingers = FixFingers<UByte, UByte>(pow)
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

        // when
        val actual = Mockito.mockConstruction(
            FindSuccessorById::class.java
        ) { findSuccessorById: FindSuccessorById<*, *>, context ->
            assertEquals(id, context.arguments()[0])
            val mocked = findSuccessorById as FindSuccessorById<UByte, UByte>
            mocked.stub {
                onBlocking { invoke(configuration) } doReturn stubbedSuccessor
            }
        }.use {
            fixFingers(configuration)
        }

        // then
        val newFingers = node.fingers.mapIndexed { index, chordNode ->
            if (index == pow) stubbedSuccessor else chordNode
        }
        val expected = node.copy(fingers = newFingers)
        assertEquals(expected, actual)
    }

    companion object {
        private val NODE_ID = 0.toUByte()
    }
}
