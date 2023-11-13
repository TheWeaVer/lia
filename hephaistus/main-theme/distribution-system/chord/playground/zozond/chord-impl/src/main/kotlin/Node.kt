interface Node {
    fun findSuccessor(id: String): Node
    fun closestPrecedingNode(id: String): Node
    fun join(node: ChordNode)
    fun leave(id: String)
}