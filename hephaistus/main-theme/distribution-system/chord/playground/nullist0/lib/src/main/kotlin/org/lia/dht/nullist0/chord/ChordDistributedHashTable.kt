package org.lia.dht.nullist0.chord

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import org.lia.dht.nullist0.DistributedHashTable
import org.lia.dht.nullist0.chord.event.*
import org.lia.dht.nullist0.chord.model.ChordNode
import org.lia.dht.nullist0.chord.utils.FlowUtils
import java.io.Closeable
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

/**
 * An implementation of [DistributedHashTable] for CHORD algorithm.
 */
internal class ChordDistributedHashTable<Id, Value>(
    initialConfiguration: ChordConfiguration<Id, Value>,
    coroutineScope: CoroutineScope,
    repeatDuration: Duration = DEFAULT_REPEAT_DURATION
): DistributedHashTable<Id, Value>, Closeable {
    private val mutableConfigurationFlow = MutableStateFlow(initialConfiguration)
    private val stabilizationJob = coroutineScope.launch {
        FlowUtils
            .infiniteFlowWithDelay(repeatDuration) { it }
            .collect { stabilize(it) }
    }

    private suspend fun stabilize(index: Int) {
        mutableConfigurationFlow.update {
            val performStabilization = PerformStabilization<Id, Value>(index)
            performStabilization(it)
        }
    }

    override fun get(key: Id): Value {
        return runBlocking {
            val configuration = mutableConfigurationFlow.value
            val getValueFromChordRing = GetValueFromChordRing<Id, Value>(key)
            getValueFromChordRing(configuration).value
        }
    }

    override fun set(key: Id, value: Value) {
        runBlocking {
            val configuration = mutableConfigurationFlow.value
            val setValueToChordRing = SetValueToChordRing(ChordNode.DataNode(key, value))
            setValueToChordRing(configuration)
        }
    }

    override fun close() {
        runBlocking {
            stabilizationJob.cancelAndJoin()
        }
    }

    companion object {
        private val DEFAULT_REPEAT_DURATION = 1.seconds
    }
}
