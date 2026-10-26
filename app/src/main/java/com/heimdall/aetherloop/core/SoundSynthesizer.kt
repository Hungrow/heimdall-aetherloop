package com.heimdall.aetherloop.core

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.util.concurrent.ConcurrentLinkedQueue
import kotlin.math.PI
import kotlin.math.exp
import kotlin.math.sin

/**
 * Procedural Real-time Sound Synthesizer.
 *
 * Generates ASMR crystalline chimes, resonant energy bells, and cosmic hums
 * completely in code via [AudioTrack]. Eliminates any external asset dependency
 * and ensures 100% offline reliability.
 */
class SoundSynthesizer {

    private val sampleRate = 44100
    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    var isMuted: Boolean = false

    // Celestial Pentatonic Frequencies (Hz) for harmonious ASMR cascades
    private val pentatonicNotes = doubleArrayOf(
        523.25, // C5
        587.33, // D5
        659.25, // E5
        783.99, // G5
        880.00, // A5
        1046.50 // C6
    )

    private var noteCursor = 0

    /**
     * Plays a crystalline chime when a particle is captured.
     * Sequenced along a pentatonic scale so rapid cascades sound like a soothing harp.
     */
    fun playParticleCatch(colorIndex: Int) {
        if (isMuted) return
        scope.launch {
            val freq = pentatonicNotes[(colorIndex + noteCursor++) % pentatonicNotes.size]
            val durationMs = 90
            playTone(frequency = freq, durationMs = durationMs, volume = 0.28f, decaySpeed = 35.0)
        }
    }

    /**
     * Plays a rich resonant bell when a pod is completely filled (100% capacity).
     */
    fun playVesselFull() {
        if (isMuted) return
        scope.launch {
            // Dual-frequency bell with harmonic shimmer
            val fundamental = 880.0
            val harmonic = 1320.0
            val numSamples = (sampleRate * 0.45).toInt()
            val buffer = ShortArray(numSamples)
            for (i in 0 until numSamples) {
                val t = i.toDouble() / sampleRate
                val envelope = exp(-8.0 * t)
                val sample1 = sin(2.0 * PI * fundamental * t)
                val sample2 = 0.5 * sin(2.0 * PI * harmonic * t)
                val combined = (sample1 + sample2) * envelope * 0.4 * Short.MAX_VALUE
                buffer[i] = combined.toInt().coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
            }
            playRawBuffer(buffer)
        }
    }

    /**
     * Plays a triumphant realm completion fanfare chord.
     */
    fun playRealmVictory() {
        if (isMuted) return
        scope.launch {
            val chord = doubleArrayOf(523.25, 659.25, 783.99, 1046.50)
            val numSamples = (sampleRate * 0.8).toInt()
            val buffer = ShortArray(numSamples)
            for (i in 0 until numSamples) {
                val t = i.toDouble() / sampleRate
                val envelope = exp(-4.0 * t)
                var sum = 0.0
                for (f in chord) {
                    sum += sin(2.0 * PI * f * t)
                }
                val sample = (sum / chord.size) * envelope * 0.45 * Short.MAX_VALUE
                buffer[i] = sample.toInt().coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
            }
            playRawBuffer(buffer)
        }
    }

    /**
     * Plays a subtle whoosh/pulse on launching a new vessel.
     */
    fun playLaunchWhoosh() {
        if (isMuted) return
        scope.launch {
            val numSamples = (sampleRate * 0.15).toInt()
            val buffer = ShortArray(numSamples)
            for (i in 0 until numSamples) {
                val t = i.toDouble() / sampleRate
                val freq = 220.0 + (300.0 * (1.0 - (t / 0.15)))
                val envelope = sin(PI * (t / 0.15))
                val sample = sin(2.0 * PI * freq * t) * envelope * 0.3 * Short.MAX_VALUE
                buffer[i] = sample.toInt().coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
            }
            playRawBuffer(buffer)
        }
    }

    private fun playTone(frequency: Double, durationMs: Int, volume: Float, decaySpeed: Double) {
        val numSamples = (sampleRate * (durationMs / 1000.0)).toInt()
        val buffer = ShortArray(numSamples)
        for (i in 0 until numSamples) {
            val t = i.toDouble() / sampleRate
            val envelope = exp(-decaySpeed * t)
            val sample = sin(2.0 * PI * frequency * t) * envelope * volume * Short.MAX_VALUE
            buffer[i] = sample.toInt().coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
        }
        playRawBuffer(buffer)
    }

    private fun playRawBuffer(buffer: ShortArray) {
        try {
            val track = AudioTrack.Builder()
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_GAME)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build()
                )
                .setAudioFormat(
                    AudioFormat.Builder()
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .setSampleRate(sampleRate)
                        .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                        .build()
                )
                .setBufferSizeInBytes(buffer.size * 2)
                .setTransferMode(AudioTrack.MODE_STATIC)
                .build()

            track.write(buffer, 0, buffer.size)
            track.play()
            track.setNotificationMarkerPosition(buffer.size)
            track.setPlaybackPositionUpdateListener(object : AudioTrack.OnPlaybackPositionUpdateListener {
                override fun onMarkerReached(t: AudioTrack?) {
                    try {
                        t?.stop()
                        t?.release()
                    } catch (_: Exception) {}
                }
                override fun onPeriodicNotification(t: AudioTrack?) {}
            })
        } catch (_: Exception) {
            // Audio error safeguard - fail silently without crashing game
        }
    }
}
