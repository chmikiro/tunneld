package com.tunneld.ipdiali.feature.dashboard

import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView

@Composable
fun DashboardWebView(
    csvContent: String,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val webView = remember { WebView(context) }

    DisposableEffect(Unit) {
        onDispose { webView.destroy() }
    }

    AndroidView(
        factory = {
            webView.apply {
                settings.javaScriptEnabled = true
                settings.domStorageEnabled = true
                webViewClient = object : WebViewClient() {
                    override fun onPageFinished(view: WebView?, url: String?) {
                        // Inject CSV after page loads
                        val escaped = csvContent
                            .replace("\\", "\\\\")
                            .replace("'", "\\'")
                            .replace("\n", "\\n")
                            .replace("\r", "")
                        view?.evaluateJavascript(
                            "window.loadTunneldCSV('$escaped')",
                            null,
                        )
                    }
                }
                loadUrl("file:///android_asset/dashboard.html")
            }
        },
        modifier = modifier,
    )
}
