package com.tunneld.ipdiali.shared.core.infrastructure.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import com.tunneld.ipdiali.shared.core.application.infrastructure.preferences.UserPreferencesDataSource
import com.tunneld.ipdiali.shared.core.domain.NotificationPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

internal class DataStoreNotificationPreferencesDataSource(
    private val dataStore: DataStore<Preferences>
) : UserPreferencesDataSource<NotificationPreferences> {
    override fun observePreferences(): Flow<NotificationPreferences> =
        dataStore.data.map { prefs -> prefs.toNotificationPreferences() }

    override suspend fun updatePreferences(
        update: NotificationPreferences.() -> NotificationPreferences
    ) {
        dataStore.updateData {
            it.toMutablePreferences().apply {
                saveNotificationPreferences(toNotificationPreferences().update())
            }
        }
    }
}

private fun Preferences.toNotificationPreferences(): NotificationPreferences =
    NotificationPreferences(
        enabled = this[NotificationPreferencesKeys.enabled] ?: false,
        wifi = this[NotificationPreferencesKeys.wifi] ?: true,
        cellular = this[NotificationPreferencesKeys.cellular] ?: true,
        vpn = this[NotificationPreferencesKeys.vpn] ?: true,
        unknown = this[NotificationPreferencesKeys.unknown] ?: true,
        ip4 = this[NotificationPreferencesKeys.ip4] ?: true,
        ip6 = this[NotificationPreferencesKeys.ip6] ?: true,
    )

private fun MutablePreferences.saveNotificationPreferences(prefs: NotificationPreferences) {
    this[NotificationPreferencesKeys.enabled] = prefs.enabled
    this[NotificationPreferencesKeys.wifi] = prefs.wifi
    this[NotificationPreferencesKeys.cellular] = prefs.cellular
    this[NotificationPreferencesKeys.vpn] = prefs.vpn
    this[NotificationPreferencesKeys.unknown] = prefs.unknown
    this[NotificationPreferencesKeys.ip4] = prefs.ip4
    this[NotificationPreferencesKeys.ip6] = prefs.ip6
}

private object NotificationPreferencesKeys {
    val enabled = booleanPreferencesKey("notifications_enabled")
    val wifi = booleanPreferencesKey("notifications_wifi_enabled")
    val cellular = booleanPreferencesKey("notifications_cellular_enabled")
    val vpn = booleanPreferencesKey("notifications_vpn_enabled")
    val unknown = booleanPreferencesKey("notifications_unknown_enabled")
    val ip4 = booleanPreferencesKey("notifications_ipv4_enabled")
    val ip6 = booleanPreferencesKey("notifications_ipv6_enabled")
}
