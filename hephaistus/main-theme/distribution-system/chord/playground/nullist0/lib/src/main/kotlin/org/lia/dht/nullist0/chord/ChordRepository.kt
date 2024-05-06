package org.lia.dht.nullist0.chord

import org.lia.dht.nullist0.chord.model.ChordNode

/**
 * An interface to abstract the local data source for the chord ring.
 */
interface ChordRepository<Id, Value> {
    fun addData(dataNode: ChordNode.DataNode<Id, Value>)
    fun getData(id: Id): ChordNode.DataNode<Id, Value>
    fun removeData(id: Id)
}
