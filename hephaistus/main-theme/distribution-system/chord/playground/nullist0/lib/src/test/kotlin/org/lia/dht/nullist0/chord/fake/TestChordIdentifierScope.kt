package org.lia.dht.nullist0.chord.fake

import org.lia.dht.nullist0.chord.model.ChordIdentifierScope
import org.lia.dht.nullist0.chord.model.ChordNode

class TestChordIdentifierScope(
    nodeToIdMap: Map<ChordNode.NetworkNode<UByte>, UByte> = mapOf()
) : ChordIdentifierScope<UByte>() {
    private val uriToIdMap = nodeToIdMap.mapKeys { it.key.uri }
    override fun ChordNode<UByte>.toId(): UByte {
        return when (this) {
            is ChordNode.NetworkNode -> checkNotNull(uriToIdMap[uri])
            is ChordNode.DataNode<UByte, *> -> key
            is ChordNode.InvalidChordNode -> UByte.MAX_VALUE
        }
    }
    override fun Int.toId(): UByte = toUByte()
    override fun bitSize(): Int = Int.SIZE_BITS
    override fun UByte.compareTo(other: UByte): Int = this.toInt() - other.toInt()
    override fun UByte.plus(other: UByte): UByte = (this + other).toUByte()
}
