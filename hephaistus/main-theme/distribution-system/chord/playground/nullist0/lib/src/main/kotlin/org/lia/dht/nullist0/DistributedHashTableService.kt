package org.lia.dht.nullist0

import java.net.URI

/**
 * An interface for distributed hash table(DHT) service. To get some [DistributedHashTable] instance,
 * client should call [create] function, or [join] function to some running DHT uri information.
 */
interface DistributedHashTableService<K, V> {
    fun create(port: Short): DistributedHashTable<K, V>

    fun join(uri: URI): DistributedHashTable<K, V>
}