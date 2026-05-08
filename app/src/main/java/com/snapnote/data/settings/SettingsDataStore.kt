package com.snapnote.data.settings

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private const val DATASTORE_NAME = "snapnote_settings"

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = DATASTORE_NAME)

object SettingsKeys {
    val AUTO_SCAN_ENABLED = booleanPreferencesKey("auto_scan_enabled")
    val ONBOARDING_COMPLETED = booleanPreferencesKey("onboarding_completed")
}

class SettingsDataStore(private val context: Context) {

    val autoScanEnabled: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[SettingsKeys.AUTO_SCAN_ENABLED] ?: true
        }

    val onboardingCompleted: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[SettingsKeys.ONBOARDING_COMPLETED] ?: false
        }

    suspend fun setAutoScanEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[SettingsKeys.AUTO_SCAN_ENABLED] = enabled
        }
    }

    suspend fun setOnboardingCompleted(completed: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[SettingsKeys.ONBOARDING_COMPLETED] = completed
        }
    }
}
