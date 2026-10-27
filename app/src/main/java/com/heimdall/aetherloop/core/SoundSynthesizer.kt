package com.heimdall.aetherloop.core

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import kotlinx.coroutines.*
import java.util.concurrent.atomic.AtomicInteger
import kotlin.math.PI
import kotlin.math.exp
import kotlin.math.sin

/**
 * Procedural Real-time Sound & Music Synthesizer.
 *
 * Fully crash-proof:
 * 1. Uses a SINGLE streaming [AudioTrack] in MODE_STREAM for background music (0 per-beat allocations).
 * 2. Uses a fixed pool of 4 reusable static [AudioTrack] instances for sound effects (never exhausts Android limit).
 * 3. Catches [Throwable] to prevent any native audio driver faults from killing the game.
 */
class SoundSynthesizer {

    private val sampleRate = 44100
    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    var isMuted: Boolean = false
    var isMusicMuted: Boolean = false

    private var currentMusicJob: Job? = null
    private var isPlayingBattleMusic = false

    // Single persistent streaming AudioTrack for BGM (Zero track churn)
    private var musicTrack: AudioTrack? = null

    // Fixed pool of 4 static AudioTracks for SFX
    private val sfxPoolSize = 4
    private val sfxTracks = arrayOfNulls<AudioTrack>(sfxPoolSize)
    private val nextSfxIndex = AtomicInteger(0)

    init {
        initAudioEngine()
    }

    private fun initAudioEngine() {
        try {
            val minBuff = AudioTrack.getMinBufferSize(
                sampleRate,
                AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT
            ).coerceAtLeast(sampleRate / 4)

            musicTrack = AudioTrack.Builder()
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_GAME)
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .build()
                )
                .setAudioFormat(
                    AudioFormat.Builder()
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .setSampleRate(sampleRate)
                        .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                        .build()
                )
                .setBufferSizeInBytes(minBuff * 2)
                .setTransferMode(AudioTrack.MODE_STREAM)
                .build()

