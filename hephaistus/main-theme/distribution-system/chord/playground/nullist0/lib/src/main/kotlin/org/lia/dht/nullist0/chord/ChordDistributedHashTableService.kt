package org.lia.dht.nullist0.chord

import org.lia.dht.nullist0.DistributedHashTable
import org.lia.dht.nullist0.DistributedHashTableService
import java.net.URI

/**
 * An implementation of [DistributedHashTableService] for CHORD algorithm.
 */
internal class ChordDistributedHashTableService<K, V>: DistributedHashTableService<K, V> {
    override fun create(port: Short): DistributedHashTable<K, V> {
        TODO("Not yet implemented")
    }

    override fun join(uri: URI): DistributedHashTable<K, V> {
        TODO("Not yet implemented")
    }
}
