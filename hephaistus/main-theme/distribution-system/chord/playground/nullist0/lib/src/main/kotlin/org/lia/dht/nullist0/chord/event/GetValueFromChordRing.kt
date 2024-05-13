package org.lia.dht.nullist0.chord.event

import org.lia.dht.nullist0.chord.ChordConfiguration
import org.lia.dht.nullist0.chord.model.ChordNode

/**
 * A class to get value from the chord ring with given [id].
 */
internal class GetValueFromChordRing<Id, Value>(
    private val id: Id
): ChordEvent<Id, Value, ChordNode.DataNode<Id, Value>> {
    override suspend fun invoke(configuration: ChordConfiguration<Id, Value>): ChordNode.DataNode<Id, Value> {
        val (chordNode, _, protocol, repository) = configuration
        val findSuccessorById = FindSuccessorById<Id, Value>(id)
        val successor = findSuccessorById(configuration)
        return if (successor.uri == chordNode.uri) {
            repository.getData(id)
        } else {
            ChordNode.DataNode(id, protocol.getValue(successor, id))
        }
    }
}
