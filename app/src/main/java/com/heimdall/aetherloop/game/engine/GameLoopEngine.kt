package com.heimdall.aetherloop.game.engine

import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
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
 * Coordinates track mathematics, SandArt deconstruction, granular particle physics,
 * vessel queues, and level progression with Compose-reactive state collections.
 */
class GameLoopEngine(
    val soundSynthesizer: SoundSynthesizer,
    val hapticManager: HapticManager
) {
    val track = BifrostTrack()
    val sandMatrix = SandArtMatrix(cols = 26, rows = 20)
    val particleEngine = ParticleEngine(
        soundSynthesizer = soundSynthesizer,
        hapticManager = hapticManager
    )

    private val vesselIdGenerator = AtomicLong(1L)

    var currentLevel: RealmLevel = RealmLevel.ALL_LEVELS.first()
        private set

    // Compose Reactive Collections for instant 60 FPS UI synchronization
    val activeVessels = mutableStateListOf<Vessel>()
    val conduits = mutableStateListOf<Conduit>()
    val vesselQueue = mutableStateListOf<AetherColor>()

    val heldVesselColor = mutableStateOf<AetherColor?>(null)
    val isWarpSpeed = mutableStateOf(false)
    val clearedProgress = mutableFloatStateOf(0f)
    val totalHarvested = mutableIntStateOf(0)

    private val _gameState = MutableStateFlow(GameState.READY)
    val gameState: StateFlow<GameState> = _gameState.asStateFlow()

    private val _levelIndex = MutableStateFlow(0)
    val levelIndex: StateFlow<Int> = _levelIndex.asStateFlow()

    init {
        particleEngine.onVesselCompleted = { vessel ->
            totalHarvested.intValue += vessel.capacity
        }

        particleEngine.onArtworkCleared = {
            if (_gameState.value == GameState.PLAYING) {
                _gameState.value = GameState.REALM_STABILIZED
                soundSynthesizer.playRealmVictory()
                hapticManager.onRealmComplete()
            }
        }

        loadLevel(0)
    }

    /**
     * Initializes a realm level and generates the sand-art matrix.
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
        totalHarvested.intValue = 0
        clearedProgress.floatValue = 0f

        // 1. Load the Sand Art Matrix for this level
        sandMatrix.loadPattern(safeIndex)

        // 2. Build Conduits / Funnels beneath the Sand Art
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

        // 3. Populate Vessel Queue
        vesselQueue.addAll(currentLevel.availableVesselQueue)
        if (vesselQueue.size < 6) {
            vesselQueue.addAll(currentLevel.availableVesselQueue)
        }

        // 4. Pre-spawn 2 initial vessels on the track so the game is immediately in action!
        if (vesselQueue.isNotEmpty()) {
            val firstColor = vesselQueue.removeAt(0)
            activeVessels.add(
                Vessel(
                    id = vesselIdGenerator.incrementAndGet(),
                    color = firstColor,
                    trackProgress = 0.05f, // Arriving onto top straight
                    capacity = 30
                )
            )
        }
        if (vesselQueue.isNotEmpty()) {
            val secondColor = vesselQueue.removeAt(0)
            activeVessels.add(
                Vessel(
                    id = vesselIdGenerator.incrementAndGet(),
                    color = secondColor,
                    trackProgress = 0.60f, // Traveling around bottom
                    capacity = 30
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
            loadLevel(0)
        }
    }

    /**
     * Dispatches the next vessel from the queue onto the track.
     */
    fun dispatchNextVessel(): Boolean {
        if (_gameState.value != GameState.PLAYING) return false

        // Check if there's enough space near the launch zone
        val spawnProgress = 0.65f
        val isOccupied = activeVessels.any {
            kotlin.math.abs(it.trackProgress - spawnProgress) < 0.12f
        }
        if (isOccupied && activeVessels.size >= 5) {
            // Wait slightly so vessels don't overlap awkwardly
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
            capacity = 30
        )
        activeVessels.add(newVessel)
        soundSynthesizer.playLaunchWhoosh()
        return true
    }

    /**
     * Swaps the upcoming vessel with the held slot.
     */
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

    /**
     * Ticks the game simulation by delta time (seconds).
     */
    fun update(dt: Float, width: Float, height: Float) {
        if (_gameState.value != GameState.PLAYING) return

        track.updateDimensions(width, height)

        val speedMultiplier = if (isWarpSpeed.value) 1.65f else 1.0f
        val effectiveSpeed = currentLevel.baseTrackSpeed * speedMultiplier

        track.beltScrollOffset = (track.beltScrollOffset + effectiveSpeed * dt * 6f) % 1.0f

        // 1. Update Vessels
        val iterator = activeVessels.iterator()
        while (iterator.hasNext()) {
            val vessel = iterator.next()

            vessel.trackProgress = (vessel.trackProgress + effectiveSpeed * dt) % 1.0f
            vessel.liquidWaveOffset += dt * 6f

            if (vessel.fullFlashAnim > 0f) {
                vessel.fullFlashAnim = (vessel.fullFlashAnim - dt * 2.5f).coerceAtLeast(0f)
            }

            // Once a full vessel completes its lap back to the dock (left straight near 0.70f), it retires
            if (vessel.isFull && vessel.state == VesselState.ON_TRACK && vessel.fullFlashAnim <= 0f) {
                if (vessel.trackProgress in 0.68f..0.78f) {
                    vessel.state = VesselState.DOCKING
                }
            }

            if (vessel.state == VesselState.DOCKING) {
                iterator.remove()
                // Auto-replenish queue so the belt is always lively
                if (vesselQueue.size < 4) {
                    vesselQueue.addAll(currentLevel.availableVesselQueue)
                }
            }
        }

        // 2. Update Granular Particle Physics & Sand Matrix deconstruction
        particleEngine.update(
            dt = dt,
            width = width,
            height = height,
            sandMatrix = sandMatrix,
            conduits = conduits,
            vessels = activeVessels,
            track = track,
            flowRatePerSec = currentLevel.flowRatePerSec * 2.5f // Energetic flow
        )

        clearedProgress.floatValue = sandMatrix.clearedRatio
    }
}
