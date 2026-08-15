package com.tunneld.ipdiali.shared.core.application.infrastructure.background

import android.content.Context
import android.content.Intent
import android.os.Build
import com.tunneld.ipdiali.shared.core.application.infrastructure.log.Logger
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

internal class AndroidRealTimeNetworkMonitor(
    private val context: Context,
    private val logger: Logger,
) : RealTimeNetworkMonitor {

    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    private val _isRunning = MutableStateFlow(prefs.getBoolean(KEY_ENABLED, false))
    override val isRunning: Flow<Boolean> = _isRunning.asStateFlow()

    init {
        if (_isRunning.value) {
            startService()
            logger.d(TAG) { "Auto-restarted real-time monitor from persisted state" }
        }
    }

    override suspend fun start() {
        startService()
        prefs.edit().putBoolean(KEY_ENABLED, true).apply()
        _isRunning.value = true
        logger.d(TAG) { "Real-time network monitor started" }
    }

    override suspend fun stop() {
        context.stopService(Intent(context, NetworkMonitorService::class.java))
        prefs.edit().putBoolean(KEY_ENABLED, false).apply()
        _isRunning.value = false
        logger.d(TAG) { "Real-time network monitor stopped" }
    }

    private fun startService() {
        val intent = Intent(context, NetworkMonitorService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent)
        } else {
            context.startService(intent)
        }
    }

    companion object {
        private const val TAG = "RealTimeNetworkMonitor"
        private const val PREFS_NAME = "tunneld_real_time_monitor"
        private const val KEY_ENABLED = "enabled"
    }
}