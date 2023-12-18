package chord

import interfaces.Chord

class ChordNode(val id: Int) : Chord {
    var successor: ChordNode? = null
    var predecessor: ChordNode? = null

    private val data: MutableMap<String, String> = mutableMapOf()

    override fun findSuccessor(keyId: Int): ChordNode {
        var chordNode = this
        while (keyId !in chordNode.id..successor!!.id) {
            chordNode = chordNode.successor!!
        }
        return chordNode.successor!!
    }

    fun store(key: String, value: String) {
        findSuccessor(hashKey(key)).data[key] = value
    }

    fun retrieve(key: String): String? {
        return findSuccessor(hashKey(key)).data[key]
    }

    private fun hashKey(key: String): Int {
        return key.hashCode() % 8 + 1 // Simplified hash function for example
    }

}
