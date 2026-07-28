package com.tunneld.ipdiali.shared.core.application.event

import com.tunneld.ipdiali.shared.core.domain.event.DomainEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

/** Subscribes to events of type [E] and returns a [Flow] of those events. */
inline fun <reified E : DomainEvent> EventBus.subscribe(): Flow<E> = events.filterIsInstance<E>()

inline fun <reified E : DomainEvent> EventBus.subscribe(
    coroutineScope: CoroutineScope,
    eventHandler: EventHandler<E>,
): Job = subscribe<E>().onEach(eventHandler::handle).launchIn(coroutineScope)
