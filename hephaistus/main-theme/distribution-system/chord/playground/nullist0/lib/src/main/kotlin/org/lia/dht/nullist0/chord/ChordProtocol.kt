package org.lia.dht.nullist0.chord

import org.lia.dht.nullist0.chord.model.AbstractChordNode

/**
 * An interface to provide the protocols for Chord DHT, saying Chord Ring.
 *
 * TODO: Make ID be concrete. In Chord, the ID of chord ring can be represented with other functionalities,
 *  like hashable property.
 */
interface ChordProtocol<Id, Value> {
    fun putValue(to: AbstractChordNode<Id, Value>, id: Id, value: Value)

    fun getValue(to: AbstractChordNode<Id, Value>, id: Id): Value

    fun notify(from: AbstractChordNode<Id, Value>, to: AbstractChordNode<Id, Value>)

    fun findSuccessorNode(via: AbstractChordNode<Id, Value>, id: Id): AbstractChordNode<Id, Value>

    fun findNode(node: AbstractChordNode<Id, Value>): AbstractChordNode<Id, Value>

    fun healthCheck(to: AbstractChordNode<Id, Value>): Boolean
}
