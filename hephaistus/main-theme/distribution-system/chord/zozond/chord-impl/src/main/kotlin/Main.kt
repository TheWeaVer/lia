
fun main(args: Array<String>) {

    val node1 = ChordNode("1")
    val node2 = ChordNode("2")
    val node3 = ChordNode("3")
    node1.join(node2)
    node1.join(node3)

    node1.leave("3")

    println()

}