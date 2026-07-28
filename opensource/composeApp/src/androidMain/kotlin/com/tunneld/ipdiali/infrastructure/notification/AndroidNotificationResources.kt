package com.tunneld.ipdiali.infrastructure.notification

import com.tunneld.ipdiali.R
import com.tunneld.ipdiali.shared.core.application.infrastructure.notification.Notification
import com.tunneld.ipdiali.shared.core.application.infrastructure.notification.NotificationResources

internal class AndroidNotificationResources : NotificationResources {
    override fun getNotificationIcon(topic: Notification.Topic): Int =
        when (topic) {
            Notification.Topic.AddressChanged -> R.drawable.ic_notification_launcher
        }
}
