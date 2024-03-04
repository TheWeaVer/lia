package org.lia.dht.nullist0.chord.model

import java.net.URI


/**
 * An abstract class to represent the node in a chord ring.
 */
abstract class AbstractChordNode<Id, Value> {
    abstract val uri: URI
    abstract val predecessorOrNull: AbstractChordNode<Id, Value>?
    abstract val successorOrNull: AbstractChordNode<Id, Value>?

    fun toChordNode(): ChordNode<Id, Value> {
        return ChordNode(
            uri = uri,
            successorOrNull = successorOrNull?.let { successor ->
                ChordNode(
                    uri = uri,
                    successorOrNull = successor.successorOrNull?.toChordNode(),
                    predecessorOrNull = successor.predecessorOrNull?.toChordNode()
                )
            },
            predecessorOrNull = predecessorOrNull?.let { predecessor ->
                ChordNode(
                    uri = uri,
                    successorOrNull = predecessor.successorOrNull?.toChordNode(),
                    predecessorOrNull = predecessor.predecessorOrNull?.toChordNode()
                )
            }
        )
    }

    /**
     * A class to represent the immutable [AbstractChordNode].
     *
     * This class will be used to represent a node from the remote.
     */
    class ChordNode<Id, Value>(
        override val uri: URI,
        override val successorOrNull: ChordNode<Id, Value>?,
        override val predecessorOrNull: ChordNode<Id, Value>?
    ): AbstractChordNode<Id, Value>()

    /**
     * A class to represent the mutable [AbstractChordNode].
     *
     * This class will be used to represent a node from the local.
     */
    class MutableChordNode<Id, Value>(
        override val uri: URI,
        override var successorOrNull: ChordNode<Id, Value>?,
        override var predecessorOrNull: ChordNode<Id, Value>?,
        val chordRingSizeInBit: Int
    ): AbstractChordNode<Id, Value>() {
        private val mutableFingers: MutableList<ChordNode<Id, Value>?> =
            List(chordRingSizeInBit) { toChordNode() }.toMutableList()
        val fingers: List<ChordNode<Id, Value>?> = mutableFingers

        fun fixFinger(index: Int, finger: ChordNode<Id, Value>) {
            require(index in 0 until chordRingSizeInBit)
            mutableFingers[index] = finger
        }
    }
}
