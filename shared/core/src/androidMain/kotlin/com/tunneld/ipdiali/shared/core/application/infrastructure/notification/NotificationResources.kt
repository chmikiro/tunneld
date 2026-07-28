package com.tunneld.ipdiali.shared.core.application.infrastructure.notification

// Provides resources needed for displaying notifications. It exists because compose multiplatform
// drawable is a nightmare to handle on Android.
interface NotificationResources {
    /** Returns the resource ID of the icon to be used in notifications. */
    fun getNotificationIcon(topic: Notification.Topic): Int
}
