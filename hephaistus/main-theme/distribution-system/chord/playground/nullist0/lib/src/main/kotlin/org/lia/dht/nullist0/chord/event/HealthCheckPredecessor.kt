package org.lia.dht.nullist0.chord.event

import org.lia.dht.nullist0.chord.ChordConfiguration
import org.lia.dht.nullist0.chord.model.ChordNode

/**
 * A class to check the health of predecessor.
 */
internal class HealthCheckPredecessor<Id, Value>: ChordEvent<Id, Value, ChordNode.NetworkNode<Id>> {
    override suspend fun invoke(configuration: ChordConfiguration<Id, Value>): ChordNode.NetworkNode<Id> {
        val (chordNode, _, protocol) = configuration
        return chordNode.copy(
            predecessor = chordNode.predecessor
                .takeIf { protocol.healthCheck(it) }
                ?: ChordNode.InvalidChordNode()
        )
    }
}
