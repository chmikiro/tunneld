package com.tunneld.ipdiali.shared.feature.dnsleak.presentation

import android.app.Activity
import android.app.Application
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import java.net.HttpURLConnection
import java.net.InetAddress
import java.net.URL
import org.json.JSONObject

class AndroidDnsLeakPlatform(
    appContext: Context,
) : DnsLeakPlatform {

    private val app = appContext.applicationContext as Application
    @Volatile private var currentActivity: Activity? = null

    init {
        app.registerActivityLifecycleCallbacks(object : Application.ActivityLifecycleCallbacks {
            override fun onActivityResumed(activity: Activity) { currentActivity = activity }
            override fun onActivityPaused(activity: Activity) {
                if (currentActivity == activity) currentActivity = null
            }
            override fun onActivityCreated(activity: Activity, bundle: Bundle?) {}
            override fun onActivityStarted(activity: Activity) {}
            override fun onActivityStopped(activity: Activity) {}
            override fun onActivityDestroyed(activity: Activity) {}
            override fun onActivitySaveInstanceState(activity: Activity, bundle: Bundle) {}
        })
    }

    override suspend fun runTest(domains: List<String>): List<DnsLeakResult> {
        // Step 1: trigger DNS resolution
        for (domain in domains) {
            try {
                InetAddress.getByName(domain)
            } catch (_: Exception) {
                // DNS resolution may fail but still registers on the server
            }
        }

        // Step 2: wait briefly for server to process queries
        kotlinx.coroutines.delay(2000)

        // Step 3: poll the API
        val url = URL("https://leak.ipdia.li/dns/leaktest")
        for (attempt in 1..15) {
            try {
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "POST"
                connection.setRequestProperty("Content-Type", "application/json")
                connection.doOutput = true
                connection.connectTimeout = 5000
                connection.readTimeout = 5000

                val body = buildString {
                    append("{\"domain\":[")
                    domains.forEachIndexed { i, d ->
                        if (i > 0) append(",")
                        append("\"$d\"")
                    }
                    append("]}")
                }
                connection.outputStream.use { it.write(body.toByteArray()) }

                val response = connection.inputStream.bufferedReader().readText()
                connection.disconnect()

                if (response != "null" && response != "{}") {
                    return parseResponse(response)
                }
            } catch (_: Exception) {
                // retry
            }
            kotlinx.coroutines.delay(4000)
        }

        return emptyList()
    }

    private fun parseResponse(raw: String): List<DnsLeakResult> {
        val obj = JSONObject(raw)
        val results = mutableListOf<DnsLeakResult>()
        for (asn in obj.keys()) {
            val entry = obj.getJSONObject(asn)
            results.add(
                DnsLeakResult(
                    isp = entry.optString("ISP", "Unknown ISP"),
                    ip = entry.optString("IP", "—"),
                    country = entry.optString("Country", "XX"),
                    city = entry.optString("City", ""),
                    asn = asn,
                )
            )
        }
        return results
    }

    override fun copyToClipboard(text: String) {
        val ctx = currentActivity ?: app
        val clipboard = ctx.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("IP Address", text)
        clipboard.setPrimaryClip(clip)
    }

    override fun openExternalBrowser(url: String, title: String) {
        val activity = currentActivity ?: return
        AlertDialog.Builder(activity)
            .setTitle(title)
            .setMessage("Open ASN details in external browser?\n\n$url")
            .setPositiveButton(android.R.string.ok) { _, _ ->
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                activity.startActivity(intent)
            }
            .setNegativeButton(android.R.string.cancel, null)
            .show()
    }
}
