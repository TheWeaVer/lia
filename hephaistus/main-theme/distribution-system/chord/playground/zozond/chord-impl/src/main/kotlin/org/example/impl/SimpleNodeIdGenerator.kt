package org.example.impl

import org.example.interfaces.NodeIdGenerator

class SimpleNodeIdGenerator: NodeIdGenerator {
    override fun getId(ip: String, port: Int): Int {
        val combined = "$ip:$port"
        return combined.hashCode()
    }
}