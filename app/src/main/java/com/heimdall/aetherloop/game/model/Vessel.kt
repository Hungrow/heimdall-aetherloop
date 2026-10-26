package com.heimdall.aetherloop.game.model

/**
 * State of a Resonance Vessel.
 */
enum class VesselState {
    ON_TRACK,
    DOCKING,
    RETIRED
}

/**
 * Resonance Vessel (Pod/Bucket).
 *
 * Travels along the Bifröst loop to intercept and capture matching aether grains.
 */
data class Vessel(
    val id: Long,
    val color: AetherColor,
    var trackProgress: Float, // Normalized 0.0 to 1.0 along the track
    val capacity: Int = 40,
    var currentAmount: Int = 0,
    var state: VesselState = VesselState.ON_TRACK,
    var liquidWaveOffset: Float = 0f,
    var fullFlashAnim: Float = 0f
) {
    val isFull: Boolean
        get() = currentAmount >= capacity

    val fillRatio: Float
        get() = (currentAmount.toFloat() / capacity.toFloat()).coerceIn(0f, 1f)

    /**
     * Adds an absorbed particle grain into the vessel.
     * Returns true if this particle filled the vessel to 100%.
     */
    fun addParticle(): Boolean {
        if (!isFull) {
            currentAmount++
            if (isFull) {
                fullFlashAnim = 1.0f
                return true
            }
        }
        return false
    }
}
