package org.lia.dht.nullist0.chord.event

import org.lia.dht.nullist0.chord.ChordConfiguration
import org.lia.dht.nullist0.chord.model.ChordNode

/**
 * A class to perform stabilization with the given [fixFingerIndex].
 */
internal class PerformStabilization<Id, Value>(
    private val fixFingerIndex: Int
): ChordEvent<Id, Value, ChordConfiguration<Id, Value>> {

    override suspend fun invoke(
        configuration: ChordConfiguration<Id, Value>
    ): ChordConfiguration<Id, Value> {
        return listOf<ChordEvent<Id, Value, ChordNode.NetworkNode<Id>>>(
            StabilizeNode(),
            FixFingers(fixFingerIndex),
            HealthCheckPredecessor()
        ).fold(configuration) { acc, chordEvent ->
            configuration.updateConfiguration(chordEvent(acc))
        }
    }
}
