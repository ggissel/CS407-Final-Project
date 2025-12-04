package com.cs407.whaap_it.util

import android.content.Context
import android.media.MediaPlayer
import com.cs407.whaap_it.R

object MusicManager {

    private var mediaPlayer: MediaPlayer? = null
    private var musicVolume: Float = 1f
    private var isMusicEnabled: Boolean = true

    fun setMusicVolume(volume: Float) {
        musicVolume = volume.coerceIn(0f, 1f)
        mediaPlayer?.setVolume(musicVolume, musicVolume)
    }

    fun setMusicEnabled(enabled: Boolean) {
        isMusicEnabled = enabled
    }

    //Menu Music Functions
    fun startMenuMusic(context: Context) {
        if (!isMusicEnabled) return

        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer.create(context, R.raw.stolen_menu_theme)
            mediaPlayer?.isLooping = true
            mediaPlayer?.setVolume(musicVolume, musicVolume)
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
        if (!isMusicEnabled) return

        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer.create(context, R.raw.stolen_gameplay_song)
            mediaPlayer?.isLooping = true
            mediaPlayer?.setVolume(musicVolume, musicVolume)
        }

        if (mediaPlayer?.isPlaying == false) {
            mediaPlayer?.start()
        }
    }

    fun resumeGameplayMusic() {
        if (!isMusicEnabled) return

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