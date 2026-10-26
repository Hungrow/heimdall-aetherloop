package com.heimdall.aetherloop.game.model

import androidx.compose.ui.graphics.Color

/**
 * Celestial Aether Energy Types.
 *
 * Each color represents a primordial harmonic frequency from the Nine Realms.
 */
enum class AetherColor(
    val id: Int,
    val displayName: String,
    val runeSymbol: String,
    val color: Color,
    val glowColor: Color,
    val hexValue: Long
) {
    SOLAR_GOLD(
        id = 0,
        displayName = "Sol Dorado",
        runeSymbol = "ᛊ", // Sowilo (Sun)
        color = Color(0xFFFFD700),
        glowColor = Color(0x66FFD700),
        hexValue = 0xFFFFD700
    ),
    ASTRAL_CYAN(
        id = 1,
        displayName = "Cian Bifröst",
        runeSymbol = "ᚱ", // Raidho (Journey/Bifrost)
        color = Color(0xFF00E5FF),
        glowColor = Color(0x6600E5FF),
        hexValue = 0xFF00E5FF
    ),
    AURORA_VIOLET(
        id = 2,
        displayName = "Violeta Valquiria",
        runeSymbol = "ᛗ", // Mannaz (Divine Mind)
        color = Color(0xFFD500F9),
        glowColor = Color(0x66D500F9),
        hexValue = 0xFFD500F9
    ),
    CRIMSON_FLAME(
        id = 3,
        displayName = "Llama Muspel",
        runeSymbol = "ᚲ", // Kaunan (Torch/Flame)
        color = Color(0xFFFF1744),
        glowColor = Color(0x66FF1744),
        hexValue = 0xFFFF1744
    ),
    EMERALD_YGGDRASIL(
        id = 4,
        displayName = "Verde Yggdrasil",
        runeSymbol = "ᛒ", // Berkanan (Life/Growth)
        color = Color(0xFF00E676),
        glowColor = Color(0x6600E676),
        hexValue = 0xFF00E676
    );

    companion object {
        fun fromId(id: Int): AetherColor = entries.firstOrNull { it.id == id } ?: SOLAR_GOLD
    }
}
