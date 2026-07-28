package com.tunneld.ipdiali.shared.core.application.event

import com.tunneld.ipdiali.shared.core.domain.event.DomainEvent

fun interface EventHandler<E : DomainEvent> {
    suspend fun handle(event: E)
}
