package org.lia.dht.nullist0.chord.event

import org.jetbrains.annotations.VisibleForTesting
import org.lia.dht.nullist0.chord.ChordConfiguration
import org.lia.dht.nullist0.chord.model.ChordNode
import kotlin.math.pow

/**
 * A class to fix fingers with given [pow] of the identifier.
 */
internal class FixFingers<Id, Value>(
    private val pow: Int
): ChordEvent<Id, Value, ChordNode.NetworkNode<Id>> {
    override suspend fun invoke(configuration: ChordConfiguration<Id, Value>): ChordNode.NetworkNode<Id> {
        val (chordNode, identifierScope) = configuration
        val positivePower = if (pow < 0) pow + Int.MAX_VALUE else pow
        val clampedPower = positivePower % identifierScope.bitSize()
        val twoToPower = 2.0.pow(clampedPower.toDouble()).toInt()
        return with(identifierScope) {
            val id = chordNode.toId() + twoToPower.toId()
            val findSuccessorById = FindSuccessorById<Id, Value>(id)
            val finger = findSuccessorById(configuration)
            chordNode.copy(
                fingers = chordNode.fingers
                    .toMutableList()
                    .apply { this[pow] = finger }
                    .toList()
            )
        }
    }
}
