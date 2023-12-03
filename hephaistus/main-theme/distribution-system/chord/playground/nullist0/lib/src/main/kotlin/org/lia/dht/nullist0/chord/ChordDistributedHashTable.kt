package org.lia.dht.nullist0.chord

import org.lia.dht.nullist0.DistributedHashTable
import org.lia.dht.nullist0.chord.model.ChordNode

/**
 * An implementation of [DistributedHashTable] for CHORD algorithm.
 */
internal class ChordDistributedHashTable<K, V>(
    private val chordNode: ChordNode,
    private val repository: ChordRepository<Int, V>
): DistributedHashTable<K, V> {
    private var finger: List<ChordNode> = emptyList()
    private var successor: ChordNode = chordNode
    private var predecessorOrNull: ChordNode? = null

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

    private fun checkPredecessor() {
        predecessorOrNull = predecessorOrNull?.takeIf { !repository.healthCheck(it) }
    }

    // TODO: Change to Hash algorithm
    private fun K.toId(): Int = hashCode()

    // TODO: Change to Hash algorithm
    private fun ChordNode.toId(): Int = hashCode()

    private fun openInterval(n: Int, m: Int): IntRange = n + 1 until m
}