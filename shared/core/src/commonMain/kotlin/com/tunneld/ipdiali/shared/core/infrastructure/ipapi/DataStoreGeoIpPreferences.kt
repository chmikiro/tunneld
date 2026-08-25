package com.tunneld.ipdiali.shared.core.infrastructure.ipapi

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

internal class DataStoreGeoIpPreferences(
    private val dataStore: DataStore<Preferences>,
) : GeoIpPreferences, VtApiKeyPreferences {
    override suspend fun getSelectedProvider(): String {
        val prefs = dataStore.data.first()
        return prefs[Keys.selectedProvider] ?: GeoIpPreferences.DEFAULT_PROVIDER
    }

    override fun observeSelectedProvider(): Flow<String> =
        dataStore.data.map { prefs ->
            prefs[Keys.selectedProvider] ?: GeoIpPreferences.DEFAULT_PROVIDER
        }

    override suspend fun setSelectedProvider(providerId: String) {
        dataStore.updateData { prefs ->
            prefs.toMutablePreferences().apply {
                this[Keys.selectedProvider] = providerId
            }
        }
    }

    override suspend fun getApiKey(providerId: String): String? {
        val prefs = dataStore.data.first()
        return prefs[Keys.apiKeyForProvider(providerId)]
    }

    override suspend fun setApiKey(providerId: String, apiKey: String?) {
        dataStore.updateData { prefs ->
            prefs.toMutablePreferences().apply {
                if (apiKey != null) {
                    this[Keys.apiKeyForProvider(providerId)] = apiKey
                } else {
                    remove(Keys.apiKeyForProvider(providerId))
                }
            }
        }
    }

    override suspend fun getVtApiKey(): String? {
        val prefs = dataStore.data.first()
        return prefs[Keys.vtApiKey]
    }

    override fun observeVtApiKey(): Flow<String?> =
        dataStore.data.map { prefs -> prefs[Keys.vtApiKey] }

    override suspend fun setVtApiKey(key: String?) {
        dataStore.updateData { prefs ->
            prefs.toMutablePreferences().apply {
                if (key != null) this[Keys.vtApiKey] = key
                else remove(Keys.vtApiKey)
            }
        }
    }

    private object Keys {
        val selectedProvider = stringPreferencesKey("geoip_selected_provider")
        val vtApiKey = stringPreferencesKey("vt_api_key")
        fun apiKeyForProvider(providerId: String) =
            stringPreferencesKey("geoip_api_key_$providerId")
    }
}
