package com.cs407.whaap_it.util

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import kotlin.math.sqrt

object ShakeDetector : SensorEventListener {

    private const val SHAKE_THRESHOLD = 30f
    private var sensorManager: SensorManager? = null
    private var onShake: (() -> Unit)? = null

    fun startListening(context: Context, onShakeCallback: () -> Unit) {
        sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        onShake = onShakeCallback

        val accelerometer = sensorManager!!.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        sensorManager!!.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME)
    }

    fun stopListening() {
        sensorManager?.unregisterListener(this)
        sensorManager = null
    }

    override fun onSensorChanged(event: SensorEvent?) {
        event ?: return

        val ax = event.values[0]
        val ay = event.values[1]
        val az = event.values[2]

        val magnitude = sqrt(ax * ax + ay * ay + az * az)

        if (magnitude > SHAKE_THRESHOLD) {
            onShake?.invoke()
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
}
