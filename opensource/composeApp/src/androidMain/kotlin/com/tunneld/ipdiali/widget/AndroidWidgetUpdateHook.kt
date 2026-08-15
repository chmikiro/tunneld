package com.tunneld.ipdiali.widget

import android.content.Context
import android.content.Intent
import android.appwidget.AppWidgetManager
import com.tunneld.ipdiali.shared.core.application.infrastructure.widget.WidgetUpdateHook
import com.tunneld.ipdiali.shared.core.domain.IpInfo

class AndroidWidgetUpdateHook(
    private val context: Context,
) : WidgetUpdateHook {
    override suspend fun onIpUpdated(ip: String, ipInfo: IpInfo?) {
        WidgetDataManager.update(
            context,
            ip = ip,
            country = ipInfo?.country,
            countryCode = ipInfo?.countryCode,
            city = ipInfo?.city,
            isp = ipInfo?.isp,
            org = ipInfo?.org,
        )
        // Notify widget to refresh
        val intent = Intent(context, TunneldWidgetReceiver::class.java).apply {
            action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
        }
        context.sendBroadcast(intent)
    }
}