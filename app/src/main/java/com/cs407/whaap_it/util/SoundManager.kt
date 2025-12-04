package com.cs407.whaap_it.util

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import android.util.Log
import com.cs407.whaap_it.R
import com.cs407.whaap_it.data.SettingsDataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

/**
 * Helper companion object used to manage the in-app sounds, such as Button Clicks
 */
object SoundManager {
    private lateinit var soundPool: SoundPool

    var appVolume: Float = 1f
        private set

    private var buttonClickSound: Int = 0

    private var toggleOnSound: Int = 0

    private var toggleOffSound: Int = 0

    private var bopItSound: Int = 0

    private var pullItSound: Int = 0

    private var twistItSound: Int = 0

    private var weakGameOverSound: Int = 0

    private var tooSlowSound: Int = 0

    private var zipperSquealSound: Int = 0

    private var swipeSound: Int = 0

    private var cartoonJumpSound: Int = 0

    private var failTrumpetSound: Int = 0

    fun init(context: Context) {
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_GAME)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()

        soundPool = SoundPool.Builder()
            .setMaxStreams(5)
            .setAudioAttributes(audioAttributes)
            .build()

        // Load short sounds
        buttonClickSound = soundPool.load(context, R.raw.ui_button_click_2, 1)

        toggleOnSound = soundPool.load(context, R.raw.toggle_button_on_sound_1, 1)

        toggleOffSound = soundPool.load(context, R.raw.toggle_button_off_sound_1, 1)

        bopItSound = soundPool.load(context, R.raw.bopit, 1)

        pullItSound = soundPool.load(context, R.raw.pullit, 1)

        twistItSound = soundPool.load(context, R.raw.twistit, 1)

        weakGameOverSound = soundPool.load(context, R.raw.weak_game_over, 1)

        tooSlowSound = soundPool.load(context, R.raw.too_slow_game_over, 1)

        zipperSquealSound = soundPool.load(context, R.raw.bop_sound, 1)

        swipeSound = soundPool.load(context, R.raw.swipe, 1)

        cartoonJumpSound = soundPool.load(context, R.raw.cartoon_jump, 1)

        failTrumpetSound = soundPool.load(context, R.raw.fail_trumpet, 1)

        // Load saved volume settings
        val settingsDataStore = SettingsDataStore(context)

        // Load saved volume settings asynchronously
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val savedAppVolume = settingsDataStore.appVolume
                    .first() // Get first value from Flow
                appVolume = savedAppVolume
            } catch (e: Exception) {
                // Use default if there's an error
                appVolume = 0.5f
                Log.e("SoundManager", "Error loading app volume: ${e.message}")
            }
        }
    }

    fun setAppVolume(volume: Float) {
        appVolume = volume.coerceIn(0f, 1f)
    }

    fun playButtonClick() {
        soundPool.play(buttonClickSound, appVolume, appVolume, 1, 0, 1f)
    }

    fun playToggleOnSound() {
        soundPool.play(toggleOnSound, appVolume, appVolume, 1, 0, 1f)
    }

    fun playToggleOffSound() {
        soundPool.play(toggleOffSound, appVolume, appVolume, 1, 0, 1f)
    }

    fun playBopIt() {
        soundPool.play(bopItSound, appVolume, appVolume, 1, 0, 1f)
    }

    fun playPullIt() {
        soundPool.play(pullItSound, appVolume, appVolume, 1, 0, 1f)
    }

    fun playTwistIt() {
        soundPool.play(twistItSound, appVolume, appVolume, 1, 0, 1f)
    }

    fun playWeakGameOver() {
        soundPool.play(weakGameOverSound, appVolume, appVolume, 1, 0, 1f)
        soundPool.play(failTrumpetSound, appVolume, appVolume, 1, 0, 1f)
    }

    fun tooSlowGameOver() {
        soundPool.play(tooSlowSound, appVolume, appVolume, 1, 0, 1f)
        soundPool.play(failTrumpetSound, appVolume, appVolume, 1, 0, 1f)
    }

    fun zipperSquealSound() {
        soundPool.play(zipperSquealSound, appVolume, appVolume, 1, 0, 1f)
    }

    fun swipeSound() {
        soundPool.play(swipeSound, appVolume, appVolume, 1, 0, 1f)
    }

    fun cartoonJumpSound() {
        soundPool.play(cartoonJumpSound, appVolume, appVolume, 1, 0, 1f)
    }

    fun failTrumpetSound() {
        soundPool.play(failTrumpetSound, appVolume, appVolume, 1, 0, 1f)
    }

    fun release() {
        soundPool.release()
    }
}