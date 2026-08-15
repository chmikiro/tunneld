package com.tunneld.ipdiali.shared.core.infrastructure.di

import com.tunneld.ipdiali.shared.core.application.infrastructure.background.PeriodicWorkManager
import com.tunneld.ipdiali.shared.core.application.infrastructure.background.RealTimeNetworkMonitor
import com.tunneld.ipdiali.shared.core.application.infrastructure.background.AndroidRealTimeNetworkMonitor

import com.tunneld.ipdiali.shared.core.application.infrastructure.background.WorkManagerPeriodicWorkManager
import com.tunneld.ipdiali.shared.core.application.infrastructure.dns.AndroidSystemDnsService
import com.tunneld.ipdiali.shared.core.application.infrastructure.dns.DnsService
import com.tunneld.ipdiali.shared.core.application.infrastructure.log.FindMyIpLogger
import com.tunneld.ipdiali.shared.core.application.infrastructure.log.Logger
import com.tunneld.ipdiali.shared.core.application.infrastructure.network.AndroidNetworkTypeObserver
import com.tunneld.ipdiali.shared.core.application.infrastructure.network.NetworkTypeObserver
import com.tunneld.ipdiali.shared.core.application.infrastructure.notification.AndroidNotificationService
import com.tunneld.ipdiali.shared.core.application.infrastructure.notification.NotificationService
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.bind

internal actual fun Module.platformModule() {
    factoryOf(::AndroidSystemDnsService).bind<DnsService>()
    factoryOf(::AndroidNetworkTypeObserver).bind<NetworkTypeObserver>()
    factoryOf(::WorkManagerPeriodicWorkManager).bind<PeriodicWorkManager>()
    factoryOf(::AndroidRealTimeNetworkMonitor).bind<RealTimeNetworkMonitor>()


    single { FindMyIpLogger }.bind<Logger>()
    factoryOf(::AndroidNotificationService).bind<NotificationService>()
}
