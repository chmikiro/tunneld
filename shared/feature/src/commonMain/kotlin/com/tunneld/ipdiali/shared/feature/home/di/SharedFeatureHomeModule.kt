package com.tunneld.ipdiali.shared.feature.home.di

import com.tunneld.ipdiali.shared.core.domain.InternetProtocolVersion
import com.tunneld.ipdiali.shared.feature.home.persentation.HomeViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module

val sharedFeatureHomeModule = module {
    viewModel {
        HomeViewModel(
            get(),
            get(named(InternetProtocolVersion.IPV4)),
            get(named(InternetProtocolVersion.IPV6)),
            get(named(InternetProtocolVersion.IPV4)),
            get(named(InternetProtocolVersion.IPV6)),
            get(),
        )
    }
}
