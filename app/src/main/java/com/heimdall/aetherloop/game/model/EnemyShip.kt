package com.heimdall.aetherloop.game.model

import androidx.compose.ui.graphics.Color

enum class ShipType(
    val title: String,
    val maxHp: Int,
    val primaryColor: Color,
    val scoreValue: Int
) {
    SCOUT("Karve Explorador", 1, Color(0xFF00E676), 80),
    ARMORED_DRAKKAR("Drakkar Acorazado", 2, Color(0xFFFFD700), 150),
    FROST_BRIG("Bergantín de Escarcha", 3, Color(0xFF00E5FF), 250),
    FIRE_DREADNOUGHT("Acorazado de Surtr", 4, Color(0xFFFF1744), 400),
    NAGLFAR_BOSS("Naglfar • Barco del Destino", 16, Color(0xFFD500F9), 2500)
}

/**
 * Invading Viking Longship.
 *
 * Positioned in naval battle formations across the fjord.
 * Features destructible hit points, wooden shields along the gunwale,
 * white flash on impact, and power-up drop chances.
 */
data class EnemyShip(
    val id: Long,
    var x: Float,
    var y: Float,
    val width: Float,
    val height: Float,
    val type: ShipType,
    var currentHp: Int = type.maxHp,
    var hitFlashAnim: Float = 0f,
    var waveOscillation: Float = 0f,
    val powerUpType: PowerUpType? = null
) {
    val isDestroyed: Boolean
        get() = currentHp <= 0

    val left: Float
        get() = x - width / 2f

    val right: Float
        get() = x + width / 2f

    val top: Float
        get() = y - height / 2f

    val bottom: Float
        get() = y + height / 2f

    /**
     * Applies damage to ship. Returns true if this hit sank the ship.
     */
    fun hit(damage: Int = 1): Boolean {
        currentHp = (currentHp - damage).coerceAtLeast(0)
        hitFlashAnim = 1.0f
        return isDestroyed
    }
}
