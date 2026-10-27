package com.heimdall.aetherloop.game.model

/**
 * Flaming cannonball projectile shot by enemy Viking warships downwards towards the player's flagship.
 */
data class CannonBall(
    var x: Float,
    var y: Float,
    var vx: Float = 0f,
    var vy: Float = 360f,
    val radius: Float = 9f,
    val damage: Int = 1
)