            musicTrack?.play()
        } catch (_: Throwable) {
            musicTrack = null
        }
    }

    // -------------------------------------------------------------
    // BACKGROUND MUSIC (BGM) STREAMING
    // -------------------------------------------------------------

    /**
     * Atmospheric, rich Norse tagelharpa & ambient cello arpeggio for Main Menu.
     */
    fun startMenuMusic() {
        if (isMusicMuted) return
        if (currentMusicJob?.isActive == true && !isPlayingBattleMusic) return

        stopMusic()
        isPlayingBattleMusic = false
        currentMusicJob = scope.launch {
            val melody = doubleArrayOf(146.83, 174.61, 196.00, 220.00, 261.63, 293.66, 261.63, 220.00, 196.00, 174.61)
            var idx = 0

            while (isActive && !isMusicMuted) {
                val freq = melody[idx % melody.size]
                streamNordicPluck(freq, durationMs = 380, volume = 0.16f, rootDrone = 73.42)
                delay(360L)
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
            val warChords = doubleArrayOf(146.83, 146.83, 174.61, 146.83, 220.00, 196.00, 174.61, 130.81)
            var beat = 0

            while (isActive && !isMusicMuted) {
                val freq = warChords[beat % warChords.size]
                val isKickDrum = (beat % 2 == 0)
                val isWarHorn = (beat % 4 == 0)

                streamNordicWarBeat(freq, isKickDrum = isKickDrum, isHorn = isWarHorn, durationMs = 220, volume = 0.20f)
                delay(200L)
                beat++
            }
        }
    }

    fun stopMusic() {
        currentMusicJob?.cancel()
        currentMusicJob = null
        try {
            musicTrack?.pause()
            musicTrack?.flush()
        } catch (_: Throwable) {}
    }

    private fun streamNordicPluck(freq: Double, durationMs: Int, volume: Float, rootDrone: Double) {
        val track = musicTrack ?: return
        try {
            if (track.playState != AudioTrack.PLAYSTATE_PLAYING) {
                track.play()
            }
            val numSamples = (sampleRate * (durationMs / 1000.0)).toInt()
            val buffer = ShortArray(numSamples)
            for (i in 0 until numSamples) {
                val t = i.toDouble() / sampleRate
                val env = exp(-6.5 * t)
                val h1 = sin(2.0 * PI * freq * t)
                val h2 = sin(4.0 * PI * freq * t) * 0.45
                val h3 = sin(6.0 * PI * freq * t) * 0.2
                val plucked = (h1 + h2 + h3) * env

                val droneEnv = exp(-3.0 * t)
                val drone = sin(2.0 * PI * rootDrone * t) * 0.3 * droneEnv

                val sample = (plucked + drone) * volume * Short.MAX_VALUE
                buffer[i] = sample.toInt().coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
            }
            track.write(buffer, 0, buffer.size)
        } catch (_: Throwable) {}
    }

    private fun streamNordicWarBeat(freq: Double, isKickDrum: Boolean, isHorn: Boolean, durationMs: Int, volume: Float) {
        val track = musicTrack ?: return
        try {
            if (track.playState != AudioTrack.PLAYSTATE_PLAYING) {
                track.play()
            }
            val numSamples = (sampleRate * (durationMs / 1000.0)).toInt()
            val buffer = ShortArray(numSamples)
            for (i in 0 until numSamples) {
                val t = i.toDouble() / sampleRate
                val env = exp(-10.0 * t)
                val bass = sin(2.0 * PI * freq * t) * env

                val drum = if (isKickDrum) {
                    val drumEnv = exp(-18.0 * t)
                    val punch = sin(2.0 * PI * 48.0 * t) * drumEnv * 1.3
                    val woodBody = (Math.random() - 0.5) * 0.25 * drumEnv
                    punch + woodBody
                } else 0.0

                val horn = if (isHorn) {
                    val hornEnv = exp(-7.0 * t)
                    val hFreq = freq * 1.5
                    sin(2.0 * PI * hFreq * t) * hornEnv * 0.35
                } else 0.0

                val sample = (bass + drum + horn) * volume * Short.MAX_VALUE
                buffer[i] = sample.toInt().coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
            }
            track.write(buffer, 0, buffer.size)
        } catch (_: Throwable) {}
    }

    // -------------------------------------------------------------
    // SOUND EFFECTS (SFX) WITH SAFE FIXED POOL
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
            playPoolBuffer(buffer)
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
            playPoolBuffer(buffer)
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
            playPoolBuffer(buffer)
        }
    }

    fun playCannonFire() {
        if (isMuted) return
        scope.launch {
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
            playPoolBuffer(buffer)
        }
    }

    fun playCannonHit() {
        if (isMuted) return
        scope.launch {
            playTone(frequency = 180.0, durationMs = 120, volume = 0.5f, decaySpeed = 22.0)
        }
    }

    fun playShipUpgrade() {
        if (isMuted) return
        scope.launch {
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
            playPoolBuffer(buffer)
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
            playPoolBuffer(buffer)
        }
    }

    fun playLaunchWhoosh() {
        if (isMuted) return
        scope.launch {
            playTone(frequency = 440.0, durationMs = 100, volume = 0.3f, decaySpeed = 30.0)
        }
    }

    fun playWaterSplash() {
        if (isMuted) return
        scope.launch {
            val numSamples = (sampleRate * 0.16).toInt()
            val buffer = ShortArray(numSamples)
            for (i in 0 until numSamples) {
                val t = i.toDouble() / sampleRate
                val env = exp(-18.0 * t)
                val noise = (Math.random() - 0.5) * 0.7
                val waveSwell = sin(2.0 * PI * 130.0 * t) * 0.35
                val sample = (waveSwell + noise) * env * 0.4 * Short.MAX_VALUE
                buffer[i] = sample.toInt().coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
            }
            playPoolBuffer(buffer)
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
            val notes = doubleArrayOf(330.0, 311.13, 293.66, 277.18)
            for (f in notes) {
                playTone(frequency = f, durationMs = 90, volume = 0.38f, decaySpeed = 20.0)
                delay(60L)
            }
        }
    }

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
        playPoolBuffer(buffer)
    }

    /**
     * Reuses an AudioTrack from the fixed pool of 4, guaranteeing ZERO leaks or track exhaustion.
     */
    private fun playPoolBuffer(buffer: ShortArray) {
        val slot = (nextSfxIndex.getAndIncrement() and 0x7FFFFFFF) % sfxPoolSize
        try {
            var track = sfxTracks[slot]
            if (track != null) {
                try {
                    track.stop()
                    track.release()
                } catch (_: Throwable) {}
                sfxTracks[slot] = null
            }

            track = AudioTrack.Builder()
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
            sfxTracks[slot] = track
        } catch (_: Throwable) {}
    }
}
