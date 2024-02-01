package org.lia.dht.nullist0.chord

import kotlinx.coroutines.*
import org.lia.dht.nullist0.DistributedHashTable
import org.lia.dht.nullist0.chord.model.AbstractChordNode
import org.lia.dht.nullist0.chord.model.AbstractChordNode.ChordNode
import org.lia.dht.nullist0.chord.model.AbstractChordNode.MutableChordNode
import org.lia.dht.nullist0.chord.model.ChordScope
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
    private val mutableChordNode: MutableChordNode<Id, Value>,
    private val chordScope: ChordScope<Id, Value>,
    private val protocol: ChordProtocol<Id, Value>,
    coroutineScope: CoroutineScope,
    private val repeatDuration: Duration
): DistributedHashTable<Id, Value>, Closeable {
    private val stabilizationJob: Job = coroutineScope.launch {
        var nextFingerToFix = -1
        while (isActive) {
            stabilize()
            nextFingerToFix = (nextFingerToFix + 1) % mutableChordNode.chordRingSizeInBit
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

    private fun findSuccessor(id: Id): AbstractChordNode<Id, Value> = with(chordScope) {
        val successor = requireNotNull(mutableChordNode.successorOrNull)
        val nodeId = mutableChordNode.toId()
        val successorId = successor.toId()
        return if (id inOpenCloseInterval (nodeId to successorId)) {
            successor
        } else {
            protocol.findSuccessorNode(closestPrecedingNode(id), id)
        }
    }

    private fun closestPrecedingNode(id: Id): AbstractChordNode<Id, Value> = with(chordScope) {
        val nodeId = mutableChordNode.toId()
        val closestNodeOrNull = mutableChordNode
            .fingers
            .filterNotNull()
            .lastOrNull { it.toId() inOpenInterval (nodeId to id) }
        return closestNodeOrNull ?: mutableChordNode
    }

    private fun stabilize() = with(chordScope) {
        val successorFromProtocol = protocol.findNode(requireNotNull(mutableChordNode.successorOrNull))
        val predecessorOfSuccessor = protocol.findNode(requireNotNull(successorFromProtocol.predecessorOrNull))
        val nodeId = mutableChordNode.toId()
        val successorId = successorFromProtocol.toId()
        if (predecessorOfSuccessor.toId() inOpenInterval (nodeId to successorId)) {
            mutableChordNode.successorOrNull = predecessorOfSuccessor.toChordNode()
        }
        protocol.notify(mutableChordNode, predecessorOfSuccessor)
    }

    private fun fixFingers(pow: Int) = with(chordScope) {
        val id = mutableChordNode.toId() + (2.0.pow(pow.toDouble()).toInt()).toId()
        val finger = findSuccessor(id).toChordNode()
        mutableChordNode.fixFinger(pow, finger)
    }

    override fun close() {
        runBlocking { stabilizationJob.cancelAndJoin() }
    }

    private fun checkPredecessor() {
        val predecessorOrNull = mutableChordNode.predecessorOrNull
        mutableChordNode.predecessorOrNull = predecessorOrNull?.takeIf { !protocol.healthCheck(it) }
    }

    private infix fun Id.inOpenInterval(pair: Pair<Id, Id>): Boolean = with(chordScope) {
        val id = this@inOpenInterval
        pair.first < id && id < pair.second
    }

    private infix fun Id.inOpenCloseInterval(pair: Pair<Id, Id>): Boolean = with(chordScope) {
        val id = this@inOpenCloseInterval
        pair.first < id && id <= pair.second
    }
    companion object {
        private val DEFAULT_REPEAT_DURATION = 1.seconds

        fun <Id: Comparable<Id>, Value> join(
            uri: URI,
            port: Short,
            chordScope: ChordScope<Id, Value>,
            chordProtocol: ChordProtocol<Id, Value>,
            coroutineScope: CoroutineScope = CoroutineScope(EmptyCoroutineContext),
            repeatDuration: Duration = DEFAULT_REPEAT_DURATION
        ): DistributedHashTable<Id, Value> {
            val chordNode = MutableChordNode<Id, Value>(
                uri = URI.create("localhost:$port"),
                successorOrNull = null,
                predecessorOrNull = null,
                chordRingSizeInBit = chordScope.bitSize()
            ).apply {
                val target = ChordNode<Id, Value>(
                    uri = uri,
                    successorOrNull = null,
                    predecessorOrNull = null
                )
                successorOrNull = with(chordScope) {
                    chordProtocol.findSuccessorNode(target, toId()).toChordNode()
                }
            }
            return ChordDistributedHashTable(
                chordNode,
                chordScope,
                chordProtocol,
                coroutineScope,
                repeatDuration
            )
        }

        fun <Id: Comparable<Id>, Value> create(
            port: Short,
            chordScope: ChordScope<Id, Value>,
            chordProtocol: ChordProtocol<Id, Value>,
            coroutineScope: CoroutineScope = CoroutineScope(EmptyCoroutineContext),
            repeatDuration: Duration = DEFAULT_REPEAT_DURATION
        ): DistributedHashTable<Id, Value> {
            val chordNode = MutableChordNode<Id, Value>(
                uri = URI.create("localhost:$port"),
                successorOrNull = null,
                predecessorOrNull = null,
                chordRingSizeInBit = chordScope.bitSize()
            ).apply { successorOrNull = toChordNode() }
            return ChordDistributedHashTable(
                chordNode,
                chordScope,
                chordProtocol,
                coroutineScope,
                repeatDuration
            )
        }
    }
}
