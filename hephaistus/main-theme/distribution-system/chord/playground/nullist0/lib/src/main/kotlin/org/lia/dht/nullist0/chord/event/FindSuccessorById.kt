package org.lia.dht.nullist0.chord.event

import org.lia.dht.nullist0.chord.ChordConfiguration
import org.lia.dht.nullist0.chord.model.ChordNode

/**
 * A class to find successor by the given id in the chord ring.
 */
internal class FindSuccessorById<Id, Value>(
    private val id: Id
): ChordEvent<Id, Value, ChordNode.NetworkNode<Id>> {

    override suspend fun invoke(configuration: ChordConfiguration<Id, Value>): ChordNode.NetworkNode<Id> {
        val (chordNode, identifierScope, protocol) = configuration
        return with(identifierScope) {
            val successor = chordNode.successor
            val nodeId = chordNode.toId()
            val successorId = successor.toId()
            if (id inOpenCloseInterval (nodeId to successorId)) {
                successor as ChordNode.NetworkNode<Id>
            } else {
                val closestPrecedingNode = chordNode.fingers
                    .filterIsInstance<ChordNode.NetworkNode<Id>>()
                    .lastOrNull { it.toId() inOpenInterval (nodeId to id) }
                    ?: chordNode
                protocol.findSuccessorNode(closestPrecedingNode, id)
            }
        }
    }
}
