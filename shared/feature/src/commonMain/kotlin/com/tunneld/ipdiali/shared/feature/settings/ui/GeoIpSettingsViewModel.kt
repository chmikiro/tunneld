package com.tunneld.ipdiali.shared.feature.settings.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tunneld.ipdiali.shared.core.infrastructure.ipapi.GeoIpPreferences
import com.tunneld.ipdiali.shared.core.infrastructure.ipapi.GeoIpProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

internal class GeoIpSettingsViewModel(
    private val preferences: GeoIpPreferences,
    providers: List<GeoIpProvider>,
) : ViewModel() {
    val providerList: List<GeoIpProvider> = providers

    val selectedProvider: StateFlow<String> = preferences
        .observeSelectedProvider()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), GeoIpPreferences.DEFAULT_PROVIDER)

    private val _apiKey = MutableStateFlow("")
    val apiKey: StateFlow<String> = _apiKey.asStateFlow()

    private val _apiKeyForProvider = MutableStateFlow<String?>(null)
    val apiKeyForProvider: StateFlow<String?> = _apiKeyForProvider.asStateFlow()

    val selectedProviderObj: GeoIpProvider?
        get() = providerList.find { it.id == selectedProvider.value }

    init {
        viewModelScope.launch {
            val providerId = selectedProvider.value
            _apiKeyForProvider.value = providerId
            _apiKey.value = preferences.getApiKey(providerId) ?: ""
        }
    }

    fun selectProvider(providerId: String) {
        viewModelScope.launch {
            preferences.setSelectedProvider(providerId)
            _apiKeyForProvider.value = providerId
            _apiKey.value = preferences.getApiKey(providerId) ?: ""
        }
    }

    fun updateApiKey(key: String) {
        _apiKey.value = key
    }

    fun saveApiKey() {
        viewModelScope.launch {
            val providerId = selectedProvider.value
            val key = _apiKey.value.trim().ifBlank { null }
            preferences.setApiKey(providerId, key)
        }
    }
}
