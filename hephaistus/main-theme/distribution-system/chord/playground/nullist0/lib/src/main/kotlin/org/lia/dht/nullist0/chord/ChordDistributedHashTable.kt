package org.lia.dht.nullist0.chord

import kotlinx.coroutines.*
import org.lia.dht.nullist0.DistributedHashTable
import org.lia.dht.nullist0.chord.model.ChordNode
import java.io.Closeable
import kotlin.time.Duration

/**
 * An implementation of [DistributedHashTable] for CHORD algorithm.
 */
internal class ChordDistributedHashTable<K, V>(
    private val chordNode: ChordNode,
    private val repository: ChordRepository<Int, V>,
    private val coroutineScope: CoroutineScope,
    private val repeatDuration: Duration
): DistributedHashTable<K, V>, Closeable {
    private var finger: List<ChordNode> = emptyList()
    private var successor: ChordNode = chordNode
    private var predecessorOrNull: ChordNode? = null
    private val stabilizationJob: Job = coroutineScope.launch {
        while (isActive) {
            stabilize()
            fixFingers()
            checkPredecessor()
            delay(repeatDuration)
        }
    }

    override fun get(key: K): V {
        val node = repository.findSuccessor(chordNode, successor, finger, key.toId())
        return repository.getValue(key.toId(), node)
    }

    override fun set(key: K, value: V) {
        val node = repository.findSuccessor(chordNode, successor, finger, key.toId())
        repository.putValue(key.toId(), value, node)
    }

    private fun stabilize() {
        val predecessorOfSuccessor = repository.findPredecessor(successor)
        val ringRange = openInterval(chordNode.toId(), successor.toId())
        if (predecessorOfSuccessor.toId() in ringRange) {
            successor = predecessorOfSuccessor
        }
        repository.notify(successor, chordNode)
    }

    private fun fixFingers() {
        finger = repository.findFingers(chordNode)
    }

    private fun notify(node: ChordNode) {
        if (predecessorOrNull == null) {
            predecessorOrNull = node
            return
        }
        val predecessor = predecessorOrNull ?: return
        val ringRange = openInterval(predecessor.toId(), chordNode.toId())
        if (node.toId() in ringRange) {
            predecessorOrNull = node
        }
    }

    override fun close() {
        runBlocking { stabilizationJob.cancelAndJoin() }
    }

    private fun checkPredecessor() {
        predecessorOrNull = predecessorOrNull?.takeIf { !repository.healthCheck(it) }
    }

    // TODO: Change to Hash algorithm
    private fun K.toId(): Int = hashCode()

    // TODO: Change to Hash algorithm
    private fun ChordNode.toId(): Int = hashCode()

    private fun openInterval(n: Int, m: Int): IntRange = n + 1 until m
}
