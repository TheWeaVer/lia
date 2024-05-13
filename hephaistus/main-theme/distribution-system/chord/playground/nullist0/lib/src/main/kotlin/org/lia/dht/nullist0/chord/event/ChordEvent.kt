package org.lia.dht.nullist0.chord.event

import org.lia.dht.nullist0.chord.ChordConfiguration

/**
 * An interface to represent the operators of chord algorithm.
 */
internal interface ChordEvent<Id, Value, ReturnType>: Function<ReturnType> {
    /**
     * A function to implement a specific logic for each event.
     */
    suspend operator fun invoke(configuration: ChordConfiguration<Id, Value>): ReturnType
}
