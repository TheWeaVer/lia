package org.example.implement

import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpHandler
import com.sun.net.httpserver.HttpServer
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.utils.io.*
import kotlinx.coroutines.*
import org.example.`interface`.ChordNode
import org.example.`interface`.ClusterCoordinator
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.InetSocketAddress
import java.nio.charset.StandardCharsets
import java.util.*


@OptIn(DelicateCoroutinesApi::class)
class SimpleClusterCoordinator(ip: String, p: Int): ClusterCoordinator {
    private val nodes: MutableList<SimpleChordNode> = Collections.synchronizedList(mutableListOf<SimpleChordNode>())
    private val nodeIds = Collections.synchronizedSet(mutableSetOf<String>())
    private var server: HttpServer? = null
    private var address = ip
    private var port: Int = 0

    init{
        port = p
        server = HttpServer.create(InetSocketAddress(port), 0)
        server!!.createContext("/", DataSourceHandler(nodes))
        server!!.createContext("/health", HealthCheckHandler())
        server!!.executor = null // creates a default executor
        server!!.start()

        GlobalScope.launch(Dispatchers.Default){
            sync()
        }

        println("Chord Coordinator Start! >> $address:$port")
    }

    class HealthCheckHandler : HttpHandler {
        override fun handle(t: HttpExchange) {
            val response = "success"
            t.sendResponseHeaders(200, response.length.toLong())
            val os = t.responseBody
            os.write(response.toByteArray())
            os.close()
        }
    }

    class DataSourceHandler(private val nodeList: List<SimpleChordNode>) : HttpHandler {

        override fun handle(t: HttpExchange) {
            if(t.requestMethod.uppercase() == "POST"){
                val br = BufferedReader(InputStreamReader(t.requestBody, StandardCharsets.UTF_8))
                val query = br.readLine()
                val requestJson: JSONObject = JSONObject(query)
                val index = requestJson.get("value").toString().length % nodeList.size
                val node = nodeList[index]
                node.saveData(requestJson.get("key").toString(), requestJson.get("value").toString())
                val responseJson = JSONObject()
                responseJson.put("message", "success")
                val response = responseJson.toString()
                t.sendResponseHeaders(200, response.length.toLong())
                val os = t.responseBody
                os.write(response.toByteArray())
                os.close()
            }else if("GET" == t.requestMethod){
                val key: String = t.requestURI.getPath().split("/")[1]
                val responseJson = JSONObject()
                val value = nodeList[0].findData(key)
                responseJson.put("message", value)
                val response = responseJson.toString()
                t.sendResponseHeaders(200, response.length.toLong())
                val os = t.responseBody
                os.write(response.toByteArray())
                os.close()
            }
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

                    val status = broadcastHttpRequest(ip, clientPort)
                    val nodeIdentifier = "$ip:$clientPort"
                    if(status){
                        // Connect, If Not In Ring
                        joinNode(nodeIdentifier)
                    }else{
                        // No Connect, If In Ring
                        if (nodeIdentifier in nodeIds){
                            removeNode(nodeIdentifier)
                        }
                    }
                }

                delay(1000)
            }
        }
    }

    suspend fun broadcastHttpRequest(ip: String, port: Int): Boolean {
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

    // Adds a new node to the cluster
    override fun joinNode(nodeIdentifier: String): Boolean {
        if (nodes.any { it.nodeId == nodeIdentifier } || nodes.size >= 8) {
            // Node ID already exists or cluster is full
            return false
        }

        val newNode = SimpleChordNode(nodeIdentifier, InMemoryDataStorage())
        nodes.add(newNode)
        nodeIds.add(nodeIdentifier)
        println("New Node Added! $nodeIdentifier")
        if (nodes.size > 1) {
            initializeNode(newNode)
        }

        // For simplicity, we manually invoke stabilization after adding a node
        stabilizeCluster()
        return true
    }

    // Removes a node from the cluster
    override fun removeNode(nodeIdentifier: String): Boolean {
        val nodeToRemove = nodes.find { it.nodeId == nodeIdentifier } ?: return false
        nodes.remove(nodeToRemove)
        nodeIds.remove(nodeIdentifier)
        println("Node Removed >>> $nodeIdentifier")

        // Reassign the successor and predecessor of the removed node's neighbors
        nodeToRemove.predecessor?.successor = nodeToRemove.successor
        nodeToRemove.successor.predecessor = nodeToRemove.predecessor

        stabilizeCluster()
        return true
    }

    override fun findSuccessor(nodeIdentifier: String): SimpleChordNode {
        // In a real implementation, this would use a more efficient method
        val sortedNodes = nodes.sortedBy { it.nodeId }
        return sortedNodes.firstOrNull { it.nodeId >= nodeIdentifier }
            ?: sortedNodes.first() // Circular property of the ring
    }

    override fun notifyPredecessor(nodeUpdated: ChordNode) {
        // Implementation would go here
    }

    override fun stabilizeCluster() {
        val sortedNodes = nodes.sortedBy { it.nodeId }
        sortedNodes.forEachIndexed { index, node ->
            val successorIndex = if (index + 1 < sortedNodes.size) index + 1 else 0 // Circular list
            node.successor = sortedNodes[successorIndex]
            sortedNodes[successorIndex].predecessor = node
        }
    }

    // Initializes a newly joined node's successor and predecessor
    private fun initializeNode(node: SimpleChordNode) {
        val successor = findSuccessor(node.nodeId)
        node.successor = successor
        node.predecessor = successor.predecessor
        successor.predecessor = node
        node.predecessor?.successor = node
    }

}
