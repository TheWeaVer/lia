package org.lia.dht.nullist0.chord.model

/**
 * An abstract class to build or evaluate the identifier of chord ring. This will provide the context about
 * identifier of chord ring. There are functionalities like [compareTo], [plus] or, [toId] with [Id].
 *
 * ```kotlin
 * val scope: ChordIdentifier = // ..
 * with(scope) {
 *     id + otherId
 *     id < otherId
 *     // do something with ids.
 * }
 * ```
 */
abstract class ChordIdentifierScope<Id, Value> {
    abstract fun ChordNode<Id>.toId(): Id

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
        val (start, end) = pair
        return if (start <= end) start < id && id < end else id < end || start < id
    }

    infix fun Id.inOpenCloseInterval(pair: Pair<Id, Id>): Boolean {
        val id = this@inOpenCloseInterval
        val (start, end) = pair
        return if (start <= end) start < id && id <= end else id <= end || start < id
    }
}
