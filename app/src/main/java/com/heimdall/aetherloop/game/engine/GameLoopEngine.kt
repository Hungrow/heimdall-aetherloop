package com.heimdall.aetherloop.game.engine

import android.graphics.PointF
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.geometry.Offset
import com.heimdall.aetherloop.core.HapticManager
import com.heimdall.aetherloop.core.PlayerProgressManager
import com.heimdall.aetherloop.core.SoundSynthesizer
import com.heimdall.aetherloop.game.model.*
import com.heimdall.aetherloop.game.physics.BifrostTrack
import com.heimdall.aetherloop.game.physics.ParticleEngine
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.concurrent.atomic.AtomicLong
import kotlin.math.hypot

enum class GameState {
    MISSION_BRIEFING,
    PLAYING,
    REALM_STABILIZED,
    VOID_OVERLOAD,
    PAUSED
}

/**
 * Master Game Loop Engine for AetherLoop.
 *
 * Coordinates track mathematics, continuous sand cascades, Shield health,
 * vessel queues, and interactive boost/tap mechanics.
 */
class GameLoopEngine(
    val soundSynthesizer: SoundSynthesizer,
    val hapticManager: HapticManager,
    val progressManager: PlayerProgressManager
) {
    val track = BifrostTrack()
    val sandMatrix = SandArtMatrix(cols = 26, rows = 20)
    val particleEngine = ParticleEngine(
        soundSynthesizer = soundSynthesizer,
        hapticManager = hapticManager
    )

    private val vesselIdGenerator = AtomicLong(1L)
    private val tempPoint = PointF()

    var currentLevel: RealmLevel = RealmLevel.ALL_LEVELS.first()
        private set

    val activeVessels = mutableStateListOf<Vessel>()
    val conduits = mutableStateListOf<Conduit>()
    val vesselQueue = mutableStateListOf<AetherColor>()

    val heldVesselColor = mutableStateOf<AetherColor?>(null)
    val isWarpSpeed = mutableStateOf(false)
    val clearedProgress = mutableFloatStateOf(0f)

    // Shield Health (100% -> 0% Defeat)
    val shieldHealth = mutableFloatStateOf(100f)

    // Score, Combos, and Stars
    val currentScore = mutableIntStateOf(0)
    val comboMultiplier = mutableIntStateOf(1)
    private var comboDecayTimer = 0f

    val earnedStars = mutableIntStateOf(0)
    val isNewHighScore = mutableStateOf(false)

    // Relic Powers: Chrono Stasis & Gjallarhorn
    val chronoCharge = mutableFloatStateOf(0f)
    val isChronoActive = mutableStateOf(false)
    private var chronoTimer = 0f

    val gjallarhornCharge = mutableFloatStateOf(0f)
    val isGjallarhornActive = mutableStateOf(false)
    private var gjallarhornTimer = 0f

    private val _gameState = MutableStateFlow(GameState.MISSION_BRIEFING)
    val gameState: StateFlow<GameState> = _gameState.asStateFlow()

    private val _levelIndex = MutableStateFlow(0)
    val levelIndex: StateFlow<Int> = _levelIndex.asStateFlow()

    init {
        particleEngine.onVesselCompleted = { vessel ->
            currentScore.intValue += 250 * comboMultiplier.intValue
            progressManager.addHarvestedAether(vessel.capacity)
            chargeRelicPowers(25)

            // Reward shield recovery on vessel completion!
            shieldHealth.floatValue = (shieldHealth.floatValue + 8f).coerceAtMost(100f)
        }

        particleEngine.onParticleCaptured = { points ->
            currentScore.intValue += points
            comboDecayTimer = 2.4f
            if (comboMultiplier.intValue < 5) {
                comboMultiplier.intValue++
            }
            chargeRelicPowers(1)
        }

        particleEngine.onParticleSpilledToVoid = {
            if (_gameState.value == GameState.PLAYING) {
                // Spilled aether damages the Heimdall Shield!
                shieldHealth.floatValue = (shieldHealth.floatValue - 0.65f).coerceAtLeast(0f)
                if (shieldHealth.floatValue <= 0f) {
                    _gameState.value = GameState.VOID_OVERLOAD
                    soundSynthesizer.playVesselFull()
                    hapticManager.onRealmComplete()
                }
            }
        }

        particleEngine.onArtworkCleared = {
            if (_gameState.value == GameState.PLAYING) {
                onLevelCompleted()
            }
        }

        loadLevel(0)
    }

    private fun chargeRelicPowers(amount: Int) {
        val chronoStep = amount.toFloat() / AetherPower.CHRONO_STASIS.chargeThreshold.toFloat()
        chronoCharge.floatValue = (chronoCharge.floatValue + chronoStep).coerceAtMost(1.0f)

        val hornStep = amount.toFloat() / AetherPower.GJALLARHORN_SURGE.chargeThreshold.toFloat()
        gjallarhornCharge.floatValue = (gjallarhornCharge.floatValue + hornStep).coerceAtMost(1.0f)
    }

    fun startLevelAction() {
        _gameState.value = GameState.PLAYING
    }

    fun activateChronoStasis(): Boolean {
        if (chronoCharge.floatValue >= 1.0f && !isChronoActive.value) {
            isChronoActive.value = true
            particleEngine.isChronoStasisActive = true
            chronoTimer = AetherPower.CHRONO_STASIS.activeDurationSec
            chronoCharge.floatValue = 0f
            soundSynthesizer.playLaunchWhoosh()
            return true
        }
        return false
    }

    fun activateGjallarhorn(): Boolean {
        if (gjallarhornCharge.floatValue >= 1.0f && !isGjallarhornActive.value) {
            isGjallarhornActive.value = true
            particleEngine.isGjallarhornActive = true
            gjallarhornTimer = AetherPower.GJALLARHORN_SURGE.activeDurationSec
            gjallarhornCharge.floatValue = 0f
            soundSynthesizer.playVesselFull()
            hapticManager.onVesselFilled()
            return true
        }
        return false
    }

    /**
     * Initializes a realm level. Starts with EMPTY track so player is required to play!
     */
    fun loadLevel(index: Int) {
        val safeIndex = index.coerceIn(0, RealmLevel.ALL_LEVELS.lastIndex)
        _levelIndex.value = safeIndex
        currentLevel = RealmLevel.ALL_LEVELS[safeIndex]

        activeVessels.clear()
        conduits.clear()
        vesselQueue.clear()
        heldVesselColor.value = null
        isWarpSpeed.value = false
        currentScore.intValue = 0
        comboMultiplier.intValue = 1
        comboDecayTimer = 0f
        clearedProgress.floatValue = 0f
        shieldHealth.floatValue = 100f
        earnedStars.intValue = 0
        isNewHighScore.value = false

        // Load Sand Pattern
        sandMatrix.loadPattern(safeIndex)

        // Build Conduits
        val colors = currentLevel.colors
        val numConduits = colors.size
        val colStep = sandMatrix.cols / numConduits

        for (i in 0 until numConduits) {
            val colStart = i * colStep
            val colEnd = ((i + 1) * colStep - 1).coerceAtMost(sandMatrix.cols - 1)
            val centerCol = (colStart + colEnd) / 2f
            val normX = 0.22f + (centerCol / sandMatrix.cols.toFloat()) * 0.56f

            conduits.add(
                Conduit(
                    id = i,
                    color = colors[i],
                    colRangeStart = colStart,
                    colRangeEnd = colEnd,
                    xNorm = normX,
                    yNorm = 0.47f
                )
            )
        }

        // Populate Queue
        vesselQueue.addAll(currentLevel.availableVesselQueue)
        if (vesselQueue.size < 6) {
            vesselQueue.addAll(currentLevel.availableVesselQueue)
        }

        _gameState.value = GameState.MISSION_BRIEFING
    }

    fun restartCurrentLevel() {
        loadLevel(_levelIndex.value)
    }

    fun nextLevel() {
        if (_levelIndex.value < RealmLevel.ALL_LEVELS.lastIndex) {
            loadLevel(_levelIndex.value + 1)
        } else {
            loadLevel(0)
        }
    }

    /**
     * Handles tap on screen:
     * - If tap was on an active vessel, grants Overdrive Rocket Boost (2x speed)!
     * - Otherwise dispatches the next vessel from the queue onto the conveyor!
     */
    fun handleScreenTap(offset: Offset) {
        if (_gameState.value != GameState.PLAYING) return

        // 1. Check if tap hit any vessel on track
        for (vessel in activeVessels) {
            if (vessel.state == VesselState.ON_TRACK) {
                track.getPointAtProgress(vessel.trackProgress, tempPoint)
                val dist = hypot(tempPoint.x - offset.x, tempPoint.y - offset.y)
                if (dist < 65f) {
                    vessel.applyOverdriveBoost()
                    soundSynthesizer.playLaunchWhoosh()
                    hapticManager.onParticleCaptured()
                    return
                }
            }
        }

        // 2. Otherwise dispatch next vessel
        dispatchNextVessel()
    }

    fun dispatchNextVessel(): Boolean {
        if (_gameState.value != GameState.PLAYING) return false

        val spawnProgress = 0.65f
        val isOccupied = activeVessels.any {
            kotlin.math.abs(it.trackProgress - spawnProgress) < 0.12f
        }
        if (isOccupied && activeVessels.size >= 5) {
            return false
        }

        if (vesselQueue.isEmpty()) {
            vesselQueue.addAll(currentLevel.availableVesselQueue)
        }

        val color = vesselQueue.removeAt(0)
        val newVessel = Vessel(
            id = vesselIdGenerator.incrementAndGet(),
            color = color,
            trackProgress = spawnProgress,
            capacity = 26
        )
        activeVessels.add(newVessel)
        soundSynthesizer.playLaunchWhoosh()
        return true
    }

    fun swapHeldColor() {
        if (vesselQueue.isEmpty()) return
        val currentFront = vesselQueue.removeAt(0)
        val held = heldVesselColor.value
        if (held == null) {
            heldVesselColor.value = currentFront
        } else {
            heldVesselColor.value = currentFront
            vesselQueue.add(0, held)
        }
    }

    fun toggleWarpSpeed() {
        isWarpSpeed.value = !isWarpSpeed.value
    }

    private fun onLevelCompleted() {
        _gameState.value = GameState.REALM_STABILIZED
        soundSynthesizer.playRealmVictory()
        hapticManager.onRealmComplete()

        val score = currentScore.intValue
        val target = currentLevel.targetScore
        val stars = when {
            score >= target && shieldHealth.floatValue >= 70f -> 3
            score >= (target * 0.6f).toInt() -> 2
            else -> 1
        }
        earnedStars.intValue = stars

        val isHigh = progressManager.setRealmHighScore(_levelIndex.value, score)
        isNewHighScore.value = isHigh
        progressManager.setRealmStars(_levelIndex.value, stars)

        if (_levelIndex.value + 1 > progressManager.highestUnlockedRealm) {
            progressManager.highestUnlockedRealm = _levelIndex.value + 1
        }
    }

    fun update(dt: Float, width: Float, height: Float) {
        if (_gameState.value != GameState.PLAYING) return

        track.updateDimensions(width, height)

        if (isChronoActive.value) {
            chronoTimer -= dt
            if (chronoTimer <= 0f) {
                isChronoActive.value = false
                particleEngine.isChronoStasisActive = false
            }
        }

        if (isGjallarhornActive.value) {
            gjallarhornTimer -= dt
            if (gjallarhornTimer <= 0f) {
                isGjallarhornActive.value = false
                particleEngine.isGjallarhornActive = false
            }
        }

        if (comboMultiplier.intValue > 1) {
            comboDecayTimer -= dt
            if (comboDecayTimer <= 0f) {
                comboMultiplier.intValue = (comboMultiplier.intValue - 1).coerceAtLeast(1)
                comboDecayTimer = 1.5f
            }
        }

        val baseMult = when {
            isChronoActive.value -> 0.45f
            isWarpSpeed.value -> 1.65f
            else -> 1.0f
        }
        val effectiveBaseSpeed = currentLevel.baseTrackSpeed * baseMult

        track.beltScrollOffset = (track.beltScrollOffset + effectiveBaseSpeed * dt * 6f) % 1.0f

        // Move Vessels with individual Overdrive Boost support
        val iterator = activeVessels.iterator()
        while (iterator.hasNext()) {
            val vessel = iterator.next()

            var vesselSpeed = effectiveBaseSpeed
            if (vessel.isBoosted) {
                vesselSpeed *= 2.1f
                vessel.boostTimer -= dt
                if (vessel.boostTimer <= 0f) {
                    vessel.isBoosted = false
                }
            }

            vessel.trackProgress = (vessel.trackProgress + vesselSpeed * dt) % 1.0f
            vessel.liquidWaveOffset += dt * 6f

            if (vessel.fullFlashAnim > 0f) {
                vessel.fullFlashAnim = (vessel.fullFlashAnim - dt * 2.5f).coerceAtLeast(0f)
            }

            if (vessel.isFull && vessel.state == VesselState.ON_TRACK && vessel.fullFlashAnim <= 0f) {
                if (vessel.trackProgress in 0.68f..0.78f) {
                    vessel.state = VesselState.DOCKING
                }
            }

            if (vessel.state == VesselState.DOCKING) {
                iterator.remove()
                if (vesselQueue.size < 4) {
                    vesselQueue.addAll(currentLevel.availableVesselQueue)
                }
            }
        }

        // Physics & Particle Simulation
        particleEngine.update(
            dt = dt,
            width = width,
            height = height,
            sandMatrix = sandMatrix,
            conduits = conduits,
            vessels = activeVessels,
            track = track,
            flowRatePerSec = currentLevel.flowRatePerSec * 1.5f,
            comboMultiplier = comboMultiplier.intValue
        )

        clearedProgress.floatValue = sandMatrix.clearedRatio
    }
}
