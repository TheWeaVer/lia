class ChordNode(val id: Int) {

    var predecessor: ChordNode? = null
    var successor: ChordNode = this
    private val fingerTable: Array<ChordNode?> = arrayOfNulls(8) // Limit array size 8.

    // Update this node's successor.
    fun updateSuccessor(newSuccessor: ChordNode) {
        successor = newSuccessor
    }

    // Update this node's predecessor.
    fun updatePredecessor(newPredecessor: ChordNode?) {
        predecessor = newPredecessor
    }

    // Find the successor of a given identifier.
    fun findSuccessor(identifier: Int): ChordNode {
        return if (isInRing(identifier, this.id, successor.id, includeEnd = true)) {
            successor
        } else {
            val node = closestPrecedingNode(identifier)
            node.findSuccessor(identifier)
        }
    }

    // Find the closest node preceding the given identifier.
    fun closestPrecedingNode(identifier: Int): ChordNode {
        for (i in fingerTable.size - 1 downTo 0) {
            val finger = fingerTable[i]
            if (finger != null && isInRing(finger.id, this.id, identifier)) {
                return finger
            }
        }
        return this
    }

    // Check in the chord ring.
    fun isInRing(
        id: Int,
        start: Int,
        end: Int,
        includeStart: Boolean = false,
        includeEnd: Boolean = false
    ): Boolean {
        return if (start < end) {
            (id > start || (id == start && includeStart)) && (id < end || (id == end && includeEnd))
        } else {
            id > start || id < end || (id == start && includeStart) || (id == end && includeEnd)
        }
    }
}
