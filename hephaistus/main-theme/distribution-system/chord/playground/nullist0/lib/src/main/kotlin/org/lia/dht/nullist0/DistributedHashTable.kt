package org.lia.dht.nullist0

/**
 * An interface to access the key-value hash table with distributed fashion.
 *
 * This will be used in the client classes. The client can get the value with [get] and set a value with [set].
 */
interface DistributedHashTable<K, V> {
    operator fun get(key: K): V

    operator fun set(key: K, value: V)
}