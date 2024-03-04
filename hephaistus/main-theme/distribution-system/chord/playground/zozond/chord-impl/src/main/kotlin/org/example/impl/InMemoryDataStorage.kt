package org.example.impl

import org.example.interfaces.DataStorage

class InMemoryDataStorage : DataStorage {
    private val storage = mutableMapOf<String, String>()

    override fun put(key: String, value: String): Boolean {
        storage[key] = value
        return true
    }

    override fun get(key: String): String? {
        return storage[key]
    }

    override fun remove(key: String): Boolean {
        return storage.remove(key) != null
    }
}
