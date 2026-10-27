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
    /**
     * Atmospheric, rich Norse tagelharpa & ambient cello arpeggio for Main Menu.
     */
    fun startMenuMusic() {
        if (isMusicMuted) return
        if (currentMusicJob?.isActive == true && !isPlayingBattleMusic) return

        stopMusic()
        isPlayingBattleMusic = false
        currentMusicJob = scope.launch {
            // Nordic modal sequence in D Dorian (D3, F3, G3, A3, C4, D4, E4, F4)
            val melody = doubleArrayOf(146.83, 174.61, 196.00, 220.00, 261.63, 293.66, 261.63, 220.00, 196.00, 174.61)
            var idx = 0

            while (isActive && !isMusicMuted) {
                val freq = melody[idx % melody.size]
                playNordicAcousticPluck(freq, durationMs = 450, volume = 0.18f, rootDrone = 73.42)
                delay(400L)
                idx++
            }
        }
    }

    /**
     * Driving Norse war march with deep resonant acoustic war drums and heavy brass horn chords.
     */
    fun startBattleMusic() {
        if (isMusicMuted) return
        if (currentMusicJob?.isActive == true && isPlayingBattleMusic) return

        stopMusic()
        isPlayingBattleMusic = true
        currentMusicJob = scope.launch {
            // Heavy D minor battle cadence with dual drums
            val warChords = doubleArrayOf(146.83, 146.83, 174.61, 146.83, 220.00, 196.00, 174.61, 130.81)
            var beat = 0

            while (isActive && !isMusicMuted) {
                val freq = warChords[beat % warChords.size]
                val isKickDrum = (beat % 2 == 0)
                val isWarHorn = (beat % 4 == 0)

                playNordicWarBeat(freq, isKickDrum = isKickDrum, isHorn = isWarHorn, durationMs = 240, volume = 0.22f)
                delay(220L)
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

    fun playWaterSplash() {
        if (isMuted) return
        scope.launch {
            // Realistic ocean wave splash and bubbling foam
            val numSamples = (sampleRate * 0.18).toInt()
            val buffer = ShortArray(numSamples)
            for (i in 0 until numSamples) {
                val t = i.toDouble() / sampleRate
                val env = exp(-16.0 * t)
                val noise = (Math.random() - 0.5) * 0.75
                val waveSwell = sin(2.0 * PI * 130.0 * t) * 0.4
                val sample = (waveSwell + noise) * env * 0.45 * Short.MAX_VALUE
                buffer[i] = sample.toInt().coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
            }
            playRawBuffer(buffer)
        }
    }

    fun playFrostFreeze() {
        if (isMuted) return
        scope.launch {
            val notes = doubleArrayOf(880.0, 1174.66, 1567.98)
            for (f in notes) {
                playTone(frequency = f, durationMs = 80, volume = 0.28f, decaySpeed = 30.0)
                delay(40L)
            }
        }
    }

    fun playGungnirBeam() {
        if (isMuted) return
        scope.launch {
            playTone(frequency = 784.0, durationMs = 180, volume = 0.4f, decaySpeed = 12.0)
        }
    }

    fun playCurseTrigger() {
        if (isMuted) return
        scope.launch {
            // Dark sinister descending dissonance
            val notes = doubleArrayOf(330.0, 311.13, 293.66, 277.18)
            for (f in notes) {
                playTone(frequency = f, durationMs = 90, volume = 0.38f, decaySpeed = 20.0)
                delay(60L)
            }
        }
    }

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

    private fun playNordicAcousticPluck(freq: Double, durationMs: Int, volume: Float, rootDrone: Double) {
        val numSamples = (sampleRate * (durationMs / 1000.0)).toInt()
        val buffer = ShortArray(numSamples)
        for (i in 0 until numSamples) {
            val t = i.toDouble() / sampleRate
            val env = exp(-6.5 * t)
            // Acoustic string harmonics (fundamental + subtle 2nd & 3rd octave harmonics for warm wooden timbre)
            val h1 = sin(2.0 * PI * freq * t)
            val h2 = sin(4.0 * PI * freq * t) * 0.45
            val h3 = sin(6.0 * PI * freq * t) * 0.2
            val plucked = (h1 + h2 + h3) * env

            // Resonant low cello drone
            val droneEnv = exp(-3.0 * t)
            val drone = sin(2.0 * PI * rootDrone * t) * 0.3 * droneEnv

            val sample = (plucked + drone) * volume * Short.MAX_VALUE
            buffer[i] = sample.toInt().coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
        }
        playRawBuffer(buffer)
    }

    private fun playNordicWarBeat(freq: Double, isKickDrum: Boolean, isHorn: Boolean, durationMs: Int, volume: Float) {
        val numSamples = (sampleRate * (durationMs / 1000.0)).toInt()
        val buffer = ShortArray(numSamples)
        for (i in 0 until numSamples) {
            val t = i.toDouble() / sampleRate
            val env = exp(-10.0 * t)
            // Low-end acoustic bass rhythm
            val bass = sin(2.0 * PI * freq * t) * env

            // Deep Bodhrán / Taiko War Drum (sub-bass 50Hz punch with resonant body decay)
            val drum = if (isKickDrum) {
                val drumEnv = exp(-18.0 * t)
                val punch = sin(2.0 * PI * 48.0 * t) * drumEnv * 1.3
                val woodBody = (Math.random() - 0.5) * 0.25 * drumEnv
                punch + woodBody
            } else 0.0

            // War horn chord layer
            val horn = if (isHorn) {
                val hornEnv = exp(-7.0 * t)
                val hFreq = freq * 1.5
                sin(2.0 * PI * hFreq * t) * hornEnv * 0.35
            } else 0.0

            val sample = (bass + drum + horn) * volume * Short.MAX_VALUE
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
