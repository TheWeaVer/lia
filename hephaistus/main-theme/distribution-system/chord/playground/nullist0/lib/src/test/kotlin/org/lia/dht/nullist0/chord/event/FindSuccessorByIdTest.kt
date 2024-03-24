package org.lia.dht.nullist0.chord.event

import kotlinx.coroutines.test.runTest
import org.lia.dht.nullist0.chord.ChordConfiguration
import org.lia.dht.nullist0.chord.fake.TestChordIdentifierScope
import org.lia.dht.nullist0.chord.fake.TestChordProtocol
import org.lia.dht.nullist0.chord.fake.TestChordRepository
import org.lia.dht.nullist0.chord.model.ChordNode
import kotlin.test.Test
import kotlin.test.assertEquals

class FindSuccessorByIdTest {
    private val findSuccessorById = FindSuccessorById<UByte, UByte>(ID_TO_FIND)

    @Test
    fun testFindSuccessorById_equalNodeId() = runTest {
        // given
        val myNode = ChordNode.NetworkNode<UByte>("my").let { it.copy(successor = it) }
        val scope = TestChordIdentifierScope(
            nodeToIdMap = mapOf(myNode to ID_TO_FIND)
        )
        val configuration = ChordConfiguration(
            node = myNode,
            identifierScope = scope,
            protocol = TestChordProtocol(scope, nodeToDataMap = mapOf(myNode to emptyMap())),
            repository = TestChordRepository()
        )

        // when
        val successor = findSuccessorById(configuration)

        // then
        assertEquals(myNode, successor)
    }

    @Test
    fun testFindSuccessorById_largeNodeId() = runTest {
        // given
        val myNode = ChordNode.NetworkNode<UByte>("my").let { it.copy(successor = it) }
        val scope = TestChordIdentifierScope(
            nodeToIdMap = mapOf(myNode to LARGE_ID)
        )
        val configuration = ChordConfiguration(
            node = myNode,
            identifierScope = scope,
            protocol = TestChordProtocol(scope, nodeToDataMap = mapOf(myNode to emptyMap())),
            repository = TestChordRepository()
        )

        // when
        val successor = findSuccessorById(configuration)

        // then
        assertEquals(myNode, successor)
    }

    @Test
    fun testFindSuccessorById_smallNodeId() = runTest {
        // given
        val myNode = ChordNode.NetworkNode<UByte>("my").let { it.copy(successor = it) }
        val scope = TestChordIdentifierScope(
            nodeToIdMap = mapOf(myNode to SMALL_ID)
        )
        val configuration = ChordConfiguration(
            node = myNode,
            identifierScope = scope,
            protocol = TestChordProtocol(scope, nodeToDataMap = mapOf(myNode to emptyMap())),
            repository = TestChordRepository()
        )

        // when
        val successor = findSuccessorById(configuration)

        // then
        assertEquals(myNode, successor)
    }

    @Test
    fun testFindSuccessorById_twoNodes_with_otherNode_dataNode_myNode() = runTest {
        // when
        val myNode = ChordNode.NetworkNode<UByte>("my").let { it.copy(successor = it) }
        val otherNode = ChordNode.NetworkNode<UByte>("other").let { it.copy(successor = it) }
        val stabilizedMyNode = myNode.copy(
            successor = otherNode,
            predecessor = otherNode,
            fingers = List<ChordNode<UByte>>(UByte.SIZE_BITS) { otherNode }
        )
        val stabilizedOtherNode = otherNode.copy(
            successor = myNode,
            predecessor = myNode,
            fingers = listOf(myNode) + List<ChordNode<UByte>>(UByte.SIZE_BITS - 1) { otherNode }
        )
        val scope = TestChordIdentifierScope(
            nodeToIdMap = mapOf(
                stabilizedOtherNode to SMALL_ID,
                stabilizedMyNode to LARGE_ID
            )
        )
        val configuration = ChordConfiguration(
            node = stabilizedMyNode,
            identifierScope = scope,
            protocol = TestChordProtocol(
                scope,
                nodeToDataMap = mapOf(
                    stabilizedMyNode to emptyMap(),
                    stabilizedOtherNode to emptyMap()
                )
            ),
            repository = TestChordRepository()
        )

        // given
        val successor = findSuccessorById(configuration)

        // then
        assertEquals(stabilizedMyNode, successor)
    }


    @Test
    fun testFindSuccessorById_twoNodes_with_myNode_dataNode_otherNode() = runTest {
        // when
        val myNode = ChordNode.NetworkNode<UByte>("my").let { it.copy(successor = it) }
        val otherNode = ChordNode.NetworkNode<UByte>("other").let { it.copy(successor = it) }
        val stabilizedMyNode = myNode.copy(
            successor = otherNode,
            predecessor = otherNode,
            fingers = listOf(myNode) + List<ChordNode<UByte>>(UByte.SIZE_BITS - 1) { otherNode }
        )
        val stabilizedOtherNode = otherNode.copy(
            successor = myNode,
            predecessor = myNode,
            fingers = List<ChordNode<UByte>>(UByte.SIZE_BITS) { otherNode }
        )
        val scope = TestChordIdentifierScope(
            nodeToIdMap = mapOf(
                stabilizedOtherNode to LARGE_ID,
                stabilizedMyNode to SMALL_ID
            )
        )
        val configuration = ChordConfiguration(
            node = stabilizedMyNode,
            identifierScope = scope,
            protocol = TestChordProtocol(
                scope,
                nodeToDataMap = mapOf(
                    stabilizedMyNode to emptyMap(),
                    stabilizedOtherNode to emptyMap()
                )
            ),
            repository = TestChordRepository()
        )

        // given
        val successor = findSuccessorById(configuration)

        // then
        assertEquals(stabilizedOtherNode, successor)
    }

    companion object {
        private val ID_TO_FIND = 1.toUByte()
        private val LARGE_ID = 2.toUByte()
        private val SMALL_ID = 0.toUByte()
    }
}
