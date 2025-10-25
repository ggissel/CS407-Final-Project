package com.cs407.whaap_it.ui.viewModels

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

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

class SettingsViewModel : ViewModel() {
    private val _settingsState = mutableStateOf(SettingsState())
    val settingsState: MutableState<SettingsState> = _settingsState

    fun toggleFlip(enabled: Boolean) {
        _settingsState.value = _settingsState.value.copy(enableFlip = enabled)
    }

    fun toggleMic(enabled: Boolean) {
        _settingsState.value = _settingsState.value.copy(useMicrophone = enabled)
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
        _settingsState.value = _settingsState.value.copy(enableShake = enabled)
    }

    fun toggleMusic(enabled: Boolean) {
        _settingsState.value = _settingsState.value.copy(enableMusic = enabled)
    }

    fun toggleGroovy(enabled: Boolean) {
        _settingsState.value = _settingsState.value.copy(groovyMode = enabled)
    }

    fun setMusicVolume(volume: Float) {
        _settingsState.value = _settingsState.value.copy(musicVolume = volume)
    }

    fun setAppVolume(volume: Float) {
        _settingsState.value = _settingsState.value.copy(appVolume = volume)
    }
}