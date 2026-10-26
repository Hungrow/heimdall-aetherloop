package com.heimdall.aetherloop.game.engine

import com.heimdall.aetherloop.core.HapticManager
import com.heimdall.aetherloop.core.SoundSynthesizer
import com.heimdall.aetherloop.game.model.*
import com.heimdall.aetherloop.game.physics.BifrostTrack
import com.heimdall.aetherloop.game.physics.ParticleEngine
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.concurrent.atomic.AtomicLong

enum class GameState {
    READY,
    PLAYING,
    REALM_STABILIZED,
    VOID_OVERLOAD,
    PAUSED
}

/**
 * Master Game Loop Engine for Heimdall: AetherLoop.
 *
 * Coordinates track mathematics, granular particle physics, vessel queues,
 * level progression, victory/defeat triggers, and ASMR audio.
 */
class GameLoopEngine(
    val soundSynthesizer: SoundSynthesizer,
    val hapticManager: HapticManager
) {
    val track = BifrostTrack()
    val particleEngine = ParticleEngine(
        soundSynthesizer = soundSynthesizer,
        hapticManager = hapticManager
    )

    private val vesselIdGenerator = AtomicLong(1L)

    var currentLevel: RealmLevel = RealmLevel.ALL_LEVELS.first()
        private set

    val activeVessels = mutableListOf<Vessel>()
    val conduits = mutableListOf<Conduit>()
    val vesselQueue = mutableListOf<AetherColor>()

    var heldVesselColor: AetherColor? = null
        private set

    var isWarpSpeed: Boolean = false
    var totalHarvested: Int = 0
        private set
    var spillCount: Int = 0
        private set

    private val _gameState = MutableStateFlow(GameState.READY)
    val gameState: StateFlow<GameState> = _gameState.asStateFlow()

    private val _levelIndex = MutableStateFlow(0)
    val levelIndex: StateFlow<Int> = _levelIndex.asStateFlow()

    init {
        particleEngine.onVesselCompleted = { vessel ->
            // Vessel absorbed max energy!
            // It will trigger glowing aura and fly/dock into Asgard constellation
            totalHarvested += vessel.capacity
            checkWinCondition()
        }

        particleEngine.onParticleSpilled = {
            spillCount++
            if (spillCount >= currentLevel.maxSpillAllowed && _gameState.value == GameState.PLAYING) {
                // If spill tolerance is exceeded, level can either overload or provide warning
                // For relaxing playability, we flag high-spill or void alert
            }
        }

        loadLevel(0)
    }

    /**
     * Initializes a realm level.
     */
    fun loadLevel(index: Int) {
        val safeIndex = index.coerceIn(0, RealmLevel.ALL_LEVELS.lastIndex)
        _levelIndex.value = safeIndex
        currentLevel = RealmLevel.ALL_LEVELS[safeIndex]

        activeVessels.clear()
        conduits.clear()
        vesselQueue.clear()
        vesselQueue.addAll(currentLevel.availableVesselQueue)
        heldVesselColor = null
        isWarpSpeed = false
        totalHarvested = 0
        spillCount = 0

        // Set up conduits positioned evenly along top
        val colorCount = currentLevel.colors.size
        for (i in 0 until colorCount) {
            val normX = if (colorCount == 1) 0.5f else 0.22f + (i.toFloat() / (colorCount - 1).toFloat()) * 0.56f
            conduits.add(
                Conduit(
                    id = i,
                    color = currentLevel.colors[i],
                    xNorm = normX,
                    yNorm = 0.14f,
                    totalRequired = currentLevel.particlesPerConduit
                )
            )
        }

        _gameState.value = GameState.PLAYING
    }

    fun restartCurrentLevel() {
        loadLevel(_levelIndex.value)
    }

    fun nextLevel() {
        if (_levelIndex.value < RealmLevel.ALL_LEVELS.lastIndex) {
            loadLevel(_levelIndex.value + 1)
        } else {
            // Loop back to beginning or master loop
            loadLevel(0)
        }
    }

    /**
     * Dispatches the next vessel from the queue onto the track.
     */
    fun dispatchNextVessel(): Boolean {
        if (_gameState.value != GameState.PLAYING) return false
        if (vesselQueue.isEmpty()) {
            // Replenish from level pool if queue is depleted
            vesselQueue.addAll(currentLevel.availableVesselQueue)
        }

        val color = vesselQueue.removeAt(0)
        val newVessel = Vessel(
            id = vesselIdGenerator.incrementAndGet(),
            color = color,
            trackProgress = 0.65f, // Spawns on bottom straight or left vertical track
            capacity = 35
        )
        activeVessels.add(newVessel)
        soundSynthesizer.playLaunchWhoosh()
        return true
    }

    /**
     * Swaps the upcoming vessel in queue with the held slot.
     */
    fun swapHeldColor() {
        if (vesselQueue.isEmpty()) return
        val currentFront = vesselQueue.removeAt(0)
        if (heldVesselColor == null) {
            heldVesselColor = currentFront
        } else {
            val prevHeld = heldVesselColor!!
            heldVesselColor = currentFront
            vesselQueue.add(0, prevHeld)
        }
    }

    fun toggleWarpSpeed() {
        isWarpSpeed = !isWarpSpeed
    }

    /**
     * Ticks the game simulation by delta time (seconds).
     */
    fun update(dt: Float, width: Float, height: Float) {
        if (_gameState.value != GameState.PLAYING) return

        track.updateDimensions(width, height)

        val speedMultiplier = if (isWarpSpeed) 1.6f else 1.0f
        val effectiveSpeed = currentLevel.baseTrackSpeed * speedMultiplier

        // Update active vessels along the track
        val iterator = activeVessels.iterator()
        while (iterator.hasNext()) {
            val vessel = iterator.next()

            vessel.trackProgress = (vessel.trackProgress + effectiveSpeed * dt) % 1.0f
            vessel.liquidWaveOffset += dt * 5f

            if (vessel.fullFlashAnim > 0f) {
                vessel.fullFlashAnim = (vessel.fullFlashAnim - dt * 2f).coerceAtLeast(0f)
            }

            // If vessel is full and completed its victory lap back to launch zone, it docks
            if (vessel.isFull && vessel.state == VesselState.ON_TRACK && vessel.fullFlashAnim <= 0f) {
                // Dock into constellation matrix after completing lap
                if (vessel.trackProgress in 0.55f..0.65f) {
                    vessel.state = VesselState.DOCKING
                }
            }

            if (vessel.state == VesselState.DOCKING) {
                iterator.remove()
            }
        }

        // Update physics and particle emissions
        particleEngine.update(
            dt = dt,
            width = width,
            height = height,
            conduits = conduits,
            vessels = activeVessels,
            track = track,
            flowRatePerSec = currentLevel.flowRatePerSec
        )

        checkWinCondition()
    }

    private fun checkWinCondition() {
        if (_gameState.value != GameState.PLAYING) return

        // All conduits sealed
        val allConduitsDone = conduits.all { it.isHarvestComplete }
        if (allConduitsDone) {
            _gameState.value = GameState.REALM_STABILIZED
            soundSynthesizer.playRealmVictory()
            hapticManager.onRealmComplete()
        }
    }
}
