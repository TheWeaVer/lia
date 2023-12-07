class ChordNode(address: String) : Node {
    val id: String

    private val address: String
    private val finger = arrayListOf<ChordNode>()
    private var successor: ChordNode = this
    private var predecessor: ChordNode? = null

    init {
        this.address = address
        this.id = Hasher.getHashCode(address)
    }

    override fun join(node: ChordNode) {
        predecessor = null
        successor = node.findSuccessor(id)
        updateFingerTable(node)
    }

    override fun findSuccessor(id: String): ChordNode {
        // 1. 핑거 테이블 안에 저장된 노드들의 ID를 전부 비교한다
        for (node in finger) {
            if (node.id == id) {
                return successor
            }
        }

        // 2. 만약 핑거 테이블 안에 저장된 노드들의 ID에 없다면 전임자를 찾아 요청한다
        val n0 = closestPrecedingNode(id)
        if (n0 == successor) {
            return successor
        }
        return n0.findSuccessor(id)
    }

    override fun closestPrecedingNode(id: String): ChordNode {
        for (node in finger.reversed()) {
            if (node != successor) {
                return node.findSuccessor(id)
            }
        }
        return this
    }

    override fun leave(id: String) {
        for (node in finger) {
            node.leave(id)
        }
        finger.removeIf { it -> it.id == id }
    }


    fun updateFingerTable(node: ChordNode) {
        finger.add(node)
    }

}