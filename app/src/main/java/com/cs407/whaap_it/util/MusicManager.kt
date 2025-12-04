package com.cs407.whaap_it.util

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.MediaPlayer
import android.util.Log
import com.cs407.whaap_it.R

object MusicManager {

    private var mediaPlayer: MediaPlayer? = null

    //Menu Music Functions
    fun startMenuMusic(context: Context) {
        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer.create(context, R.raw.stolen_menu_theme)
            mediaPlayer?.apply {
                isLooping = true
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(AudioAttributes.USAGE_GAME)
                        .build()
                )
            }
            requestAudioFocus(context)
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
            mediaPlayer?.apply {
                isLooping = true
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(AudioAttributes.USAGE_GAME)
                        .build()
                )
            }
            requestAudioFocus(context)
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

    private fun requestAudioFocus(context: Context) {
        try {
            val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager

            @Suppress("DEPRECATION")
            val result = audioManager.requestAudioFocus(
                { focusChange ->
                    // Ignore audio focus changes - keep music playing during microphone use
                    Log.d("MusicManager", "Audio focus changed: $focusChange - ignoring")
                },
                AudioManager.STREAM_MUSIC,
                AudioManager.AUDIOFOCUS_GAIN
            )

            if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
                Log.d("MusicManager", "Audio focus granted - music will keep playing")
            }
        } catch (e: Exception) {
            Log.e("MusicManager", "Error requesting audio focus", e)
        }
    }
}