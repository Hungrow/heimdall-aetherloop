package com.heimdall.aetherloop.game.model

/**
 * Ballista projectile fired upward by player paddle.
 */
data class Projectile(
    var x: Float,
    var y: Float,
    var vy: Float = -1200f,
    val damage: Int = 1,
    var isActive: Boolean = true
)
