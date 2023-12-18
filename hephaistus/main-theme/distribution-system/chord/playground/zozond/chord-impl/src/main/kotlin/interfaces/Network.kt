package interfaces

import chord.ChordNode

interface Network {
    fun addNode(node: ChordNode)

    fun removeNode(node: ChordNode)
}
