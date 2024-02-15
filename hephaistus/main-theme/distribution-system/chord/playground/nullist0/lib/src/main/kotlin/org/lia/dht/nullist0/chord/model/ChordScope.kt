package org.lia.dht.nullist0.chord.model

/**
 * An interface to build identifier of Chord Ring.
 */
abstract class ChordScope<Id, Value> {
    abstract fun AbstractChordNode<Id, Value>.toId(): Id

    abstract fun bitSize(): Int

    abstract fun Int.toId(): Id

    /**
     * The [Id] in a chord ring, is an element of integers modulo n, where a natural number n is a power of 2.
     * There is a natural addition in such set, so this function will implement that addition.
     */
    abstract infix operator fun Id.plus(other: Id): Id

    /**
     * The [Id] in a chord ring, is an element of integers modulo n, where a natural number n is a power of 2.
     * Since there is no order in the such set, this function will implement an order on the integer set,
     * not on the modulo set. In other words, this will return the natural order of integers.
     */
    protected abstract infix operator fun Id.compareTo(other: Id): Int

    infix fun Id.inOpenInterval(pair: Pair<Id, Id>): Boolean  {
        val id = this@inOpenInterval
        val (start, end) = if (pair.first < pair.second) pair else pair.second to pair.first
        return start < id && id < end
    }

    infix fun Id.inOpenCloseInterval(pair: Pair<Id, Id>): Boolean {
        val id = this@inOpenCloseInterval
        val (start, end) = if (pair.first < pair.second) pair else pair.second to pair.first
        return start < id && id <= end
    }
}
