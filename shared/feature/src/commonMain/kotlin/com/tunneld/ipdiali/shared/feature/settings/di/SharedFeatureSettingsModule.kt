package com.tunneld.ipdiali.shared.feature.settings.di

import com.tunneld.ipdiali.shared.core.infrastructure.ipapi.GeoIpProvider
import com.tunneld.ipdiali.shared.feature.settings.ui.GeoIpSettingsViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val sharedFeatureSettingsModule = module {
    viewModel {
        GeoIpSettingsViewModel(
            preferences = get(),
            providers = get(),
        )
    }
}
