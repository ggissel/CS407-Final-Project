package com.cs407.whaap_it.util

import android.content.Context
import android.media.MediaPlayer
import com.cs407.whaap_it.R

/**
 * Helper singleton method to manage audio/sound functions such as button clicks
 */
object SoundManager {
    private var buttonClickSound: MediaPlayer? = null

    fun init(context: Context) {
        if (buttonClickSound == null) {
            buttonClickSound = MediaPlayer.create(context, R.raw.ui_button_click_1)
        }
    }

    fun playButtonClick() {
        buttonClickSound?.let {
            if (it.isPlaying) {
                it.seekTo(0)
            }
            it.start()
        }
    }

    fun release() {
        buttonClickSound?.release()
        buttonClickSound = null
    }
}