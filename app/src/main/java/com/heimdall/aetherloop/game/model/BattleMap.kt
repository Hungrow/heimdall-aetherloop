package com.heimdall.aetherloop.game.model

import androidx.compose.ui.graphics.Color

enum class MapBiome(
    val realmName: String,
    val subtitle: String,
    val runeSymbol: String,
    val skyColors: List<Color>,
    val waterColors: List<Color>,
    val waveFoamColor: Color,
    val ambientParticleType: AmbientParticleType,
    val ambientColor: Color
) {
    VIGRID_FJORD(
        realmName = "Fiordo de Vigrid",
        subtitle = "Costas Sagradas de Asgard",
        runeSymbol = "ᛊ",
        skyColors = listOf(Color(0xFF030D1A), Color(0xFF071F36), Color(0xFF0C2E4E), Color(0xFF08223B)),
        waterColors = listOf(Color(0xFF071D33), Color(0xFF0A2B4C), Color(0xFF061A2D)),
        waveFoamColor = Color(0x3300E5FF),
        ambientParticleType = AmbientParticleType.SEA_SPRAY,
        ambientColor = Color(0x4000E5FF)
    ),
    NIFLHEIM_ICE(
        realmName = "Glaciar de Niflheim",
        subtitle = "Mar de Escarcha y Nieblas",
        runeSymbol = "ᚺ",
        skyColors = listOf(Color(0xFF030914), Color(0xFF0A1B2E), Color(0xFF132B45), Color(0xFF0E2238)),
        waterColors = listOf(Color(0xFF091F38), Color(0xFF12345A), Color(0xFF0B213D)),
        waveFoamColor = Color(0x44E0F7FA),
        ambientParticleType = AmbientParticleType.SNOWFLAKE,
        ambientColor = Color(0x60B2EBF2)
    ),
    MUSPELHEIM_MAGMA(
        realmName = "Abismo de Muspelheim",
        subtitle = "Océano de Fuego y Cenizas",
        runeSymbol = "ᚲ",
        skyColors = listOf(Color(0xFF140503), Color(0xFF2E0C06), Color(0xFF45140A), Color(0xFF260803)),
        waterColors = listOf(Color(0xFF1F0804), Color(0xFF380E08), Color(0xFF140502)),
        waveFoamColor = Color(0x44FF5722),
        ambientParticleType = AmbientParticleType.FIRE_EMBER,
        ambientColor = Color(0x66FF9800)
    ),
    HELHEIM_ABYSS(
        realmName = "Aguas del Helheim",
        subtitle = "Fiordo Sombrío de las Almas",
        runeSymbol = "ᛗ",
        skyColors = listOf(Color(0xFF090314), Color(0xFF1A0A2E), Color(0xFF2C1248), Color(0xFF150624)),
        waterColors = listOf(Color(0xFF130624), Color(0xFF240C40), Color(0xFF0D031A)),
        waveFoamColor = Color(0x33D500F9),
        ambientParticleType = AmbientParticleType.SPIRIT_WISP,
        ambientColor = Color(0x55E040FB)
    ),
    BIFROST_ASTRAL(
        realmName = "Vórtice del Bifröst",
        subtitle = "Circuito Cósmico Estelar",
        runeSymbol = "ᚱ",
        skyColors = listOf(Color(0xFF02040D), Color(0xFF060B21), Color(0xFF0E1438), Color(0xFF050817)),
        waterColors = listOf(Color(0xFF080F2B), Color(0xFF101E4D), Color(0xFF05091C)),
        waveFoamColor = Color(0x44FFD700),
        ambientParticleType = AmbientParticleType.STARDUST,
        ambientColor = Color(0x66FFD700)
    );

    companion object {
        fun forWave(waveIndex: Int): MapBiome {
            return when (waveIndex) {
                0 -> VIGRID_FJORD
                1 -> VIGRID_FJORD
                2 -> NIFLHEIM_ICE
                3 -> MUSPELHEIM_MAGMA
                4 -> HELHEIM_ABYSS
                else -> BIFROST_ASTRAL
            }
        }
    }
}

enum class AmbientParticleType {
    SEA_SPRAY,
    SNOWFLAKE,
    FIRE_EMBER,
    SPIRIT_WISP,
    STARDUST
}

/**
 * Ambient environmental particle (snow, ash, stardust, sea foam).
 */
data class AmbientParticle(
    var x: Float,
    var y: Float,
    var vx: Float,
    var vy: Float,
    var size: Float,
    var alpha: Float,
    val maxAlpha: Float,
    val type: AmbientParticleType,
    val color: Color
)

/**
 * Floating scenic hazard or terrain feature (iceberg, volcanic rock, ancient buoy).
 */
data class FloatingScenery(
    val id: Long,
    var x: Float,
    var y: Float,
    var width: Float,
    var height: Float,
    var vy: Float, // slow drift
    val type: SceneryType,
    var rotation: Float = 0f
)

enum class SceneryType {
    ICEBERG,
    VOLCANIC_ROCK,
    RUNIC_BUOY,
    GHOST_WRECK
}

/**
 * Water Ripple ring created when balls, projectiles, or sinking ships hit water.
 */
data class WaterRipple(
    val x: Float,
    val y: Float,
    var radius: Float = 4f,
    var maxRadius: Float = 45f,
    var alpha: Float = 0.8f,
    val color: Color = Color(0xFF00E5FF)
)
