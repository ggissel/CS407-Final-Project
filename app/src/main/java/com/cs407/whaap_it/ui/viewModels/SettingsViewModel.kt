package com.cs407.whaap_it.ui.viewModels

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
    var settingsState by mutableStateOf(SettingsState())
        private set

    fun toggleFlip(enabled: Boolean) {
        settingsState = settingsState.copy(enableFlip = enabled)
    }

    fun toggleMic(enabled: Boolean) {
        settingsState = settingsState.copy(useMicrophone = enabled)
    }

    fun toggleHaptic(enabled: Boolean) {
        settingsState = settingsState.copy(hapticFeedback = enabled)
    }

    fun togglePinch(enabled: Boolean) {
        settingsState = settingsState.copy(pinchGesture = enabled)
    }

    fun toggleSwipe(enabled: Boolean) {
        settingsState = settingsState.copy(swipeGesture = enabled)
    }

    fun toggleDoubleTap(enabled: Boolean) {
        settingsState = settingsState.copy(doubleTap = enabled)
    }

    fun toggleLongPress(enabled: Boolean) {
        settingsState = settingsState.copy(longPress = enabled)
    }

    fun toggleShake(enabled: Boolean) {
        settingsState = settingsState.copy(enableShake = enabled)
    }

    fun toggleMusic(enabled: Boolean) {
        settingsState = settingsState.copy(enableMusic = enabled)
    }

    fun toggleGroovy(enabled: Boolean) {
        settingsState = settingsState.copy(groovyMode = enabled)
    }

    fun setMusicVolume(volume: Float) {
        settingsState = settingsState.copy(musicVolume = volume)
    }

    fun setAppVolume(volume: Float) {
        settingsState = settingsState.copy(appVolume = volume)
    }
}