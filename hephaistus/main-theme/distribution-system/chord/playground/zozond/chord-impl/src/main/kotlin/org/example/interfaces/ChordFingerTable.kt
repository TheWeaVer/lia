package org.example.interfaces

interface ChordFingerTable {

    fun add(chordNode: ChordNode)
    fun remove(chordNode: ChordNode)
    fun getSuccessor(): ChordNode
    fun getPrecedingNode(): ChordNode?
    fun getNode(ip:String, port: Int): ChordNode?
}