package org.lia.dht.nullist0.chord

import org.lia.dht.nullist0.chord.model.ChordNode

/**
 * TODO: Make ID be concrete. In Chord, the ID of chord ring can be represented with other functionalities,
 *  like hashable property.
 */
interface ChordRepository<ID, Value> {
    fun getValue(id: ID, from: ChordNode): Value

    fun putValue(id: ID, value: Value, to: ChordNode)

    fun findSuccessor(origin: ChordNode, successor: ChordNode, finger: List<ChordNode>, id: ID): ChordNode

    fun findPredecessor(from: ChordNode): ChordNode

    fun findFingers(node: ChordNode) : List<ChordNode>

    fun healthCheck(node: ChordNode): Boolean

    fun notify(to: ChordNode, about: ChordNode)
}
