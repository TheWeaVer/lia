package org.lia.dht.nullist0.chord

import kotlinx.coroutines.*
import org.lia.dht.nullist0.DistributedHashTable
import org.lia.dht.nullist0.chord.model.ChordIdentifierScope
import org.lia.dht.nullist0.chord.model.ChordNode
import java.io.Closeable
import java.net.URI
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.math.pow
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

/**
 * An implementation of [DistributedHashTable] for CHORD algorithm.
 */
internal class ChordDistributedHashTable<Id, Value> private constructor(
    private val chordNode: ChordNode.NetworkNode<Id>,
    private val chordIdentifierScope: ChordIdentifierScope<Id, Value>,
    private val protocol: ChordProtocol<Id, Value>,
    coroutineScope: CoroutineScope,
    private val repeatDuration: Duration
): DistributedHashTable<Id, Value>, Closeable {
    private val stabilizationJob: Job = coroutineScope.launch {
        val bitSize = chordIdentifierScope.bitSize()
        var nextFingerToFix = -1
        while (isActive) {
            stabilize()
            nextFingerToFix = (nextFingerToFix + 1) % bitSize
            fixFingers(nextFingerToFix)
            checkPredecessor()
            delay(repeatDuration)
        }
    }

    override fun get(key: Id): Value {
        val node = findSuccessor(key)
        return protocol.getValue(node, key)
    }

    override fun set(key: Id, value: Value) {
        val node = findSuccessor(key)
        protocol.putValue(node, key, value)
    }

    private fun findSuccessor(id: Id): ChordNode.NetworkNode<Id> = with(chordIdentifierScope) {
        val successor = chordNode.successor
        val nodeId = chordNode.toId()
        val successorId = successor.toId()
        return if (id inOpenCloseInterval (nodeId to successorId)) {
            successor as ChordNode.NetworkNode<Id>
        } else {
            protocol.findSuccessorNode(closestPrecedingNode(id), id)
        }
    }

    private fun closestPrecedingNode(id: Id): ChordNode<Id> = with(chordIdentifierScope) {
        val nodeId = chordNode.toId()
        val closestNodeOrNull = chordNode
            .fingers
            .lastOrNull { it.toId() inOpenInterval (nodeId to id) }
        return closestNodeOrNull ?: chordNode
    }

    private fun stabilize() = with(chordIdentifierScope) {
        val successorFromProtocol = protocol.findNode(chordNode.successor)
        val predecessorOfSuccessor = protocol.findNode(successorFromProtocol.predecessor)
        val nodeId = chordNode.toId()
        val successorId = successorFromProtocol.toId()
        if (predecessorOfSuccessor.toId() inOpenInterval (nodeId to successorId)) {
            // TODO: Handle to use new node class object
            val newNode = chordNode.copy(
                successor = predecessorOfSuccessor
            )
        }
        protocol.notify(chordNode, predecessorOfSuccessor)
    }

    private fun fixFingers(pow: Int) = with(chordIdentifierScope) {
        val id = chordNode.toId() + (2.0.pow(pow.toDouble()).toInt()).toId()
        val finger = findSuccessor(id)
        // TODO: Handle to use new node class object
        val newNode = chordNode.copy(
            fingers = chordNode.fingers
                .toMutableList()
                .apply { this[pow] = finger }
                .toList()
        )
    }

    override fun close() {
        runBlocking { stabilizationJob.cancelAndJoin() }
    }

    private fun checkPredecessor() {
        val predecessor = chordNode.predecessor
        // TODO: Handle to use new node class object
        val newNode = chordNode.copy(
            predecessor = predecessor.takeIf { !protocol.healthCheck(it) } ?: ChordNode.InvalidChordNode()
        )
    }

    companion object {
        private val DEFAULT_REPEAT_DURATION = 1.seconds

        fun <Id: Comparable<Id>, Value> join(
                uri: URI,
                port: Short,
                chordIdentifierScope: ChordIdentifierScope<Id, Value>,
                chordProtocol: ChordProtocol<Id, Value>,
                coroutineScope: CoroutineScope = CoroutineScope(EmptyCoroutineContext),
                repeatDuration: Duration = DEFAULT_REPEAT_DURATION
        ): DistributedHashTable<Id, Value> {
            val chordNode = ChordNode.NetworkNode(
                uri = "localhost:$port",
                successor = with(chordIdentifierScope) {
                    chordProtocol.findSuccessorNode(
                        ChordNode.NetworkNode(uri = uri.path),
                        ChordNode.NetworkNode<Id>(uri = "localhost:$port").toId()
                    )
                }
            )
            return ChordDistributedHashTable(
                chordNode,
                chordIdentifierScope,
                chordProtocol,
                coroutineScope,
                repeatDuration
            )
        }

        fun <Id: Comparable<Id>, Value> create(
                port: Short,
                chordIdentifierScope: ChordIdentifierScope<Id, Value>,
                chordProtocol: ChordProtocol<Id, Value>,
                coroutineScope: CoroutineScope = CoroutineScope(EmptyCoroutineContext),
                repeatDuration: Duration = DEFAULT_REPEAT_DURATION
        ): DistributedHashTable<Id, Value> {
            val chordNode = ChordNode.NetworkNode<Id>(
                uri = "localhost:$port",
                successor = ChordNode.NetworkNode(
                    uri = "localhost:$port"
                )
            )
            return ChordDistributedHashTable(
                chordNode,
                chordIdentifierScope,
                chordProtocol,
                coroutineScope,
                repeatDuration
            )
        }
    }
}
