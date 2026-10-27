package com.heimdall.aetherloop.game.model

import androidx.compose.ui.graphics.Color

enum class BallType(val color: Color, val glowColor: Color, val isPiercing: Boolean) {
    NORMAL(Color(0xFF00E5FF), Color(0x6600E5FF), false),
    MJOLNIR_LIGHTNING(Color(0xFFFFD700), Color(0x88FFD700), true),
    SURTR_FIRE(Color(0xFFFF1744), Color(0x88FF1744), false)
}

/**
 * Rune Energy Ball (The Einherjar Orb).
 */
data class Ball(
    var x: Float,
    var y: Float,
    var vx: Float,
    var vy: Float,
    val radius: Float = 11f,
    var type: BallType = BallType.NORMAL,
    var isAttachedToPaddle: Boolean = true,
    var relativePaddleOffset: Float = 0f
) {
    val trailPoints = ArrayList<android.graphics.PointF>(8)

    fun updateTrail() {
        if (trailPoints.size >= 8) {
            trailPoints.removeAt(0)
        }
        trailPoints.add(android.graphics.PointF(x, y))
    }
}
