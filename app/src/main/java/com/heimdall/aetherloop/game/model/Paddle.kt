package com.heimdall.aetherloop.game.model

import com.heimdall.aetherloop.R

/**
 * Player's Asgardian Flagship with Evolution Tiers.
 *
 * Upgrades when catching power-ups and downgrades in aspect upon taking enemy hits.
 */
data class Paddle(
    var centerX: Float = 500f,
    var y: Float = 1600f,
    var tier: Int = 2, // 1 to 4
    var baseWidth: Float = 210f,
    var width: Float = 210f,
    var height: Float = 28f,
    var hasBallistas: Boolean = false,
    var ballistaTimer: Float = 0f,
    var expandTimer: Float = 0f,
    var flashAnim: Float = 0f
) {
    val left: Float
        get() = centerX - width / 2f

    val right: Float
        get() = centerX + width / 2f

    val top: Float
        get() = y - height / 2f

    val bottom: Float
        get() = y + height / 2f

    val tierSpriteResId: Int
        get() = when (tier) {
            1 -> R.drawable.paddle_tier1
            2 -> R.drawable.paddle_tier2
            3 -> R.drawable.paddle_flagship
            else -> R.drawable.paddle_tier4
        }

    fun applyTierDimensions() {
        baseWidth = when (tier) {
            1 -> 160f
            2 -> 210f
            3 -> 260f
            else -> 310f
        }
        width = if (expandTimer > 0f) baseWidth * 1.35f else baseWidth
        if (tier >= 3) {
            hasBallistas = true
            ballistaTimer = Float.MAX_VALUE
        }
    }

    fun upgradeTier(): Boolean {
        flashAnim = 1.0f
        if (tier < 4) {
            tier++
            applyTierDimensions()
            return true
        }
        return false
    }

    fun downgradeTier(): Boolean {
        flashAnim = 1.0f
        if (tier > 1) {
            tier--
            applyTierDimensions()
            return true
        }
        return false // already lowest tier (loses life)
    }

    fun updateTimers(dt: Float) {
        if (flashAnim > 0f) {
            flashAnim = (flashAnim - dt * 3f).coerceAtLeast(0f)
        }

        if (hasBallistas && tier < 3) {
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

    fun activateExpansion(multiplier: Float = 1.35f, duration: Float = 10f) {
        width = baseWidth * multiplier
        expandTimer = duration
    }
}
