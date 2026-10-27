package com.heimdall.aetherloop.game.model

/**
 * Player's Asgardian Flagship / Bifröst War Shield Paddle.
 */
data class Paddle(
    var centerX: Float = 500f,
    var y: Float = 1600f,
    var baseWidth: Float = 170f,
    var width: Float = 170f,
    var height: Float = 26f,
    var hasBallistas: Boolean = false,
    var ballistaTimer: Float = 0f,
    var expandTimer: Float = 0f
) {
    val left: Float
        get() = centerX - width / 2f

    val right: Float
        get() = centerX + width / 2f

    val top: Float
        get() = y - height / 2f

    val bottom: Float
        get() = y + height / 2f

    fun updateTimers(dt: Float) {
        if (hasBallistas) {
            ballistaTimer -= dt
            if (ballistaTimer <= 0f) {
                hasBallistas = false
            }
        }

        if (expandTimer > 0f) {
            expandTimer -= dt
            if (expandTimer <= 0f) {
                width = baseWidth
            }
        }
    }

    fun activateBallistas(duration: Float = 8.5f) {
        hasBallistas = true
        ballistaTimer = duration
    }

    fun activateExpansion(multiplier: Float = 1.45f, duration: Float = 10f) {
        width = baseWidth * multiplier
        expandTimer = duration
    }
}
