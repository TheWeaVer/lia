package org.lia.dht.nullist0.chord.model

/**
 * An interface to build identifier of Chord Ring.
 */
interface ChordScope<Id, Value> {
    fun AbstractChordNode<Id, Value>.toId(): Id

    fun bitSize(): Int

    fun Int.toId(): Id

    infix operator fun Id.plus(other: Id): Id

    infix operator fun Id.compareTo(other: Id): Int
}
