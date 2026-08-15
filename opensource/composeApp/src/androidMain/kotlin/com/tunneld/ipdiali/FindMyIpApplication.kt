package com.tunneld.ipdiali

import android.app.Application
import com.tunneld.ipdiali.di.initKoin
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.koin.android.ext.koin.androidContext

class FindMyIpApplication : Application() {

    private val coroutineScope by lazy {
        CoroutineScope(Dispatchers.Default + SupervisorJob() + CoroutineName("FindMyIpApplication"))
    }

    override fun onCreate() {
        super.onCreate()
        initKoin(coroutineScope) { androidContext(this@FindMyIpApplication) }
    }
}