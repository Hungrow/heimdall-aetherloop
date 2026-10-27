package com.heimdall.aetherloop.game.model

import androidx.compose.ui.graphics.Color
import com.heimdall.aetherloop.R

enum class PowerUpType(
    val title: String,
    val runeSymbol: String,
    val color: Color,
    val drawableResId: Int
) {
    MJOLNIR("Rayo de Mjölnir", "ᚦ", Color(0xFFFFD700), R.drawable.rune_mjolnir),
    MULTIBALL("Trinidad de Bifröst", "ᛟ", Color(0xFF00E5FF), R.drawable.rune_multiball),
    SURTR_FIRE("Fuego de Surtr", "ᚲ", Color(0xFFFF1744), R.drawable.rune_fire),
    BALLISTAS("Balistas Gemelas", "ᛏ", Color(0xFFD500F9), R.drawable.rune_ballistas),
    SHIELD_EXPAND("Muro de Escudos", "ᛉ", Color(0xFF00E676), R.drawable.rune_shield),
    REPAIR_LIFE("Beso de Valquiria", "ᛒ", Color(0xFFFF4081), R.drawable.rune_valkyrie)
}

/**
 * Falling Power-Up Rune dropped when an enemy longship is destroyed.
 */
data class PowerUpDrop(
    var x: Float,
    var y: Float,
    var vy: Float = 220f,
    val type: PowerUpType,
    val radius: Float = 18f
)
