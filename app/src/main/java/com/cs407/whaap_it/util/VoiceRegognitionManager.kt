package com.cs407.whaap_it.util

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import android.media.AudioManager

object VoiceRecognitionManager {
    private var speechRecognizer: SpeechRecognizer? = null
    private var isListening = false
    private var onSoundDetectedCallback: (() -> Unit)? = null
    private var onErrorCallback: (() -> Unit)? = null
    private var hasDetectedSound = false


    private const val SOUND_THRESHOLD = 3.0f
    private var soundPeakCount = 0
    private const val REQUIRED_PEAKS = 3

    private fun createSpeechRecognizer(context: Context) {
        speechRecognizer?.destroy()
        try {
            val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        } catch (e: Exception) {
            Log.e("VoiceRecognition", "Could not access audio manager", e)
        }
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context.applicationContext).apply {
            setRecognitionListener(object : RecognitionListener {
                override fun onReadyForSpeech(params: Bundle?) {
                    Log.d("VoiceRecognition", "✓ Ready - listening for sound...")
                    isListening = true
                    hasDetectedSound = false
                    soundPeakCount = 0
                }

                override fun onBeginningOfSpeech() {
                    Log.d("VoiceRecognition", "✓ Sound detected!")
                }

                override fun onRmsChanged(rmsdB: Float) {
                    Log.d("VoiceRecognition", "Sound level: $rmsdB")


                    if (rmsdB > SOUND_THRESHOLD) {
                        soundPeakCount++
                        Log.d("VoiceRecognition", "LOUD sound detected! ($soundPeakCount/$REQUIRED_PEAKS)")


                        if (soundPeakCount >= REQUIRED_PEAKS && !hasDetectedSound) {
                            hasDetectedSound = true
                            Log.d("VoiceRecognition", "Sound detected!")

                            onSoundDetectedCallback?.invoke()

                            stopListening()
                        }
                    }
                }

                override fun onBufferReceived(buffer: ByteArray?) {}

                override fun onEndOfSpeech() {
                    Log.d("VoiceRecognition", "Sound ended")
                }

                override fun onError(error: Int) {
                    val errorMessage = when (error) {
                        SpeechRecognizer.ERROR_AUDIO -> "Audio recording error"
                        SpeechRecognizer.ERROR_CLIENT -> "Client side error"
                        SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "Insufficient permissions"
                        SpeechRecognizer.ERROR_NETWORK -> "Network error"
                        SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> "Network timeout"
                        SpeechRecognizer.ERROR_NO_MATCH -> "No match (this is OK for sound detection)"
                        SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> "Recognizer busy"
                        SpeechRecognizer.ERROR_SERVER -> "Server error"
                        SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "Timeout (no sound detected)"
                        else -> "Unknown error: $error"
                    }
                    Log.d("VoiceRecognition", "Event: $errorMessage")

                    isListening = false

                    if (!hasDetectedSound) {
                        if (error != SpeechRecognizer.ERROR_NO_MATCH &&
                            error != SpeechRecognizer.ERROR_SPEECH_TIMEOUT) {
                            onErrorCallback?.invoke()
                        }
                    }
                }

                override fun onResults(results: Bundle?) {
                    val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                    if (!matches.isNullOrEmpty()) {
                        Log.d("VoiceRecognition", "Me just needed sound")
                    }

                    if (!hasDetectedSound) {
                        hasDetectedSound = true
                        Log.d("VoiceRecognition", "Sound detected via speech results")
                        onSoundDetectedCallback?.invoke()
                    }

                    isListening = false
                }

                override fun onPartialResults(partialResults: Bundle?) {
                    if (!hasDetectedSound) {
                        val matches = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                        if (!matches.isNullOrEmpty()) {
                            hasDetectedSound = true
                            Log.d("VoiceRecognition", "Sound detected via partial results")
                            onSoundDetectedCallback?.invoke()
                            stopListening()
                        }
                    }
                }

                override fun onEvent(eventType: Int, params: Bundle?) {}
            })
        }
    }

    fun initialize(context: Context) {
        createSpeechRecognizer(context)
    }

    fun startListening(
        context: Context,
        onResult: (String) -> Unit,
        onError: () -> Unit = {}
    ) {
        if (isListening) {
            Log.d("VoiceRecognition", "Already listening, stopping first...")
            stopListening()
        }

        onSoundDetectedCallback = { onResult("SOUND") }
        onErrorCallback = onError
        hasDetectedSound = false
        soundPeakCount = 0

        createSpeechRecognizer(context)

        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, context.packageName)
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
            putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-US")


            putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS, 3000)
            putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_POSSIBLY_COMPLETE_SILENCE_LENGTH_MILLIS, 3000)
            putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_MINIMUM_LENGTH_MILLIS, 5000)

            putExtra(RecognizerIntent.EXTRA_PREFER_OFFLINE, false)


            putExtra(RecognizerIntent.EXTRA_AUDIO_SOURCE, android.media.MediaRecorder.AudioSource.MIC)
        }

        try {
            Log.d("VoiceRecognition", "Starting sound detection (threshold: $SOUND_THRESHOLD)")
            speechRecognizer?.startListening(intent)
            isListening = true
        } catch (e: Exception) {
            Log.e("VoiceRecognition", "Failed to start listening", e)
            isListening = false
            onErrorCallback?.invoke()
        }
    }

    fun stopListening() {
        Log.d("VoiceRecognition", "Stopping sound detection")

        if (isListening) {
            try {
                speechRecognizer?.stopListening()
            } catch (e: Exception) {
                Log.e("VoiceRecognition", "Error stopping", e)
            }
            isListening = false
        }
    }

    fun isCurrentlyListening(): Boolean = isListening

    fun destroy() {
        Log.d("VoiceRecognition", "Destroying recognizer")
        stopListening()
        speechRecognizer?.destroy()
        speechRecognizer = null
        isListening = false
        onSoundDetectedCallback = null
        onErrorCallback = null
    }
}