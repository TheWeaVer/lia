class ChordStorage {
    private val keys: MutableList<String> = mutableListOf()

    // Store a key in this node.
    fun storeKey(key: String) {
        keys.add(key)
    }

    // Retrieve a key from this node.
    fun retrieveKey(key: String): String? {
        return if (key in keys) key else null
    }
}
