package com.tunneld.ipdiali.shared.core.application.infrastructure.notification

interface NotificationService {
    suspend fun notify(notification: Notification)
}
