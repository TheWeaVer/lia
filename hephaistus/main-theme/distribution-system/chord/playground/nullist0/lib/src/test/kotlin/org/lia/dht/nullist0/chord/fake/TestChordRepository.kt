package org.lia.dht.nullist0.chord.fake

import org.lia.dht.nullist0.chord.ChordRepository
import org.lia.dht.nullist0.chord.model.ChordNode

class TestChordRepository(
    dataSet: Map<UByte, UByte> = mapOf()
): ChordRepository<UByte, UByte> {
    private val mutableDataSet = dataSet.toMutableMap()
    override fun addData(dataNode: ChordNode.DataNode<UByte, UByte>) {
        mutableDataSet[dataNode.key] = dataNode.value
    }

    override fun getData(id: UByte): ChordNode.DataNode<UByte, UByte> {
        val value = checkNotNull(mutableDataSet[id])
        return ChordNode.DataNode(id, value)
    }
}
