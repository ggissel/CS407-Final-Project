package com.cs407.whaap_it.util

import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.util.Log
import kotlinx.coroutines.*
import kotlin.math.abs
import kotlin.math.sqrt

object VoiceRecognitionManager {
    private var audioRecord: AudioRecord? = null
    private var isListening = false
    private var recordingJob: Job? = null
    private var onSoundDetectedCallback: (() -> Unit)? = null

    private const val SAMPLE_RATE = 44100
    private const val CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_MONO
    private const val AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT


    private const val AMPLITUDE_THRESHOLD = 2000

    private var hasTriggered = false

    fun initialize(context: android.content.Context) {
    }

    fun startListening(
        context: android.content.Context,
        onResult: (String) -> Unit,
        onError: () -> Unit = {}
    ) {
        if (isListening) {
            return
        }

        onSoundDetectedCallback = { onResult("SOUND") }
        hasTriggered = false

        try {
            val bufferSize = AudioRecord.getMinBufferSize(
                SAMPLE_RATE,
                CHANNEL_CONFIG,
                AUDIO_FORMAT
            )

            if (bufferSize == AudioRecord.ERROR || bufferSize == AudioRecord.ERROR_BAD_VALUE) {
                onError()
                return
            }

            audioRecord = AudioRecord(
                MediaRecorder.AudioSource.MIC,
                SAMPLE_RATE,
                CHANNEL_CONFIG,
                AUDIO_FORMAT,
                bufferSize
            )

            if (audioRecord?.state != AudioRecord.STATE_INITIALIZED) {
                onError()
                return
            }

            audioRecord?.startRecording()
            isListening = true

            recordingJob = CoroutineScope(Dispatchers.IO).launch {
                val audioBuffer = ShortArray(bufferSize)

                while (isActive && isListening && !hasTriggered) {
                    val readResult = audioRecord?.read(audioBuffer, 0, bufferSize) ?: 0

                    if (readResult > 0) {
                        val amplitude = calculateAmplitude(audioBuffer, readResult)

                        if (System.currentTimeMillis() % 500 < 100) {
                        }

                        if (amplitude > AMPLITUDE_THRESHOLD && !hasTriggered) {
                            hasTriggered = true

                            withContext(Dispatchers.Main) {
                                onSoundDetectedCallback?.invoke()
                            }

                            stopListening()
                        }
                    }

                    delay(10)
                }
            }

        } catch (e: SecurityException) {
            onError()
        } catch (e: Exception) {
            onError()
        }
    }

    private fun calculateAmplitude(buffer: ShortArray, readSize: Int): Int {
        var sum = 0.0
        for (i in 0 until readSize) {
            sum += abs(buffer[i].toDouble())
        }
        val average = sum / readSize
        return average.toInt()
    }

    fun stopListening() {

        isListening = false
        recordingJob?.cancel()
        recordingJob = null

        try {
            audioRecord?.stop()
            audioRecord?.release()
        } catch (e: Exception) {
        }

        audioRecord = null
    }

    fun isCurrentlyListening(): Boolean = isListening

    fun destroy() {
        stopListening()
        onSoundDetectedCallback = null
    }
}