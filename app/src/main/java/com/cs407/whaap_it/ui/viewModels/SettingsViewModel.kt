package com.cs407.whaap_it.ui.viewModels

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

}