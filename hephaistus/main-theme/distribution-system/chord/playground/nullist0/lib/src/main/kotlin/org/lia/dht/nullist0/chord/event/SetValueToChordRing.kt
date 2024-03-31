package org.lia.dht.nullist0.chord.event

import org.lia.dht.nullist0.chord.ChordConfiguration
import org.lia.dht.nullist0.chord.model.ChordNode

/**
 * A class to set value to chord ring with given [dataNode].
 */
internal class SetValueToChordRing<Id, Value>(
    private val dataNode: ChordNode.DataNode<Id, Value>
): ChordEvent<Id, Value, Unit> {
    override suspend fun invoke(configuration: ChordConfiguration<Id, Value>) {
        val (chordNode, _, protocol, repository) = configuration
        val findSuccessorById = FindSuccessorById<Id, Value>(dataNode.key)
        val successorNode = findSuccessorById(configuration)
        if (successorNode.uri == chordNode.uri) {
            repository.addData(dataNode)
        } else {
            protocol.putValue(successorNode, dataNode)
        }
    }
}
