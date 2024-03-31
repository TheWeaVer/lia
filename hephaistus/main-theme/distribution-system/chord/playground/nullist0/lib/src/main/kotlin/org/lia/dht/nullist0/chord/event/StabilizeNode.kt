package org.lia.dht.nullist0.chord.event

import org.lia.dht.nullist0.chord.ChordConfiguration
import org.lia.dht.nullist0.chord.model.ChordNode

/**
 * A class to stabilize node using the chord protocol.
 */
internal class StabilizeNode<Id, Value>: ChordEvent<Id, Value, ChordNode.NetworkNode<Id>> {
    override suspend fun invoke(configuration: ChordConfiguration<Id, Value>): ChordNode.NetworkNode<Id> {
        val (chordNode, identifierScope, protocol) = configuration
        val successorFromProtocol = protocol.findNode(chordNode.successor)
        val predecessorOfSuccessor = protocol.findNode(successorFromProtocol.predecessor)
        val newNode = with(identifierScope) {
            val nodeId = chordNode.toId()
            val successorId = successorFromProtocol.toId()
            if (predecessorOfSuccessor.toId() inOpenInterval (nodeId to successorId)) {
                chordNode.copy(successor = predecessorOfSuccessor)
            } else {
                chordNode
            }
        }
        protocol.notify(chordNode, predecessorOfSuccessor)
        return newNode
    }
}
