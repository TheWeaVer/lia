package org.example.interfaces

interface DataStorage {
    fun put(key: String, value: String): Boolean
    fun get(key: String): String?
    fun remove(key: String): Boolean
}
