package com.tunneld.ipdiali.shared.core.infrastructure.ipapi

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

internal class DataStoreExternalLinkPreferences(
    private val dataStore: DataStore<Preferences>,
) : ExternalLinkPreferences {
    override fun observeSkipVtConfirmation(): Flow<Boolean> =
        dataStore.data.map { it[Keys.skipVtConfirmation] ?: false }

    override fun observeSkipProviderConfirmation(): Flow<Boolean> =
        dataStore.data.map { it[Keys.skipProviderConfirmation] ?: false }

    override suspend fun getSkipVtConfirmation(): Boolean {
        val prefs = dataStore.data.first()
        return prefs[Keys.skipVtConfirmation] ?: false
    }

    override suspend fun getSkipProviderConfirmation(): Boolean {
        val prefs = dataStore.data.first()
        return prefs[Keys.skipProviderConfirmation] ?: false
    }

    override suspend fun setSkipVtConfirmation(skip: Boolean) {
        dataStore.updateData { prefs ->
            prefs.toMutablePreferences().apply { this[Keys.skipVtConfirmation] = skip }
        }
    }

    override suspend fun setSkipProviderConfirmation(skip: Boolean) {
        dataStore.updateData { prefs ->
            prefs.toMutablePreferences().apply { this[Keys.skipProviderConfirmation] = skip }
        }
    }

    private object Keys {
        val skipVtConfirmation = booleanPreferencesKey("skip_vt_external_confirm")
        val skipProviderConfirmation = booleanPreferencesKey("skip_provider_external_confirm")
    }
}
