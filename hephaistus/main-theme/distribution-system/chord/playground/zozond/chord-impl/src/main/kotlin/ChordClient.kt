class ChordClient(private val currentNode: ChordNode) {

    fun create() {
        currentNode.updatePredecessor(null)
        currentNode.updateSuccessor(currentNode) // Successor is itself.
        println("Created a new Chord ring with ${currentNode.id}.")
    }

    fun join(knownNode: ChordNode) {
        currentNode.updatePredecessor(null)
        currentNode.updateSuccessor(knownNode.findSuccessor(currentNode.id))
        println("${currentNode.id} > ${knownNode.id} has joined the ring.")
    }

    fun findSuccessor(identifier: Int): ChordNode {
        return currentNode.findSuccessor(identifier)
    }

    fun stabilize() {
        val x = currentNode.successor.predecessor
        if (x != null && currentNode.isInRing(x.id, currentNode.id, currentNode.successor.id)) {
            currentNode.updateSuccessor(x)
        }
        notify(currentNode)
    }

    // Notify the successor that this node should be its predecessor.
    fun notify(node: ChordNode) {
        if (currentNode.predecessor == null || currentNode.isInRing(
                node.id,
                currentNode.predecessor!!.id,
                currentNode.id
            )
        ) {
            currentNode.updatePredecessor(node)
        }
    }

    fun getNode(): ChordNode {
        return this.currentNode
    }
}
