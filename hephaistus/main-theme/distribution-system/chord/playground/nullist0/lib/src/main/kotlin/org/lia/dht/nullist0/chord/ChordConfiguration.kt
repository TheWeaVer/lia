package org.lia.dht.nullist0.chord

import org.lia.dht.nullist0.chord.model.ChordIdentifierScope
import org.lia.dht.nullist0.chord.model.ChordNode

/**
 * A data class to hold the configuration of the chord ring.
 */
data class ChordConfiguration<Id, Value>(
    val node: ChordNode.NetworkNode<Id>,
    val identifierScope: ChordIdentifierScope<Id>,
    val protocol: ChordProtocol<Id, Value>,
    val repository: ChordRepository<Id, Value>
) {
    fun updateConfiguration(newNode: ChordNode.NetworkNode<Id>): ChordConfiguration<Id, Value> {
        return copy(node = newNode)
    }
}
