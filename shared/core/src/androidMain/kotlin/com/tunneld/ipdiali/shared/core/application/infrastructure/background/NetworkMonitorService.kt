package com.tunneld.ipdiali.shared.core.application.infrastructure.background

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.tunneld.ipdiali.shared.core.application.infrastructure.log.Logger
import com.tunneld.ipdiali.shared.core.application.usecase.RefreshAddressUseCase
import com.tunneld.ipdiali.shared.core.domain.InternetProtocolVersion
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.qualifier.named

internal class NetworkMonitorService : Service(), KoinComponent {
    private val refreshIp4: RefreshAddressUseCase by inject(named(InternetProtocolVersion.IPV4))
    private val refreshIp6: RefreshAddressUseCase by inject(named(InternetProtocolVersion.IPV6))
    private val logger: Logger by inject()

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val networkCallback = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            refreshAddresses("network available")
        }

        override fun onCapabilitiesChanged(
            network: Network,
            capabilities: NetworkCapabilities,
        ) {
            refreshAddresses("capabilities changed")
        }

        override fun onLost(network: Network) {
            refreshAddresses("network lost")
        }
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForeground()
        (getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager)
            .registerDefaultNetworkCallback(networkCallback)
        return START_STICKY
    }

    override fun onDestroy() {
        (getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager)
            .unregisterNetworkCallback(networkCallback)
        serviceScope.cancel()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun startForeground() {
        val launchIntent = packageManager.getLaunchIntentForPackage(packageName)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, launchIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT,
        )

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Tunnel'd")
            .setContentText("Monitoring network changes")
            .setSmallIcon(android.R.drawable.ic_menu_compass)
            .setOngoing(true)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            startForeground(
                NOTIFICATION_ID, notification,
                ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE,
            )
        } else {
            startForeground(NOTIFICATION_ID, notification)
        }
    }

    private fun refreshAddresses(reason: String) {
        serviceScope.launch {
            try {
                refreshIp4.refresh()
                refreshIp6.refresh()
                logger.d(TAG) { "Addresses refreshed ($reason)" }
            } catch (e: Exception) {
                logger.e(TAG, e) { "Refresh failed" }
            }
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID, "Network Monitor", NotificationManager.IMPORTANCE_DEFAULT,
            ).apply {
                description = "Shows when real-time IP change detection is active"
                setShowBadge(false)
            }
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

    companion object {
        private const val TAG = "NetworkMonitorService"
        private const val CHANNEL_ID = "network_monitor_channel"
        private const val NOTIFICATION_ID = 2001
    }
}