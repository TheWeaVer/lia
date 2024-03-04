package org.example.interfaces

interface NodeIdGenerator {
    fun getId(ip: String, port: Int): Int
}