package org.example.`interface`

interface ChordNode {
    val nodeId: String
    val dataStorage: DataStorage
    val fingerTable: MutableList<ChordNode?>
    var predecessor: ChordNode?
    var successor: ChordNode

    fun findSuccessor(id: String): ChordNode
    fun closestPrecedingNode(id: String): ChordNode
    fun updateFingerTable(node: ChordNode, i: Int)
    fun updatePredecessor(node: ChordNode?)
    fun updateSuccessor(node: ChordNode)

    fun findData(key: String): String?
    fun saveData(key: String, value: String)
}