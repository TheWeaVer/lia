package org.lia.dht.nullist0.chord

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.lia.dht.nullist0.DistributedHashTable
import java.net.URI
import kotlin.random.Random
import kotlin.test.Ignore
import kotlin.test.assertEquals

@Ignore("This will be resolved when the first implementation is ready")
class ChordDistributedHashTableServiceTest {

    private lateinit var service: ChordDistributedHashTableService<String, Int>

    @BeforeEach
    fun setUp() {
        service = ChordDistributedHashTableService()
    }

    @Test
    fun testCreateAndUse() {
        // given
        val dummies = aDummy()
        val dht = service.create(aPort())

        // when
        dummies.forEach { (key, value) ->
            dht[key] = value
        }

        // then
        dummies.forEach { (key, value) ->
            assertEquals(value, dht[key])
        }
    }

    @Test
    fun testCreateAndJoin_setByHost_twoPool() {
        // given
        val dummies = aDummy()
        val port = aPort()
        val host = service.create(port)
        val client = service.join(aLoopbackUri(port))

        // when
        dummies.forEach { (key, value) ->
            host[key] = value
        }

        // then: all values should be found by host and client
        dummies.forEach { (key, value) ->
            assertEquals(value, client[key])
            assertEquals(value, host[key])
        }
    }

    @Test
    fun testCreateAndJoin_setByClient_twoPool() {
        // given
        val dummies = aDummy()
        val port = aPort()
        val host = service.create(port)
        val client = service.join(aLoopbackUri(port))

        // when
        dummies.forEach { (key, value) ->
            client[key] = value
        }

        // then: all values should be found by host and clients
        dummies.forEach { (key, value) ->
            assertEquals(value, client[key])
            assertEquals(value, host[key])
        }
    }

    @Test
    fun testCreateAndJoin_set_multiplePool() {
        // given
        val dummies = aDummy()
        val port = aPort()
        val host = service.create(port)
        val clients = aClients(aLoopbackUri(port))

        // when
        dummies.forEach { (key, value) ->
            host[key] = value
        }

        // then: all values should be found by host and clients
        dummies.forEach { (key, value) ->
            assertEquals(value, host[key])
            clients.forEach { assertEquals(value, it[key]) }
        }
    }

    @Test
    fun testCreateAndJoin_set_randomly() {
        // given
        val dummies = aDummy()
        val port = aPort()
        val host = service.create(port)
        val clients = aClients(aLoopbackUri(port))

        // when
        dummies.forEach { (key, value) ->
            val index = aInt(clients.size)
            val client = clients[index]
            client[key] = value
        }

        // then: all values should be found by host and clients
        dummies.forEach { (key, value) ->
            assertEquals(value, host[key])
            clients.forEach { assertEquals(value, it[key]) }
        }
    }

    private fun aDummy(len: Int = 100): Map<String, Int> = List(len) { aKey() to aValue() }.toMap()

    private fun aKey(len: Int = 100): String =
        List(len) { ('a'..'z').random() }.joinToString("")

    private fun aValue(min: Int = 0, max: Int = 100): Int = Random.nextInt(min, max)

    private fun aInt(max: Int = 100): Int = Random.nextInt(max)

    private fun aPort(): Short = Random.nextInt(Short.MAX_VALUE.toInt()).toShort()

    private fun aClients(uri: URI, len: Int = 100): List<DistributedHashTable<String, Int>> =
        List(len) { service.join(uri) }

    private fun aLoopbackUri(port: Short): URI = URI.create("http://127.0.0.1:$port")
}