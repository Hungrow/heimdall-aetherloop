package com.heimdall.aetherloop.core

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import kotlinx.coroutines.*
import kotlin.math.PI
import kotlin.math.exp
import kotlin.math.sin

/**
 * Procedural Real-time Sound & Music Synthesizer.
 *
 * Generates tactile SFX and dynamic Norse background music loops completely
 * in code via [AudioTrack], with zero external asset overhead.
 */
class SoundSynthesizer {

    private val sampleRate = 44100
    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    var isMuted: Boolean = false
    var isMusicMuted: Boolean = false

    private var currentMusicJob: Job? = null
    private var isPlayingBattleMusic = false

    // -------------------------------------------------------------
    // BACKGROUND MUSIC (BGM) SYNTHESIS
    // -------------------------------------------------------------

    /**
     * Atmospheric, slow Norse harp & cello arpeggio for Main Menu.
     */
    fun startMenuMusic() {
        if (isMusicMuted) return
        if (currentMusicJob?.isActive == true && !isPlayingBattleMusic) return

        stopMusic()
        isPlayingBattleMusic = false
        currentMusicJob = scope.launch {
            // Dorian / Pentatonic D minor arpeggio (D3, F3, A3, C4, D4, E4, F4, A4)
            val notes = doubleArrayOf(146.83, 174.61, 220.00, 261.63, 293.66, 329.63, 349.23, 440.00)
            val pattern = intArrayOf(0, 2, 4, 1, 3, 5, 2, 4, 7, 5, 3, 1)

            while (isActive && !isMusicMuted) {
                for (noteIdx in pattern) {
                    if (!isActive || isMusicMuted) break
                    val freq = notes[noteIdx % notes.size]
                    // Plucked string envelope
                    playPluckedTone(freq, durationMs = 380, volume = 0.16f, droneFreq = 73.42) // D2 root drone
                    delay(350L)
                }
            }
        }
    }

    /**
     * Driving, intense Norse war rhythm with percussion and war horns for In-Game battle.
     */
    fun startBattleMusic() {
        if (isMusicMuted) return
        if (currentMusicJob?.isActive == true && isPlayingBattleMusic) return

        stopMusic()
        isPlayingBattleMusic = true
        currentMusicJob = scope.launch {
            // Driving D minor war ostinato with drum pulses
            val bassRiff = doubleArrayOf(146.83, 146.83, 174.61, 146.83, 220.00, 196.00, 174.61, 130.81)
            var beat = 0

            while (isActive && !isMusicMuted) {
                val freq = bassRiff[beat % bassRiff.size]
                val isHeavyBeat = (beat % 4 == 0)
                
                // Play drum + bass note simultaneously
                playWarBeat(freq, isDrum = isHeavyBeat, durationMs = 210, volume = 0.20f)
                delay(200L)
                beat++
            }
        }
    }

    fun stopMusic() {
        currentMusicJob?.cancel()
        currentMusicJob = null
    }

    // -------------------------------------------------------------
    // SOUND EFFECTS (SFX)
    // -------------------------------------------------------------

    fun playPaddleHit() {
        if (isMuted) return
        scope.launch {
            playTone(frequency = 587.33, durationMs = 70, volume = 0.35f, decaySpeed = 45.0)
        }
    }

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

    fun playCannonFire() {
        if (isMuted) return
        scope.launch {
            // Low boom + crackle
            val numSamples = (sampleRate * 0.16).toInt()
            val buffer = ShortArray(numSamples)
            for (i in 0 until numSamples) {
                val t = i.toDouble() / sampleRate
                val freq = 120.0 - (70.0 * (t / 0.16))
                val envelope = exp(-18.0 * t)
                val noise = (Math.random() - 0.5) * 0.4
                val sample = (sin(2.0 * PI * freq * t) + noise) * envelope * 0.45 * Short.MAX_VALUE
                buffer[i] = sample.toInt().coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
            }
            playRawBuffer(buffer)
        }
    }

    fun playCannonHit() {
        if (isMuted) return
        scope.launch {
            // Wood splinter impact & metallic crash
            playTone(frequency = 180.0, durationMs = 120, volume = 0.5f, decaySpeed = 22.0)
        }
    }

    fun playShipUpgrade() {
        if (isMuted) return
        scope.launch {
            // Ascending celebratory fanfare chord (D5 -> F#5 -> A5 -> D6)
            val notes = doubleArrayOf(587.33, 739.99, 880.00, 1174.66)
            for (f in notes) {
                playTone(frequency = f, durationMs = 90, volume = 0.35f, decaySpeed = 18.0)
                delay(65L)
            }
        }
    }

    fun playShipDowngrade() {
        if (isMuted) return
        scope.launch {
            // Descending distress tone
            val notes = doubleArrayOf(440.00, 369.99, 293.66)
            for (f in notes) {
                playTone(frequency = f, durationMs = 110, volume = 0.4f, decaySpeed = 20.0)
                delay(80L)
            }
        }
    }

    fun playPowerUpCatch() {
        if (isMuted) return
        scope.launch {
            playTone(frequency = 880.0, durationMs = 60, volume = 0.3f, decaySpeed = 25.0)
            playTone(frequency = 1174.66, durationMs = 90, volume = 0.35f, decaySpeed = 20.0)
        }
    }

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

    // -------------------------------------------------------------
    // AUDIO ENGINE CORE
    // -------------------------------------------------------------

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

    private fun playPluckedTone(freq: Double, durationMs: Int, volume: Float, droneFreq: Double) {
        val numSamples = (sampleRate * (durationMs / 1000.0)).toInt()
        val buffer = ShortArray(numSamples)
        for (i in 0 until numSamples) {
            val t = i.toDouble() / sampleRate
            val env = exp(-8.0 * t)
            val plucked = sin(2.0 * PI * freq * t) * env
            val drone = sin(2.0 * PI * droneFreq * t) * 0.4
            val sample = (plucked + drone) * volume * Short.MAX_VALUE
            buffer[i] = sample.toInt().coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
        }
        playRawBuffer(buffer)
    }

    private fun playWarBeat(freq: Double, isDrum: Boolean, durationMs: Int, volume: Float) {
        val numSamples = (sampleRate * (durationMs / 1000.0)).toInt()
        val buffer = ShortArray(numSamples)
        for (i in 0 until numSamples) {
            val t = i.toDouble() / sampleRate
            val env = exp(-14.0 * t)
            val bass = sin(2.0 * PI * freq * t) * env
            val drum = if (isDrum) {
                val drumEnv = exp(-22.0 * t)
                (sin(2.0 * PI * 65.0 * t) + (Math.random() - 0.5) * 0.4) * drumEnv * 0.8
            } else 0.0
            val sample = (bass + drum) * volume * Short.MAX_VALUE
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
