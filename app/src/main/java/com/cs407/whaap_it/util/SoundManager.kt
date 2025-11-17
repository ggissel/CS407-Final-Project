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

    private var bopItSound: Int = 0

    private var pullItSound: Int = 0

    private var twistItSound: Int = 0

    private var weakGameOverSound: Int = 0

    private var tooSlowSound: Int = 0

    private var zipperSquealSound: Int = 0

    private var swipeSound: Int = 0

    private var cartoonJumpSound: Int = 0

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

        zipperSquealSound = soundPool.load(context, R.raw.zipper_squeal, 1)

        swipeSound = soundPool.load(context, R.raw.swipe, 1)

        cartoonJumpSound = soundPool.load(context, R.raw.cartoon_jump, 1)
    }

    fun playButtonClick() {
        soundPool.play(buttonClickSound, 1f, 1f, 1, 0, 1f)
    }

    fun playToggleOnSound() {
        soundPool.play(toggleOnSound, 1f, 1f, 1, 0, 1f)
    }

    fun playToggleOffSound() {
        soundPool.play(toggleOffSound, 1f, 1f, 1, 0, 1f)
    }

    fun playBopIt() {
        soundPool.play(bopItSound, 1f, 1f, 1, 0, 1f)
    }

    fun playPullIt() {
        soundPool.play(pullItSound, 1f, 1f, 1, 0, 1f)
    }

    fun playTwistIt() {
        soundPool.play(twistItSound, 1f, 1f, 1, 0, 1f)
    }

    fun playWeakGameOver() {
        soundPool.play(weakGameOverSound, 1f, 1f, 1, 0, 1f)
    }

    fun tooSlowGameOver() {
        soundPool.play(tooSlowSound, 1f, 1f, 1, 0, 1f)
    }

    fun zipperSquealSound() {
        soundPool.play(zipperSquealSound, 1f, 1f, 1, 0, 1f)
    }

    fun swipeSound() {
        soundPool.play(swipeSound, 1f, 1f, 1, 0, 1f)
    }

    fun cartoonJumpSound() {
        soundPool.play(cartoonJumpSound, 1f, 1f, 1, 0, 1f)
    }

    fun release() {
        soundPool.release()
    }
}