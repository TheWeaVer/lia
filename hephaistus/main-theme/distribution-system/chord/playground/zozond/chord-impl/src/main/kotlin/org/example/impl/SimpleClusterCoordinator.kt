package org.example.impl

import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.utils.io.*
import kotlinx.coroutines.*
import org.example.interfaces.ChordNode
import org.example.interfaces.ClusterCoordinator
import java.util.*


@OptIn(DelicateCoroutinesApi::class)
class SimpleClusterCoordinator(private val ip: String, private val port: Int): ClusterCoordinator {
    private val node = SimpleChordNode(ip, port)
    private val dataStorage = InMemoryDataStorage()
    private val server = SimpleClusterCoordinatorHttpServer(ip, port)

    init{
        GlobalScope.launch(Dispatchers.Default){
            sync()
        }
    }

    override suspend fun sync() {
        GlobalScope.launch(Dispatchers.Default) {
            while (true) {
                val ip = "127.0.0.1"

                for(clientPort in 80..85){
                    if(clientPort == port){
                        continue
                    }
                    val isConnected = broadcastHttpRequest(ip, clientPort)

                    if(isConnected){
                        joinNode(ip, port)
                    }else{
                        removeNode(ip, port)
                    }
                }

                delay(1000)
            }
        }
    }

    private suspend fun broadcastHttpRequest(ip: String, port: Int): Boolean {
        try {
            val client = HttpClient()
            val url = "http://$ip:$port/health"
            val response: HttpResponse = client.get(url)
            if(response.status.value == 200 && response.content.readUTF8Line() == "success"){
                return true;
            }
        }catch (ignore: Exception){
        }

        return false;
    }

    override fun joinNode(ip: String, port: Int): Boolean {
        println("New Node Added! $ip:$port")
        node.addNode(ip, port)
        return true
    }

    override fun removeNode(ip: String, port: Int): Boolean {
        println("Node Removed >>> $ip:$port")
        node.removeNode(ip, port)
        return true
    }

    override fun stabilizeCluster() {
    }


    override fun findSuccessor(ip: String, port: Int): SimpleChordNode {
        return node
    }

    override fun notifyPredecessor(nodeUpdated: ChordNode) {
        // Implementation would go here
    }


}
