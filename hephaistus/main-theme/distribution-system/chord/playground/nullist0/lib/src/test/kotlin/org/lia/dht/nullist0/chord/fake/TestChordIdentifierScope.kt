package org.lia.dht.nullist0.chord.fake

import org.lia.dht.nullist0.chord.model.ChordIdentifierScope
import org.lia.dht.nullist0.chord.model.ChordNode

class TestChordIdentifierScope : ChordIdentifierScope<Int>() {
    override fun ChordNode<Int>.toId(): Int  = error("Not supported")
    override fun Int.toId(): Int  = error("Not supported")
    override fun bitSize(): Int  = error("Not supported")
    override fun Int.compareTo(other: Int): Int  = compareTo(other)
    override fun Int.plus(other: Int): Int = error("Not supported")
}
