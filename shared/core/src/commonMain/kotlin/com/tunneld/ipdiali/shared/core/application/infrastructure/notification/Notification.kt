package com.tunneld.ipdiali.shared.core.application.infrastructure.notification

sealed interface Notification {
    val id: Int

    enum class Topic {
        AddressChanged
    }

    data class Info(
        override val id: Int,
        val topic: Topic,
        val title: String,
        val message: String,
    ) : Notification
}
