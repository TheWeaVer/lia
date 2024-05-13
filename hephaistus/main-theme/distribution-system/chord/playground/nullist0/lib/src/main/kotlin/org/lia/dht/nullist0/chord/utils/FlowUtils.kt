package org.lia.dht.nullist0.chord.utils

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlin.time.Duration


/**
 * A helper object for the flow of chord algorithm.
 */
internal object FlowUtils {
    /**
     * A function to create the infinite flow with the given delay.
     *
     * @param block - this will provide to create the value from the infinite integer.
     */
    fun <T> infiniteFlowWithDelay(repeatDuration: Duration, block: suspend (Int) -> T) = flow {
        var count = 0
        while(true) {
            val next = block(count)
            emit(next)
            delay(repeatDuration)
            count += 1
        }
    }
}
