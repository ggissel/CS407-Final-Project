package com.cs407.whaap_it.ui.viewModels

import android.content.Context
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cs407.whaap_it.data.SettingsDataStore
import com.cs407.whaap_it.util.MusicManager
import com.cs407.whaap_it.util.SoundManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

/**
 * Holds the toggle state of each game settings option
 */
data class SettingsState(
    // Game Settings
    val enableShake: Boolean = true, // Accelerometer
    val enableFlip: Boolean = true, // Gyroscope
    val useMicrophone: Boolean = true, // Microphone
    val hapticFeedback: Boolean = true, // Vibrations
    val pinchGesture: Boolean = true, // Pinch Gesture
    val swipeGesture: Boolean = true, // Swipe Gesture
    val doubleTap: Boolean = true, // Double Tap Gesture
    val longPress: Boolean = true, // Long Press

    // Audio/Music Settings
    val enableMusic: Boolean = true,
    val musicVolume: Float = 0.5f,
    val appVolume: Float = 0.5f,
    val groovyMode: Boolean = false, // A very different music track

)

class SettingsViewModel(
    private val context: Context
) : ViewModel() {

    private val settingsDataStore = SettingsDataStore(context)
    private val _settingsState = MutableStateFlow(SettingsState())
    val settingsState: StateFlow<SettingsState> = _settingsState.asStateFlow()

    init {
        // Retrieve settings from DataStore, then update the settings state
        viewModelScope.launch {
            combine(
                settingsDataStore.enableMusic,
                settingsDataStore.musicVolume,
                settingsDataStore.appVolume,
                settingsDataStore.useMicrophone,
                settingsDataStore.enableShake,
            ) { enableMusic, musicVolume, appVolume, useMicrophone, enableShake ->

                MusicManager.setMusicEnabled(enableMusic)
                MusicManager.setMusicVolume(musicVolume)
                SoundManager.setAppVolume(appVolume)

                SettingsState(
                    enableMusic = enableMusic,
                    musicVolume = musicVolume,
                    appVolume = appVolume,
                    useMicrophone = useMicrophone,
                    enableShake = enableShake
                )
            }.collect { newState ->
                _settingsState.value = newState
            }
        }
    }
    fun toggleFlip(enabled: Boolean) {
        _settingsState.value = _settingsState.value.copy(enableFlip = enabled)
    }

    fun toggleMic(enabled: Boolean) {
        viewModelScope.launch {
            settingsDataStore.setUseMicrophone(enabled)
        }
    }

    fun toggleHaptic(enabled: Boolean) {
        _settingsState.value = _settingsState.value.copy(hapticFeedback = enabled)
    }

    fun togglePinch(enabled: Boolean) {
        _settingsState.value = _settingsState.value.copy(pinchGesture = enabled)
    }

    fun toggleSwipe(enabled: Boolean) {
        _settingsState.value = _settingsState.value.copy(swipeGesture = enabled)
    }

    fun toggleDoubleTap(enabled: Boolean) {
        _settingsState.value = _settingsState.value.copy(doubleTap = enabled)
    }

    fun toggleLongPress(enabled: Boolean) {
        _settingsState.value = _settingsState.value.copy(longPress = enabled)
    }

    fun toggleShake(enabled: Boolean) {
        viewModelScope.launch {
            settingsDataStore.setEnableShake(enabled)  // Use the correct function
        }
        _settingsState.value = _settingsState.value.copy(enableShake = enabled)
    }

    fun toggleMusic(enabled: Boolean) {
        viewModelScope.launch {
            settingsDataStore.setEnableMusic(enabled)
        }

        MusicManager.setMusicEnabled(enabled)

        if (enabled) {
            MusicManager.startMenuMusic(context)
        } else {
            MusicManager.pauseMenuMusic()
        }

        _settingsState.value = _settingsState.value.copy(enableMusic = enabled)
    }

    fun toggleGroovy(enabled: Boolean) {
        viewModelScope.launch {
            settingsDataStore.setGroovyMode(enabled)
        }
        _settingsState.value = _settingsState.value.copy(groovyMode = enabled)
    }

    fun setMusicVolume(volume: Float) {
        viewModelScope.launch {
            settingsDataStore.setMusicVolume(volume)
        }

        MusicManager.setMusicVolume(volume)
        _settingsState.value = _settingsState.value.copy(musicVolume = volume)
    }

    fun setAppVolume(volume: Float) {
        viewModelScope.launch {
            settingsDataStore.setAppVolume(volume)
        }

        SoundManager.setAppVolume(volume)
        _settingsState.value = _settingsState.value.copy(appVolume = volume)
    }
}