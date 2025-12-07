package com.cs407.whaap_it.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.settingsDataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class SettingsDataStore(private val context: Context) {
    companion object {
        val ENABLE_MUSIC = booleanPreferencesKey("enable_music")
        val MUSIC_VOLUME = floatPreferencesKey("music_volume")
        val APP_VOLUME = floatPreferencesKey("app_volume")
        val GROOVY_MODE = booleanPreferencesKey("groovy_mode")

        val USE_MICROPHONE = booleanPreferencesKey("use_microphone")

        // Default app setting values
        private const val DEFAULT_ENABLE_MUSIC = true
        private const val DEFAULT_MUSIC_VOLUME = 0.5f
        private const val DEFAULT_APP_VOLUME = 0.5f
        private const val DEFAULT_GROOVY_MODE = false

        // Default game setting values
        private const val DEFAULT_USE_MICROPHONE = true

    }

    val enableMusic: Flow<Boolean> = context.settingsDataStore.data
        .map { preferences ->
            preferences[ENABLE_MUSIC] ?: DEFAULT_ENABLE_MUSIC
        }

    val musicVolume: Flow<Float> = context.settingsDataStore.data
        .map { preferences ->
            preferences[MUSIC_VOLUME] ?: DEFAULT_MUSIC_VOLUME
        }

    val appVolume: Flow<Float> = context.settingsDataStore.data
        .map { preferences ->
            preferences[APP_VOLUME] ?: DEFAULT_APP_VOLUME
        }

    val groovyMode: Flow<Boolean> = context.settingsDataStore.data
        .map { preferences ->
            preferences[GROOVY_MODE] ?: DEFAULT_GROOVY_MODE
        }

    val useMicrophone: Flow<Boolean> = context.settingsDataStore.data
        .map { preferences ->
            preferences[USE_MICROPHONE] ?: DEFAULT_USE_MICROPHONE
        }

    suspend fun setEnableMusic(enable: Boolean) {
        context.settingsDataStore.edit { preferences ->
            preferences[ENABLE_MUSIC] = enable
        }
    }

    suspend fun setMusicVolume(volume: Float) {
        context.settingsDataStore.edit { preferences ->
            preferences[MUSIC_VOLUME] = volume.coerceIn(0f, 1f)
        }
    }

    suspend fun setAppVolume(volume: Float) {
        context.settingsDataStore.edit { preferences ->
            preferences[APP_VOLUME] = volume.coerceIn(0f, 1f)
        }
    }

    suspend fun setGroovyMode(enabled: Boolean) {
        context.settingsDataStore.edit { preferences ->
            preferences[GROOVY_MODE] = enabled
        }
    }

    suspend fun setUseMicrophone(enabled: Boolean) {
        context.settingsDataStore.edit { preferences ->
            preferences[USE_MICROPHONE] = enabled
        }
    }
}
