package org.example.impl;

import org.example.interfaces.ChordFingerTable
import org.example.interfaces.ChordNode

class SimpleChordFingerTable(
    var successor: ChordNode,
    var precedingNode: ChordNode
) : ChordFingerTable {
    var nodes = mutableListOf<ChordNode>()

    init {
        nodes.add(successor)
    }

    override fun getNode(ip: String, port: Int): ChordNode? {
        nodes.forEach { node ->
            if (node.ip == ip && node.port == port) {
                return node
            }
        }
        return null
    }

    override fun add(chordNode: ChordNode) {
        val successor = findSuccessor(chordNode.nodeId)
        if(successor != null){
            this.successor = successor
        }
        nodes.add(chordNode)
    }

    override fun remove(chordNode: ChordNode) {
        nodes.removeIf { node -> node.ip == chordNode.ip && node.port == chordNode.port }
    }

    override fun getSuccessor(): ChordNode {
        return successor
    }

    override fun getPrecedingNode(): ChordNode? {
        return precedingNode
    }

    fun findSuccessor(id: Int): ChordNode? {
        nodes.forEach { node ->
            if (id in (node.nodeId + 1)..(node.successor.nodeId)) {
                return node.successor
            }
        }
        return null
    }
}
