package com.tunneld.ipdiali.shared.core.application.event

import com.tunneld.ipdiali.shared.core.application.infrastructure.log.Logger
import com.tunneld.ipdiali.shared.core.domain.event.DomainEvent
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow

internal class SharedFlowEventBus(private val logger: Logger) : EventBus {
    private val _events =
        MutableSharedFlow<DomainEvent>(
            extraBufferCapacity = 50,
            onBufferOverflow = BufferOverflow.DROP_LATEST,
        )

    override val events: Flow<DomainEvent> = _events

    override fun publish(event: DomainEvent) {
        // We can't suspend here so we use tryEmit. If it fails, the event is dropped.
        // Other solution would be to use separate coroutine scope for the event bus and launch a
        // new coroutine for each event, but that would be overkill for now probably.
        // There might be a better way to handle this in the future.

        if (_events.tryEmit(event)) {
            logger.d(TAG) { "Posted an event ${event::class}" }
        } else {
            logger.w(TAG) { "Failed to post an event ${event::class}" }
        }
    }

    private companion object {
        const val TAG = "SharedFlowEventBus"
    }
}
