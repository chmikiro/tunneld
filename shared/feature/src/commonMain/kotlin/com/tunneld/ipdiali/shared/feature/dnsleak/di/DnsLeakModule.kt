package com.tunneld.ipdiali.shared.feature.dnsleak.di

import com.tunneld.ipdiali.shared.feature.dnsleak.presentation.DnsLeakViewModel
import org.koin.dsl.module

val sharedFeatureDnsLeakModule = module {
    factory { DnsLeakViewModel(get()) }
}
