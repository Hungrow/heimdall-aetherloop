package com.heimdall.aetherloop.core

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.exp
import kotlin.math.sin

/**
 * Procedural Real-time Sound Synthesizer.
 *
 * Generates responsive tactile SFX for Arkanoid paddle hits, wooden ship splinters,
 * thunder strikes, ballista shots, and victory fanfare completely in code via [AudioTrack].
 */
class SoundSynthesizer {

    private val sampleRate = 44100
    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    var isMuted: Boolean = false

    /**
     * Resonant clank when the rune sphere bounces off the player's shield paddle.
     */
    fun playPaddleHit() {
        if (isMuted) return
        scope.launch {
            playTone(frequency = 587.33, durationMs = 70, volume = 0.35f, decaySpeed = 45.0)
        }
    }

    /**
     * Wooden crunch when the ball strikes an enemy Viking longship.
     */
    fun playShipHit() {
        if (isMuted) return
        scope.launch {
            val numSamples = (sampleRate * 0.08).toInt()
            val buffer = ShortArray(numSamples)
            for (i in 0 until numSamples) {
                val t = i.toDouble() / sampleRate
                val freq = 220.0 - (100.0 * (t / 0.08))
                val envelope = exp(-35.0 * t)
                val noise = (Math.random() - 0.5) * 0.3
                val sample = (sin(2.0 * PI * freq * t) + noise) * envelope * 0.4 * Short.MAX_VALUE
                buffer[i] = sample.toInt().coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
            }
            playRawBuffer(buffer)
        }
    }

    /**
     * Deep explosion rumble when an enemy longship is destroyed.
     */
    fun playShipExplosion() {
        if (isMuted) return
        scope.launch {
            val numSamples = (sampleRate * 0.22).toInt()
            val buffer = ShortArray(numSamples)
            for (i in 0 until numSamples) {
                val t = i.toDouble() / sampleRate
                val envelope = exp(-12.0 * t)
                val noise = (Math.random() - 0.5) * 0.8
                val lowFreq = sin(2.0 * PI * 80.0 * t)
                val sample = (lowFreq + noise) * envelope * 0.45 * Short.MAX_VALUE
                buffer[i] = sample.toInt().coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
            }
            playRawBuffer(buffer)
        }
    }

    /**
     * Laser ballista shot fired from paddle.
     */
    fun playLaserShot() {
        if (isMuted) return
        scope.launch {
            val numSamples = (sampleRate * 0.09).toInt()
            val buffer = ShortArray(numSamples)
            for (i in 0 until numSamples) {
                val t = i.toDouble() / sampleRate
                val freq = 1100.0 - (800.0 * (t / 0.09))
                val envelope = exp(-28.0 * t)
                val sample = sin(2.0 * PI * freq * t) * envelope * 0.3 * Short.MAX_VALUE
                buffer[i] = sample.toInt().coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
            }
            playRawBuffer(buffer)
        }
    }

    /**
     * Chime when catching a power-up rune.
     */
    fun playPowerUpCatch() {
        if (isMuted) return
        scope.launch {
            playTone(frequency = 880.0, durationMs = 60, volume = 0.3f, decaySpeed = 25.0)
            playTone(frequency = 1174.66, durationMs = 90, volume = 0.35f, decaySpeed = 20.0)
        }
    }

    /**
     * Tone when a ball is lost past the bottom line.
     */
    fun playBallLost() {
        if (isMuted) return
        scope.launch {
            val numSamples = (sampleRate * 0.25).toInt()
            val buffer = ShortArray(numSamples)
            for (i in 0 until numSamples) {
                val t = i.toDouble() / sampleRate
                val freq = 350.0 - (180.0 * (t / 0.25))
                val envelope = exp(-10.0 * t)
                val sample = sin(2.0 * PI * freq * t) * envelope * 0.35 * Short.MAX_VALUE
                buffer[i] = sample.toInt().coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
            }
            playRawBuffer(buffer)
        }
    }

    /**
     * Fanfare on fleet victory.
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

    fun playLaunchWhoosh() {
        if (isMuted) return
        scope.launch {
            playTone(frequency = 440.0, durationMs = 100, volume = 0.3f, decaySpeed = 30.0)
        }
    }

    // Backward compatibility bridges
    fun playVesselFull() { playPowerUpCatch() }
    fun playParticleCatch(noteIndex: Int = 0) { playPaddleHit() }
    fun playVesselLaunch() { playLaunchWhoosh() }
    fun playStarEarned() { playRealmVictory() }

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
        } catch (_: Exception) {}
    }
}
