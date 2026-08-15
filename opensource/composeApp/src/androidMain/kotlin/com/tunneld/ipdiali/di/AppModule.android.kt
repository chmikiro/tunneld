package com.tunneld.ipdiali.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.room.Room
import com.tunneld.ipdiali.infrastructure.notification.AndroidNotificationResources
import com.tunneld.ipdiali.infrastructure.room.FindMyIpDatabase
import com.tunneld.ipdiali.infrastructure.room.FindMyIpDatabase.Companion.buildDatabase
import com.tunneld.ipdiali.shared.core.application.infrastructure.notification.NotificationResources
import com.tunneld.ipdiali.shared.feature.dnsleak.presentation.AndroidDnsLeakPlatform
import com.tunneld.ipdiali.shared.feature.dnsleak.presentation.DnsLeakPlatform
import com.tunneld.ipdiali.widget.AndroidWidgetUpdateHook
import com.tunneld.ipdiali.shared.core.application.infrastructure.widget.WidgetUpdateHook
import okio.Path.Companion.toPath
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.core.scope.Scope
import org.koin.dsl.bind

internal actual fun Scope.database(): FindMyIpDatabase =
    Room.databaseBuilder(
            context = get(),
            klass = FindMyIpDatabase::class.java,
            name = DATABASE_NAME,
        )
        .buildDatabase()

internal actual fun Module.platformModule() {
    factoryOf(::AndroidNotificationResources).bind<NotificationResources>()
    factory { AndroidDnsLeakPlatform(androidContext()) }.bind<DnsLeakPlatform>()
    factory { AndroidWidgetUpdateHook(androidContext()) }.bind<WidgetUpdateHook>()
}

internal actual fun Scope.createDataStore(): DataStore<Preferences> =
    PreferenceDataStoreFactory.createWithPath {
        androidContext().filesDir.resolve(DATASTORE_FILE_NAME).absolutePath.toPath()
    }
