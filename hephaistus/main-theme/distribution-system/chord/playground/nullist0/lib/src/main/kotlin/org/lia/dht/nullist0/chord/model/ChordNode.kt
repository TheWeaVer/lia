package org.lia.dht.nullist0.chord.model

/**
 * A class to represent node of chord ring.
 *
 * This class will be used to represent a node from the remote.
 */
sealed class ChordNode<Id> {
    /**
     * A class to represent network node of chord ring.
     */
    data class NetworkNode<Id>(
        val uri: String,
        val successor: ChordNode<Id> = InvalidChordNode(),
        val predecessor: ChordNode<Id> = InvalidChordNode(),
        val fingers: List<ChordNode<Id>> = emptyList()
    ): ChordNode<Id>()

    /**
     * A class to represent data node of chord ring.
     */
    data class DataNode<Id, Value>(
        val key: Id,
        val value: Value
    ): ChordNode<Id>()

    /**
     * A class to represent invalid chord nodes.
     */
    class InvalidChordNode<Id>: ChordNode<Id>()
}
