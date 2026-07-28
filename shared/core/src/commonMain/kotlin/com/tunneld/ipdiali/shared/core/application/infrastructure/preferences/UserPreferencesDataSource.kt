package com.tunneld.ipdiali.shared.core.application.infrastructure.preferences

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

interface UserPreferencesDataSource<P> {

    /**
     * Returns a [Flow] that emits the current preferences and updates whenever the preferences
     * change.
     */
    fun observePreferences(): Flow<P>

    /**
     * Atomically updates the stored preferences by applying the given [update] function to the
     * current preferences and storing the result.
     */
    suspend fun updatePreferences(update: P.() -> P)
}

suspend fun <P> UserPreferencesDataSource<P>.get(): P = observePreferences().first()
