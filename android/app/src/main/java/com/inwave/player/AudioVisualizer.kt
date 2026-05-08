package com.inwave.player

import android.media.audiofx.Visualizer
import androidx.annotation.OptIn
import androidx.media3.common.util.UnstableApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

private const val MAX_MAGNITUDE = 128
private const val BINS = 4
private const val BIN_SIZE = 128 / BINS  // 32
private const val NORM = BIN_SIZE * MAX_MAGNITUDE  // 32 * 128 = 4096

@OptIn(UnstableApi::class)
class AudioVisualizer : AutoCloseable {
    private val _wave = MutableStateFlow(FloatArray(4))
    val wavePulse = _wave.asStateFlow()

    private val listener = object : Visualizer.OnDataCaptureListener {
        override fun onFftDataCapture(
            visualizer: Visualizer?,
            bytes: ByteArray?,
            samplingRate: Int
        ) { }

        override fun onWaveFormDataCapture(
            visualizer: Visualizer?,
            waveform: ByteArray?,
            samplingRate: Int
        ) {
            waveform?.let { data ->
                val pointsCount = 3
                val step = maxOf(1, data.size / pointsCount)
                val result = FloatArray(pointsCount)

                for (i in 0 until pointsCount) {
                    result[i] = (data[i * step].toFloat() / 128)
                }

                _wave.value = result
            }/*

            waveform?.let { data ->
                val result = FloatArray(4)

                for (i in data.indices) {
                    val amplitude = abs(data[i].toInt() + 128)  // 0..128
                    result[i / 32] += amplitude
                }

                for (i in 0..3) {
                    result[i] = result[i] / 4096
                }

                _wave.value = result
            }*/
        }
    }

    private val visualizer by lazy {
        InWaveMediaSessionService.instance?.player?.audioSessionId?.let {
            Visualizer(it)
        }?.apply {
            captureSize = 128
            setDataCaptureListener(listener, Visualizer.getMaxCaptureRate() / 4, true, false)
            enabled = true
        }
    }

    fun startListener() {
        visualizer?.enabled = true
    }

    fun pauseListener() {
        visualizer?.enabled = false
    }

    fun stopListener() {
        visualizer?.run {
            enabled = false
            release()
        }
    }

    override fun close() {
        stopListener()
    }
}