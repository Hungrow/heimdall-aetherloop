package com.heimdall.aetherloop.game.model

import androidx.compose.ui.graphics.Color
import com.heimdall.aetherloop.R
import kotlin.math.sin

enum class ShipType(
    val title: String,
    val maxHp: Int,
    val primaryColor: Color,
    val scoreValue: Int,
    val drawableResId: Int,
    val isBoss: Boolean = false
) {
    SCOUT("Karve Explorador", 1, Color(0xFF00E676), 80, R.drawable.ship_scout),
    ARMORED_DRAKKAR("Drakkar Acorazado", 2, Color(0xFFFFD700), 150, R.drawable.ship_drakkar),
    FROST_BRIG("Bergantín de Escarcha", 3, Color(0xFF00E5FF), 250, R.drawable.ship_frost),
    FIRE_DREADNOUGHT("Acorazado de Surtr", 4, Color(0xFFFF1744), 400, R.drawable.ship_fire),

    // Milestone Bosses every 25 levels
    BOSS_JORMUNGANDR("Jefe: Dromon Jörmungandr (Nvl 25)", 30, Color(0xFF00E676), 3000, R.drawable.ship_drakkar, isBoss = true),
    BOSS_SURTR("Jefe: Leviatán de Surtr (Nvl 50)", 50, Color(0xFFFF5722), 5000, R.drawable.ship_fire, isBoss = true),
    BOSS_NIDHOGGR("Jefe: Dragón Níðhöggr (Nvl 75)", 75, Color(0xFF00E5FF), 7500, R.drawable.ship_frost, isBoss = true),
    BOSS_NAGLFAR("Jefe Final: Naglfar Ragnarök (Nvl 100)", 100, Color(0xFFD500F9), 15000, R.drawable.ship_naglfar, isBoss = true)
}

/**
 * Invading Viking Longship.
 *
 * Features horizontal and vertical patrol movement, cannonball attacks,
 * destructible hit points, and sinking animations.
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
    val powerUpType: PowerUpType? = null,
    var isSinking: Boolean = false,
    var sinkProgress: Float = 0f,
    var tiltAngle: Float = 0f,
    var vx: Float = 0f,
    var descentSpeed: Float = 2.5f,
    var canShoot: Boolean = false,
    var cannonCooldown: Float = 3f
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

    fun hit(damage: Int = 1): Boolean {
        currentHp = (currentHp - damage).coerceAtLeast(0)
        hitFlashAnim = 1.0f
        if (currentHp <= 0 && !isSinking) {
            isSinking = true
            sinkProgress = 0.01f
            tiltAngle = if (Math.random() > 0.5) 18f else -18f
        }
        return isDestroyed
    }

    fun updateMovement(dt: Float, screenW: Float) {
        if (isSinking) return

        waveOscillation += dt * 2.2f
        x += (vx + sin(waveOscillation) * 25f) * dt
        y += descentSpeed * dt

        // Bounce horizontally off walls
        val halfW = width / 2f
        if (x - halfW < 12f) {
            x = 12f + halfW
            vx = -vx
        } else if (x + halfW > screenW - 12f) {
            x = screenW - 12f - halfW
            vx = -vx
        }
    }
}
