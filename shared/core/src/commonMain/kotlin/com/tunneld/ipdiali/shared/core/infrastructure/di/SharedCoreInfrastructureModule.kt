package com.tunneld.ipdiali.shared.core.infrastructure.di

import com.tunneld.ipdiali.shared.BuildConfig
import com.tunneld.ipdiali.shared.core.application.infrastructure.date.DateProvider
import com.tunneld.ipdiali.shared.core.application.infrastructure.local.CurrentAddressLocalDataSource
import com.tunneld.ipdiali.shared.core.application.infrastructure.preferences.UserPreferencesDataSource
import com.tunneld.ipdiali.shared.core.application.infrastructure.remote.IpAddressRemoteDataSource
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import com.tunneld.ipdiali.shared.core.domain.ThemeMode
import com.tunneld.ipdiali.shared.core.domain.InternetProtocolVersion
import com.tunneld.ipdiali.shared.core.domain.Ip4Address
import com.tunneld.ipdiali.shared.core.domain.Ip6Address
import com.tunneld.ipdiali.shared.core.domain.NotificationPreferences
import com.tunneld.ipdiali.shared.core.infrastructure.datastore.DataStoreNotificationPreferencesDataSource
import com.tunneld.ipdiali.shared.core.infrastructure.date.DateProviderImpl
import com.tunneld.ipdiali.shared.core.infrastructure.fake.FakeAddressDataSource
import com.tunneld.ipdiali.shared.core.infrastructure.inmemory.InMemoryIpAddressDataSource
import com.tunneld.ipdiali.shared.core.infrastructure.ipapi.DataStoreGeoIpPreferences
import com.tunneld.ipdiali.shared.core.infrastructure.ipapi.GeoIpDataSource
import com.tunneld.ipdiali.shared.core.infrastructure.ipapi.GeoIpPreferences
import com.tunneld.ipdiali.shared.core.infrastructure.ipapi.GeoIpProvider
import com.tunneld.ipdiali.shared.core.infrastructure.ipapi.Ip2LocationProvider
import com.tunneld.ipdiali.shared.core.infrastructure.ipapi.IpApiCoProvider
import com.tunneld.ipdiali.shared.core.infrastructure.ipapi.IpInfoProvider
import com.tunneld.ipdiali.shared.core.infrastructure.ipify.IpifyAddressDataSource
import com.tunneld.ipdiali.shared.core.infrastructure.ipify.IpifyConfigImpl
import com.tunneld.ipdiali.shared.core.infrastructure.mapper.StringToAddressMapper
import com.tunneld.ipdiali.shared.core.infrastructure.mapper.StringToAddressMapperImpl
import io.ktor.client.HttpClient
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlin.random.Random
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.named
import org.koin.core.module.dsl.singleOf
import org.koin.core.qualifier.named
import org.koin.dsl.bind
import org.koin.dsl.module
import org.koin.dsl.onClose

val sharedCoreInfrastructureModule = module {
    platformModule()

    singleOf(::DateProviderImpl).bind<DateProvider>()
    factory { StringToAddressMapperImpl }.bind<StringToAddressMapper>()
    single(named(InternetProtocolVersion.IPV4)) { InMemoryIpAddressDataSource<Ip4Address>() }
        .bind<CurrentAddressLocalDataSource<Ip4Address>>()
    single(named(InternetProtocolVersion.IPV6)) { InMemoryIpAddressDataSource<Ip6Address>() }
        .bind<CurrentAddressLocalDataSource<Ip6Address>>()

    factoryOf(::DataStoreNotificationPreferencesDataSource) {
            named(NotificationPreferences::class.qualifiedName.toString())
        }
        .bind<UserPreferencesDataSource<NotificationPreferences>>()

    if (BuildConfig.USE_FAKE) {
        fakeModule()
    } else {
        ipifyModule()
    }
}

internal expect fun Module.platformModule()

private fun Module.ipifyModule() {
    single(named("ipifyClient")) { HttpClient {} }.onClose { it?.close() }
    single {
        IpifyAddressDataSource(
            config = IpifyConfigImpl,
            httpClient = get(named("ipifyClient")),
            stringToAddressMapper = get(),
        )
    }
    factory(named(InternetProtocolVersion.IPV4)) { get<IpifyAddressDataSource>().ip4Wrapper() }
        .bind<IpAddressRemoteDataSource<Ip4Address>>()
    factory(named(InternetProtocolVersion.IPV6)) { get<IpifyAddressDataSource>().ip6Wrapper() }
        .bind<IpAddressRemoteDataSource<Ip6Address>>()

    // Geolocation preferences (DataStore-backed)
    singleOf(::DataStoreGeoIpPreferences).bind<GeoIpPreferences>()

    // Geolocation providers
    single { Ip2LocationProvider(httpClient = get(named("ipifyClient"))) }
    single { IpApiCoProvider(httpClient = get(named("ipifyClient"))) }
    single { IpInfoProvider(httpClient = get(named("ipifyClient"))) }

    // Provider list for settings UI
    single {
        listOf<GeoIpProvider>(
            get<Ip2LocationProvider>(),
            get<IpApiCoProvider>(),
            get<IpInfoProvider>(),
        )
    }

    // Unified geolocation data source
    single {
        GeoIpDataSource(
            providers = listOf(get<Ip2LocationProvider>(), get<IpApiCoProvider>(), get<IpInfoProvider>()),
            preferences = get(),
            logger = get(),
        )
    }
}

private fun Module.fakeModule() {
    single {
        FakeAddressDataSource(random = Random(0), stringToAddressMapper = StringToAddressMapperImpl)
    }
    factory(named(InternetProtocolVersion.IPV4)) { get<FakeAddressDataSource>().ip4Wrapper() }
        .bind<IpAddressRemoteDataSource<Ip4Address>>()
    factory(named(InternetProtocolVersion.IPV6)) { get<FakeAddressDataSource>().ip6Wrapper() }
        .bind<IpAddressRemoteDataSource<Ip6Address>>()
}
