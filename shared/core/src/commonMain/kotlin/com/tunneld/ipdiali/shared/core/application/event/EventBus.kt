package com.tunneld.ipdiali.shared.core.application.event

import com.tunneld.ipdiali.shared.core.domain.event.DomainEvent
import kotlinx.coroutines.flow.Flow

interface EventBus {
    val events: Flow<DomainEvent>

    fun publish(event: DomainEvent)
}
