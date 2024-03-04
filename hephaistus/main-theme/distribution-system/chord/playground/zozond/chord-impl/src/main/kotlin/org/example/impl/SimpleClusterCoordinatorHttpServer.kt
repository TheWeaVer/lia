package org.example.impl;

import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpHandler
import com.sun.net.httpserver.HttpServer
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.InetSocketAddress
import java.nio.charset.StandardCharsets

class SimpleClusterCoordinatorHttpServer(
    private val ip: String,
    private val port: Int,
) {
    private var server: HttpServer? = null

    init {
        server = HttpServer.create(InetSocketAddress(port), 0)
        server!!.createContext("/health", HealthCheckHandler())
        server!!.executor = null // creates a default executor
        server!!.start()
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
            if (t.requestMethod.uppercase() == "POST") {
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
            } else if ("GET" == t.requestMethod) {
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
}
