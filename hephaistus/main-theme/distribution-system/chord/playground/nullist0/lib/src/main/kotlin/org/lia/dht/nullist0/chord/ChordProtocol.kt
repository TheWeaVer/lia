package org.lia.dht.nullist0.chord

import org.lia.dht.nullist0.chord.model.ChordNode

/**
 * An interface to provide the protocols for Chord DHT, saying Chord Ring.
 *
 * The functions of this interface will perform without checking whether the node is correct. For example, [putValue]
 * will be performed without checking whether the given data should save the given node.
 *
 * TODO: Make ID be concrete. In Chord, the ID of chord ring can be represented with other functionalities,
 *  like hashable property.
 */
interface ChordProtocol<Id, Value> {
    fun putValue(to: ChordNode<Id>, data: ChordNode.DataNode<Id, Value>)

    fun getValue(to: ChordNode<Id>, id: Id): Value

    fun removeValue(to: ChordNode<Id>, id: Id)

    fun notify(from: ChordNode<Id>, to: ChordNode<Id>)

    fun findSuccessorNode(via: ChordNode<Id>, id: Id): ChordNode.NetworkNode<Id>

    fun findNode(node: ChordNode<Id>): ChordNode.NetworkNode<Id>

    fun healthCheck(to: ChordNode<Id>): Boolean
}
