import org.example.implement.SimpleChordNode
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*

class SimpleChordNodeTest {

    private lateinit var node1: SimpleChordNode
    private lateinit var node2: SimpleChordNode
    private lateinit var node3: SimpleChordNode

    @BeforeEach
    fun setUp() {
        // Initialize nodes with unique IDs
        node1 = SimpleChordNode("1")
        node2 = SimpleChordNode("2")
        node3 = SimpleChordNode("3")

        // Manually set up a small ring for testing
        node1.successor = node2
        node2.successor = node3
        node3.successor = node1

        node1.predecessor = node3
        node2.predecessor = node1
        node3.predecessor = node2
    }

    @Test
    fun testFindSuccessor() {
        // Test that each node finds its correct successor
        assertSame(node2, node1.findSuccessor("2"), "Node 1 should find Node 2 as its successor.")
        assertSame(node3, node2.findSuccessor("3"), "Node 2 should find Node 3 as its successor.")
        assertSame(node1, node3.findSuccessor("1"), "Node 3 should find Node 1 as its successor.")
    }

    @Test
    fun testUpdatePredecessorAndSuccessor() {
        // Create a new node and insert it between node1 and node2
        val newNode = SimpleChordNode("1.5").apply {
            predecessor = node1
            successor = node2
        }

        // Update node1 and node2 to recognize the new node
        node1.successor = newNode
        node2.predecessor = newNode

        // Verify the updates
        assertSame(newNode, node1.successor, "Node 1's successor should now be the new node.")
        assertSame(newNode, node2.predecessor, "Node 2's predecessor should now be the new node.")
    }

    @Test
    fun testClosestPrecedingNode() {
        // Assuming a simple implementation where closestPrecedingNode might just return the node itself for simplicity
        assertSame(node1, node1.closestPrecedingNode("2"), "Node 1 should find itself as the closest preceding node for ID 2.")
        assertSame(node2, node2.closestPrecedingNode("3"), "Node 2 should find itself as the closest preceding node for ID 3.")
    }
}
