package com.tunneld.ipdiali.di

import com.tunneld.ipdiali.shared.core.application.di.sharedCoreApplicationModule
import com.tunneld.ipdiali.shared.core.infrastructure.di.sharedCoreInfrastructureModule
import com.tunneld.ipdiali.shared.feature.dnsleak.di.sharedFeatureDnsLeakModule
import com.tunneld.ipdiali.shared.feature.home.di.sharedFeatureHomeModule
import com.tunneld.ipdiali.shared.feature.notifications.di.sharedFeatureNotificationsModule
import com.tunneld.ipdiali.shared.feature.settings.di.sharedFeatureSettingsModule
import kotlinx.coroutines.CoroutineScope
import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration

/**
 * Initializes Koin with the provided configuration and modules.
 *
 * @param applicationCoroutineScope CoroutineScope with whole application lifecycle.
 * @param config Optional KoinAppDeclaration to configure Koin.
 */
internal fun initKoin(
    applicationCoroutineScope: CoroutineScope,
    config: KoinAppDeclaration? = null,
) = startKoin {
    config?.invoke(this)

    modules(
        appModule,
        sharedCoreApplicationModule(applicationCoroutineScope),
        sharedCoreInfrastructureModule,
        sharedFeatureDnsLeakModule,
        sharedFeatureHomeModule,
        sharedFeatureNotificationsModule,
        sharedFeatureSettingsModule,
    )
}
