package com.tunneld.ipdiali.shared.core.application.usecase

import com.tunneld.ipdiali.shared.core.application.infrastructure.preferences.UserPreferencesDataSource
import com.tunneld.ipdiali.shared.core.domain.NotificationPreferences

// Can't use fun interface here because of default parameters
interface PartialUpdateNotificationPreferencesUseCase {

    /** Update only the provided fields in the notification preferences. */
    suspend fun update(
        enabled: Boolean? = null,
        wifi: Boolean? = null,
        cellular: Boolean? = null,
        vpn: Boolean? = null,
        unknown: Boolean? = null,
        ip4: Boolean? = null,
        ip6: Boolean? = null,
    )
}

internal class PartialUpdateNotificationPreferencesUseCaseImpl(
    private val userPreferencesDataSource: UserPreferencesDataSource<NotificationPreferences>
) : PartialUpdateNotificationPreferencesUseCase {
    override suspend fun update(
        enabled: Boolean?,
        wifi: Boolean?,
        cellular: Boolean?,
        vpn: Boolean?,
        unknown: Boolean?,
        ip4: Boolean?,
        ip6: Boolean?,
    ) {
        userPreferencesDataSource.updatePreferences {
            copy(
                enabled = enabled ?: this.enabled,
                wifi = wifi ?: this.wifi,
                cellular = cellular ?: this.cellular,
                vpn = vpn ?: this.vpn,
                unknown = unknown ?: this.unknown,
                ip4 = ip4 ?: this.ip4,
                ip6 = ip6 ?: this.ip6,
            )
        }
    }
}
