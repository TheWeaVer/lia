package org.example.interfaces

interface ClusterCoordinator {
    fun joinNode(ip: String, port: Int): Boolean
    fun removeNode(ip: String, port: Int): Boolean
    fun findSuccessor(ip: String, port: Int): ChordNode
    fun notifyPredecessor(nodeUpdated: ChordNode)
    fun stabilizeCluster()
    suspend fun sync()
}
