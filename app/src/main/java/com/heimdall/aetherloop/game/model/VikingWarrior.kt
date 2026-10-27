package com.heimdall.aetherloop.game.model

/**
 * Viking Einherjar Warrior launched into battle against enemy longships.
 *
 * Leaps from the player's flagship paddle, arcs towards enemy ships,
 * lands on deck to deal slashing damage (with axe slash visual effects),
 * and dives into the fjord with water splash.
 */
data class VikingWarrior(
    val id: Long,
    var x: Float,
    var y: Float,
    var vx: Float,
    var vy: Float,
    var rotation: Float = 0f,
    var scale: Float = 1f,
    var targetShipId: Long? = null,
    var isBoarding: Boolean = false,
    var boardingTimer: Float = 0f, // Time spent slashing on enemy deck
    var state: WarriorState = WarriorState.LEAPING
)

enum class WarriorState {
    LEAPING,
    SLASHING,
    DIVING,
    SPLASHED
}
