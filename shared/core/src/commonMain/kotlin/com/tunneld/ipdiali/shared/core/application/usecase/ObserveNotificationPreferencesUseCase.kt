package com.tunneld.ipdiali.shared.core.application.usecase

import com.tunneld.ipdiali.shared.core.application.infrastructure.preferences.UserPreferencesDataSource
import com.tunneld.ipdiali.shared.core.domain.NotificationPreferences
import kotlinx.coroutines.flow.Flow

fun interface ObserveNotificationPreferencesUseCase {
    fun observe(): Flow<NotificationPreferences>
}

internal class ObserveNotificationPreferencesUseCaseImpl(
    private val userPreferencesDataSource: UserPreferencesDataSource<NotificationPreferences>
) : ObserveNotificationPreferencesUseCase {
    override fun observe(): Flow<NotificationPreferences> =
        userPreferencesDataSource.observePreferences()
}
