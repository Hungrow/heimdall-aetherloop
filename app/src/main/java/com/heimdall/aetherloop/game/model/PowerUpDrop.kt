package com.heimdall.aetherloop.game.model

import androidx.compose.ui.graphics.Color

enum class PowerUpType(
    val title: String,
    val runeSymbol: String,
    val color: Color
) {
    MJOLNIR("Rayo de Mjölnir", "ᚦ", Color(0xFFFFD700)),
    MULTIBALL("Trinidad de Bifröst", "ᛗ", Color(0xFF00E5FF)),
    SURTR_FIRE("Fuego de Surtr", "ᚲ", Color(0xFFFF1744)),
    BALLISTAS("Balistas Gemelas", "ᚱ", Color(0xFFD500F9)),
    SHIELD_EXPAND("Muro de Escudos", "ᛒ", Color(0xFF00E676)),
    REPAIR_LIFE("Beso de Valquiria", "ᛉ", Color(0xFFFF4081))
}

/**
 * Falling Power-Up Rune dropped when an enemy longship is destroyed.
 */
data class PowerUpDrop(
    var x: Float,
    var y: Float,
    var vy: Float = 220f,
    val type: PowerUpType,
    val radius: Float = 16f
)
