package com.tunneld.ipdiali.shared.core.application.di

import com.tunneld.ipdiali.shared.core.application.event.EventBus
import com.tunneld.ipdiali.shared.core.application.event.EventHandler
import com.tunneld.ipdiali.shared.core.application.event.subscribe
import com.tunneld.ipdiali.shared.core.domain.event.DomainEvent
import org.koin.core.module.Module
import org.koin.core.parameter.ParametersHolder
import org.koin.core.qualifier.Qualifier
import org.koin.core.qualifier.named
import org.koin.core.scope.Scope
import org.koin.dsl.onClose

inline fun <reified H : EventHandler<E>, reified E : DomainEvent> Module.eventHandler(
    qualifier: Qualifier = named(H::class.qualifiedName!!),
    noinline definition: Scope.(ParametersHolder) -> EventHandler<E>,
) {
    single(qualifier = qualifier, createdAtStart = true) {
            get<EventBus>()
                .subscribe<E>(
                    coroutineScope = applicationCoroutineScope(),
                    eventHandler = definition(it),
                )
        }
        .onClose { it?.cancel() }
}
