package com.cs407.whaap_it.util

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import com.cs407.whaap_it.R

/**
 * Helper companion object used to manage the in-app sounds, such as Button Clicks
 */
object SoundManager {
    private lateinit var soundPool: SoundPool
    private var buttonClickSound: Int = 0

    private var toggleOnSound: Int = 0

    private var toggleOffSound: Int = 0

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
        buttonClickSound = soundPool.load(context, R.raw.ui_button_click_1, 1)

    }

    fun playButtonClick() {
        soundPool.play(buttonClickSound, 1f, 1f, 1, 0, 1f)
    }

    fun playToggleOnSound() {

    }

    fun release() {
        soundPool.release()
    }
}