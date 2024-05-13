package org.lia.dht.nullist0.chord.fake

import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking
import org.lia.dht.nullist0.chord.ChordProtocol
import org.lia.dht.nullist0.chord.model.ChordNode

class TestChordProtocol(
    private val identifierScope: TestChordIdentifierScope,
    nodeToDataMap: Map<ChordNode.NetworkNode<UByte>, Map<UByte, UByte>> = mapOf()
): ChordProtocol<UByte, UByte> {
    private val mutableNoteToDataMap = nodeToDataMap.mapValues { it.value.toMutableMap() }.toMutableMap()
    override fun putValue(to: ChordNode<UByte>, data: ChordNode.DataNode<UByte, UByte>) {
        val dataMap = checkNotNull(mutableNoteToDataMap[to])
        dataMap[data.key] = data.value
    }

    override fun getValue(to: ChordNode<UByte>, id: UByte): UByte {
        val dataMap = checkNotNull(mutableNoteToDataMap[to])
        val value = checkNotNull(dataMap[id])
        return value
    }

    override fun notify(from: ChordNode<UByte>, to: ChordNode<UByte>) = Unit

    override fun findSuccessorNode(
        via: ChordNode<UByte>,
        id: UByte
    ): ChordNode.NetworkNode<UByte> = with(identifierScope) {
        val idToDataMap = mutableNoteToDataMap.map { it.key.toId() to (it.key to it.value) }.toMap()
        val range = flow {
            var count = 0
            while(true) {
                val newId = id + count.toUInt()
                emit(newId.toUByte())
                count += 1
            }
        }
        val key = runBlocking { range.first { idToDataMap[it] != null } }
        return checkNotNull(idToDataMap[key]).first
    }

    override fun findNode(node: ChordNode<UByte>): ChordNode.NetworkNode<UByte> {
        require(node is ChordNode.NetworkNode)
        val nodeFromMap = mutableNoteToDataMap.filterKeys { it.uri == node.uri }.keys.first()
        return nodeFromMap
    }

    override fun healthCheck(to: ChordNode<UByte>): Boolean {
        return mutableNoteToDataMap[to] != null
    }

    override fun removeValue(to: ChordNode<UByte>, id: UByte) {
        TODO("Not yet implemented")
    }
}
