package org.example.implement;

import org.example.`interface`.ChordNode
import org.example.`interface`.DataStorage

class SimpleChordNode(override val nodeId: String,
                      override val dataStorage: DataStorage): ChordNode {
    private val m = 8
    override val fingerTable: MutableList<ChordNode?> = MutableList(m) { null }

    override var predecessor: ChordNode? = null
    override var successor: ChordNode = this

    override fun findSuccessor(id: String): ChordNode {
        if (id == nodeId) return this
        var currentNode: ChordNode = this
        while (!isIdInRange(id, currentNode.nodeId, currentNode.successor.nodeId, inclusiveEnd = true)) {
            currentNode = currentNode.closestPrecedingNode(id)
        }
        return currentNode.successor
    }

    // Finds the closest node preceding the given ID in the finger table
    override fun closestPrecedingNode(id: String): ChordNode {
        for (i in m - 1 downTo 0) {
            val finger = fingerTable[i]
            if (finger != null && isIdInRange(finger.nodeId, this.nodeId, id, inclusiveEnd = false)) {
                return finger
            }
        }
        return this // If no closer node is found, return self
    }

    // Updates the finger table with a new node at the specified index
    override fun updateFingerTable(node: ChordNode, i: Int) {
        if (i < 0 || i >= m) return // Ensure the index is within the finger table bounds
        fingerTable[i] = node
    }

    // Updates the predecessor of the current node
    override fun updatePredecessor(node: ChordNode?) {
        predecessor = node
    }

    // Updates the successor of the current node
    override fun updateSuccessor(node: ChordNode) {
        successor = node
    }

    // Utility function to check if a given ID falls within a specified range in the ID space
    private fun isIdInRange(id: String, start: String, end: String, inclusiveEnd: Boolean): Boolean {
        val idInt = id.toInt()
        val startInt = start.toInt()
        val endInt = end.toInt()

        return if (startInt < endInt) {
            idInt > startInt && (idInt < endInt || (inclusiveEnd && idInt == endInt))
        } else if (startInt > endInt) {
            idInt > startInt || (idInt < endInt || (inclusiveEnd && idInt == endInt))
        } else {
            idInt == startInt && inclusiveEnd
        }
    }

    override fun findData(key: String): String? {
        return dataStorage.get(key)
    }

    override fun saveData(key: String, value: String) {
        dataStorage.put(key, value)
    }
}
