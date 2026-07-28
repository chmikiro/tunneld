package com.tunneld.ipdiali.shared.feature.dnsleak.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class DnsLeakResult(
    val isp: String,
    val ip: String,
    val country: String,
    val city: String,
    val asn: String,
)

sealed interface DnsLeakState {
    data object Idle : DnsLeakState
    data object Running : DnsLeakState
    data class Done(val results: List<DnsLeakResult>) : DnsLeakState
    data class Error(val message: String) : DnsLeakState
}

class DnsLeakViewModel(
    private val platform: DnsLeakPlatform,
) : ViewModel() {

    private val _state = MutableStateFlow<DnsLeakState>(DnsLeakState.Idle)
    val state = _state.asStateFlow()

    fun startTest() {
        if (_state.value is DnsLeakState.Running) return
        _state.value = DnsLeakState.Running

        viewModelScope.launch {
            try {
                val domains = List(5) { generateDomain() }
                val results = withContext(Dispatchers.IO) {
                    platform.runTest(domains)
                }
                _state.value = DnsLeakState.Done(results)
            } catch (e: Exception) {
                _state.value = DnsLeakState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun reset() {
        _state.value = DnsLeakState.Idle
    }

    fun copyToClipboard(text: String) = platform.copyToClipboard(text)

    fun openExternalBrowser(url: String, title: String) = platform.openExternalBrowser(url, title)

    private fun generateDomain(): String {
        val chars = "abcdefghijklmnopqrstuvwxyz0123456789"
        val id = (1..16).map { chars.random() }.joinToString("")
        return "$id.leak.ipdia.li"
    }
}

interface DnsLeakPlatform {
    suspend fun runTest(domains: List<String>): List<DnsLeakResult>
    fun copyToClipboard(text: String)
    fun openExternalBrowser(url: String, title: String)
}
