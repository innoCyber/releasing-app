@file:Suppress("EXPERIMENTAL_API_USAGE")

package ptml.releasing.app.eventbus

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.filter
import kotlinx.coroutines.channels.map
import kotlinx.coroutines.launch
import ptml.releasing.app.utils.AppCoroutineDispatchers
import kotlin.coroutines.CoroutineContext

/**
 * You can use like this.
 * val channel = EventBus().asChannel<ItemChangeAction>()
 * launch (UI){
 *   for(action in channel){
 *     // You can use item
 *     action.item
 *   }
 * }
 */
@Suppress("DEPRECATION")
class EventBus(
    private val dispatchers: AppCoroutineDispatchers
) : CoroutineScope {
    val bus: BroadcastChannel<Any> = BroadcastChannel(1)

    override val coroutineContext: CoroutineContext
        get() = dispatchers.main

    fun send(o: Any) {
        launch {
            bus.send(o)
        }
    }

    inline fun <reified T> asChannel(): ReceiveChannel<T> {
        return bus.openSubscription().filter { it is T }.map { it as T }
    }

    companion object {
        private val lock = Any()
        private var INSTANCE: EventBus? = null

        fun getInstance(dispatchers: AppCoroutineDispatchers): EventBus {
            if (INSTANCE == null) {
                synchronized(lock) {
                    if (INSTANCE == null) {
                        INSTANCE = EventBus(dispatchers)
                    }
                    return INSTANCE as EventBus
                }
            }
            return INSTANCE as EventBus
        }
    }
}