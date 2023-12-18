package chord

import interfaces.Network

class ChordClient : Network {

    private val nodes = mutableListOf<ChordNode>()

    override fun addNode(node: ChordNode) {
        nodes.add(node)
        updateNodeConnections()
    }

    override fun removeNode(node: ChordNode) {
        nodes.remove(node)
        updateNodeConnections()
    }

    private fun updateNodeConnections() {
        val sortedNodes = nodes.sortedBy { it -> it.id }

        for (i in sortedNodes.indices) {
            val nextIndex = (i + 1) % sortedNodes.size
            sortedNodes[i].successor = sortedNodes[nextIndex]
            sortedNodes[nextIndex].predecessor = sortedNodes[i]
        }
    }
}
