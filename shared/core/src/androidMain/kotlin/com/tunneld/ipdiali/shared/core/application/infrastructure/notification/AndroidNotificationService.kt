package com.tunneld.ipdiali.shared.core.application.infrastructure.notification

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.annotation.RequiresPermission
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.tunneld.ipdiali.shared.core.application.infrastructure.log.Logger
import tunneld.composeapp.generated.resources.Res
import tunneld.composeapp.generated.resources.notification_channel_address_changed
import org.jetbrains.compose.resources.getString

internal class AndroidNotificationService(
    private val context: Context,
    private val resources: NotificationResources,
    private val logger: Logger,
) : NotificationService {

    private val notificationManagerCompat = NotificationManagerCompat.from(context)

    override suspend fun notify(notification: Notification) {
        if (!context.hasNotificationPermission()) {
            logger.w(TAG) { "Missing POST_NOTIFICATIONS permission, cannot show notification." }
            return
        }

        try {
            @SuppressLint("MissingPermission")
            when (notification) {
                is Notification.Info -> notify(notification)
            }
        } catch (e: SecurityException) {
            logger.e(TAG, e) { "Failed to post notification due to missing permission." }
        }
    }

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    private suspend fun notify(notification: Notification.Info) {
        notificationManagerCompat.notify(notification.id, buildNotification(notification))
    }

    private suspend fun buildNotification(
        notification: Notification.Info
    ): android.app.Notification {
        return NotificationCompat.Builder(context, notification.topic.createChannelIfNeeded())
            .setContentTitle(notification.title)
            .setContentText(notification.message)
            .setSmallIcon(resources.getNotificationIcon(notification.topic))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()
    }

    private suspend fun Notification.Topic.createChannelIfNeeded(): String {
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(channelId, channelName(), importance)
        notificationManagerCompat.createNotificationChannel(channel)
        return channelId
    }

    private companion object {
        const val TAG = "AndroidNotificationService"
    }
}

private fun Context.hasNotificationPermission(): Boolean =
    if (Build.VERSION.SDK_INT < 33) {
        true
    } else {
        ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) ==
            PackageManager.PERMISSION_GRANTED
    }

private val Notification.Topic.channelId: String
    get() =
        when (this) {
            Notification.Topic.AddressChanged -> "address_changed_channel"
        }

private suspend fun Notification.Topic.channelName(): String =
    when (this) {
        Notification.Topic.AddressChanged ->
            getString(Res.string.notification_channel_address_changed)
    }
