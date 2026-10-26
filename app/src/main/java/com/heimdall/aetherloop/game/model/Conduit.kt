package com.heimdall.aetherloop.game.model

/**
 * Celestial Conduit / Hopper Funnel.
 *
 * Chutes positioned directly beneath the Sand Art Mosaic that open and discharge
 * falling grains when a matching resonance vessel passes underneath.
 */
data class Conduit(
    val id: Int,
    val color: AetherColor,
    val colRangeStart: Int,
    val colRangeEnd: Int,
    val xNorm: Float,            // Normalized X coordinate across viewport
    val yNorm: Float = 0.48f,    // Normalized Y coordinate of funnel spout
    var isOpen: Boolean = false,
    var flowAccumulator: Float = 0f
)
