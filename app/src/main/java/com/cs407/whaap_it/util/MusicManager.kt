package com.cs407.whaap_it.util

import android.content.Context
import android.media.MediaPlayer
import com.cs407.whaap_it.R

object MusicManager {

    private var mediaPlayer: MediaPlayer? = null

    //Menu Music Functions
    fun startMenuMusic(context: Context) {
        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer.create(context, R.raw.stolen_menu_theme)
            mediaPlayer?.isLooping = true
        }

        if (mediaPlayer?.isPlaying == false) {
            mediaPlayer?.start()
        }
    }

    fun pauseMenuMusic() {
        mediaPlayer?.pause()
    }

    fun stopMenuMusic() {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
    }

    //Gameplay music Functions
    fun startGameplayMusic(context: Context) {
        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer.create(context, R.raw.stolen_gameplay_song)
            mediaPlayer?.isLooping = true
        }

        if (mediaPlayer?.isPlaying == false) {
            mediaPlayer?.start()
        }
    }

    fun resumeGameplayMusic() {
        mediaPlayer?.start()
    }
    fun pauseGameplayMusic() {
        mediaPlayer?.pause()
    }

    fun stopGameplayMusic() {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
    }
}