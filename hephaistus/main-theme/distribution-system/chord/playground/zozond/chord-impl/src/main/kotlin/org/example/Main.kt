package org.example

import org.example.impl.SimpleClusterCoordinator
import kotlinx.coroutines.delay

suspend fun main(args: Array<String>) {
    var port = 80
    var ip = "127.0.0.1"

    // --address=
    // --port=
    for(arg in args){
        val splitted = arg.split("=")

        if(splitted[0] == "--address"){
            ip = splitted[1]
        }else if(splitted[0] == "--port"){
            port = Integer.parseInt(splitted[1])
        }
    }

    SimpleClusterCoordinator(ip, port)
    println("Chord Server running on port $port...")
    delay(1000)
}
