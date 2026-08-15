package com.tunneld.ipdiali.shared.core.application.infrastructure.widget

import com.tunneld.ipdiali.shared.core.domain.IpInfo

interface WidgetUpdateHook {
    suspend fun onIpUpdated(ip: String, ipInfo: IpInfo?)
}