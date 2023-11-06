import kotlin.math.ln

class ChordNode(m: Int){
    var fingerTable = arrayOfNulls<ChordNode>(m)
    var successor: ChordNode? = null
    var predecessor: ChordNode? = null
    var m = 0
    var nid = 0

    init{
        this.m = m
        create()
    }

    // successor 노드를 찾기
    fun findSuccessor(node: ChordNode, id: Int) :ChordNode {
//        if(this.id == id){
//            return this
//        }else{
//            val n = closestPrecedingFinger(node, id)
//            return n.findSuccessor(node, id)
//        }
    }

    private fun closestPrecedingFinger(cur: ChordNode, id: Int): ChordNode {
        for (i in m - 1 downTo 0) {
            val tempNode = fingerTable[i]
            if (tempNode != null && tempNode.id == id){
                return tempNode
            }
        }
        return cur
    }

    fun create(){
        predecessor = null
        successor = this
    }

    fun join(node: ChordNode){
        predecessor = null;
        successor = node.findSuccessor(this, id)
    }

    fun stabilize(){
        val x = successor!!.predecessor
        if (x == successor){
            successor = x
        }
        successor!!.notify(this);
    }

    fun notify(node: ChordNode){
        if (predecessor == null || node.predecessor == this)
            predecessor = node
    }

    fun fixFinger(){
        for (i in fingerTable.indices){
            fingerTable[i] = fingerTable[i]!!.findSuccessor(id)
        }
    }

    fun checkPredecessor(){
        while(true){
            predecessor = null
        }
    }
}


fun main(args: Array<String>) {
    // 1.

}