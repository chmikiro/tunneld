package com.tunneld.ipdiali.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.tunneld.ipdiali.application.infrastructure.OpensourceAppConfig
import com.tunneld.ipdiali.feature.background.presentation.BackgroundWorkViewModel
import com.tunneld.ipdiali.infrastructure.FindMyIpConfig
import com.tunneld.ipdiali.infrastructure.room.FindMyIpDatabase
import com.tunneld.ipdiali.shared.core.application.infrastructure.config.AppConfig
import com.tunneld.ipdiali.shared.core.application.infrastructure.local.AddressHistoryLocalDataSource
import com.tunneld.ipdiali.shared.core.application.infrastructure.transaction.TransactionProvider
import com.tunneld.ipdiali.shared.core.infrastructure.room.RoomAddressHistoryDataSource
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.core.scope.Scope
import org.koin.dsl.bind
import org.koin.dsl.binds
import org.koin.dsl.module

internal val appModule = module {
    platformModule()

    factoryOf(::FindMyIpConfig).binds(arrayOf(AppConfig::class, OpensourceAppConfig::class))
    roomModule()
    dataStoreModule()

    viewModelOf(::BackgroundWorkViewModel)
}

internal expect fun Module.platformModule()

internal const val DATABASE_NAME = "database"

internal expect fun Scope.database(): FindMyIpDatabase

private val Scope.database: FindMyIpDatabase
    get() = get<FindMyIpDatabase>()

private fun Module.roomModule() {
    single<FindMyIpDatabase> { database() }.binds(arrayOf(TransactionProvider::class))

    factory { database.addressHistoryDao }

    factoryOf(::RoomAddressHistoryDataSource).bind<AddressHistoryLocalDataSource>()
}

internal const val DATASTORE_FILE_NAME = "user_preferences.preferences_pb"

internal expect fun Scope.createDataStore(): DataStore<Preferences>

private fun Module.dataStoreModule() {
    single { createDataStore() }
}
