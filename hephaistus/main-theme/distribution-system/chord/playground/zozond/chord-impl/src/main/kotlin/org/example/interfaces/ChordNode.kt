package org.example.interfaces

interface ChordNode {
    val ip: String
    val port: Int
    val nodeId: Int
    val fingerTable: ChordFingerTable

    var predecessor: ChordNode?
    var successor: ChordNode

    fun findSuccessor(nodeId: Int): ChordNode
    fun closestPrecedingNode(nodeId: Int): ChordNode?
}
