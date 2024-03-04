package org.example.impl;

import org.example.interfaces.ChordNode

class SimpleChordNode(override val ip: String,
                      override val port: Int): ChordNode {
    override var predecessor: ChordNode? = null
    override var successor: ChordNode = this
    override var nodeId: Int = 0
    override val fingerTable = SimpleChordFingerTable()

    private val nodeIdGenerator = SimpleNodeIdGenerator()

    init {
        this.nodeId  = nodeIdGenerator.getId(ip, port)
    }

    override fun findSuccessor(nodeId: Int): ChordNode {
        return successor;
    }

    override fun closestPrecedingNode(nodeId: Int): ChordNode? {
        return predecessor;
    }

    fun addNode(ip: String, port: Int){
        fingerTable.add(SimpleChordNode(ip, port))
        successor = fingerTable.getSuccessor()
        predecessor = fingerTable.getPrecedingNode()
    }

    fun removeNode(ip: String, port: Int){
        val deleteNode = fingerTable.getNode(ip, port)
        fingerTable.remove(deleteNode)
        successor = fingerTable.getSuccessor()
        predecessor = fingerTable.getPrecedingNode()
    }

}
