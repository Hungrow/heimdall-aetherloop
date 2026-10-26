package com.heimdall.aetherloop.game.model

/**
 * Celestial Conduit (Emitter / Spout).
 *
 * Churns and emits cosmic aether streams from realm rifts down toward the loop.
 */
data class Conduit(
    val id: Int,
    val color: AetherColor,
    val xNorm: Float,            // Normalized X coordinate across viewport (0.15 to 0.85)
    val yNorm: Float = 0.12f,    // Normalized Y coordinate of nozzle
    val totalRequired: Int = 40, // Total aether particles required to seal conduit
    var harvestedCount: Int = 0,
    var emittedCount: Int = 0,
    var emissionAccumulator: Float = 0f
) {
    val isHarvestComplete: Boolean
        get() = harvestedCount >= totalRequired

    val harvestProgress: Float
        get() = (harvestedCount.toFloat() / totalRequired.toFloat()).coerceIn(0f, 1f)
}
