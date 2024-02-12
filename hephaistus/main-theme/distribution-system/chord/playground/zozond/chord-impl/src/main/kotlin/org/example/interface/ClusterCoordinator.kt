package org.example.`interface`

interface ClusterCoordinator {
    fun joinNode(nodeIdentifier: String): Boolean
    fun removeNode(nodeIdentifier: String): Boolean
    fun findSuccessor(nodeIdentifier: String): ChordNode
    fun notifyPredecessor(nodeUpdated: ChordNode)
    fun stabilizeCluster()
    suspend fun sync()
}