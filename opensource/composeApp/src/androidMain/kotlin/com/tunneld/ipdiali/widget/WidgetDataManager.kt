package com.tunneld.ipdiali.widget

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object WidgetDataManager {
    private const val PREFS_NAME = "tunneld_widget_prefs"
    private const val KEY_IP = "current_ip"
    private const val KEY_COUNTRY = "current_country"
    private const val KEY_COUNTRY_CODE = "current_country_code"
    private const val KEY_CITY = "current_city"
    private const val KEY_ISP = "current_isp"
    private const val KEY_ORG = "current_org"
    private const val KEY_TIMESTAMP = "last_updated"

    fun prefs(context: Context): SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun update(
        context: Context,
        ip: String,
        country: String?,
        countryCode: String?,
        city: String?,
        isp: String?,
        org: String?,
    ) {
        prefs(context).edit().apply {
            putString(KEY_IP, ip)
            putString(KEY_COUNTRY, country)
            putString(KEY_COUNTRY_CODE, countryCode)
            putString(KEY_CITY, city)
            putString(KEY_ISP, isp)
            putString(KEY_ORG, org)
            putLong(KEY_TIMESTAMP, System.currentTimeMillis())
        }.apply()
    }

    data class WidgetData(
        val ip: String,
        val country: String?,
        val countryCode: String?,
        val city: String?,
        val isp: String?,
        val org: String?,
        val lastUpdated: Long,
    ) {
        val isEmpty: Boolean get() = ip.isBlank()
    }

    fun read(context: Context): WidgetData {
        val p = prefs(context)
        return WidgetData(
            ip = p.getString(KEY_IP, "") ?: "",
            country = p.getString(KEY_COUNTRY, null),
            countryCode = p.getString(KEY_COUNTRY_CODE, null),
            city = p.getString(KEY_CITY, null),
            isp = p.getString(KEY_ISP, null),
            org = p.getString(KEY_ORG, null),
            lastUpdated = p.getLong(KEY_TIMESTAMP, 0),
        )
    }
}