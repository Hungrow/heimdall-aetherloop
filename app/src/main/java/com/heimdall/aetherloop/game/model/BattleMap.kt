package com.heimdall.aetherloop.game.model

import androidx.compose.ui.graphics.Color
import kotlin.math.PI
import kotlin.math.sin

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
        realmName = "Mar de Vigrid",
        subtitle = "Costas y Fiordos de Asgard",
        runeSymbol = "ᛊ",
        skyColors = listOf(Color(0xFF0D253A), Color(0xFF163E5C), Color(0xFF22587E)),
        waterColors = listOf(Color(0xFF0F324E), Color(0xFF13456B), Color(0xFF09253C)),
        waveFoamColor = Color(0x66E0F7FA),
        ambientParticleType = AmbientParticleType.SEA_SPRAY,
        ambientColor = Color(0x6000E5FF)
    ),
    NIFLHEIM_ICE(
        realmName = "Océano Helado de Niflheim",
        subtitle = "Mar Glacial de Icebergs y Escarcha",
        runeSymbol = "ᚺ",
        skyColors = listOf(Color(0xFF0E2235), Color(0xFF193B58), Color(0xFF27587F)),
        waterColors = listOf(Color(0xFF0E3852), Color(0xFF164E72), Color(0xFF0B2B41)),
        waveFoamColor = Color(0x77FFFFFF),
        ambientParticleType = AmbientParticleType.SNOWFLAKE,
        ambientColor = Color(0x70E0F7FA)
    ),
    MUSPELHEIM_MAGMA(
        realmName = "Mar de Fuego de Muspelheim",
        subtitle = "Tempestad Volcánica Oceánica",
        runeSymbol = "ᚲ",
        skyColors = listOf(Color(0xFF2C0F08), Color(0xFF4A180E), Color(0xFF6B2516)),
        waterColors = listOf(Color(0xFF33120A), Color(0xFF4E1D11), Color(0xFF220A06)),
        waveFoamColor = Color(0x77FFAB91),
        ambientParticleType = AmbientParticleType.FIRE_EMBER,
        ambientColor = Color(0x77FF6D00)
    ),
    HELHEIM_ABYSS(
        realmName = "Océano Espectral de Helheim",
        subtitle = "Aguas Brumosas de los Caídos",
        runeSymbol = "ᛗ",
        skyColors = listOf(Color(0xFF1A0F2B), Color(0xFF2D1B48), Color(0xFF432A69)),
        waterColors = listOf(Color(0xFF1F1235), Color(0xFF311C54), Color(0xFF150A24)),
        waveFoamColor = Color(0x66EA80FC),
        ambientParticleType = AmbientParticleType.SPIRIT_WISP,
        ambientColor = Color(0x66E040FB)
    ),
    BIFROST_ASTRAL(
        realmName = "Gran Mar del Norte",
        subtitle = "Tempestad Nórdica de los Dioses",
        runeSymbol = "ᚱ",
        skyColors = listOf(Color(0xFF0E1A33), Color(0xFF162A52), Color(0xFF233F78)),
        waterColors = listOf(Color(0xFF11264B), Color(0xFF183B75), Color(0xFF0B1934)),
        waveFoamColor = Color(0x77FFE082),
        ambientParticleType = AmbientParticleType.SEA_SPRAY,
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
 * Dynamic Ocean Swell wave that sweeps across the sea.
 *
 * When surging at peak crest (isBlocking = true), it acts as an ocean barrier
 * that intercepts and absorbs enemy cannonballs and player laser shots!
 */
data class OceanSwell(
    val id: Long,
    var y: Float,
    var height: Float = 34f,
    var vy: Float = 18f,
    var surgeTimer: Float = 0f,
    val period: Float = 16.0f, // Surges only once every 16 seconds
    var isBlocking: Boolean = false,
    var isWarning: Boolean = false,
    var crestAlpha: Float = 0f
) {
    fun update(dt: Float, screenHeight: Float) {
        surgeTimer += dt
        val phase = (surgeTimer % period) / period
        val surgeStrength = (sin(phase * 2.0 * PI).toFloat() + 1f) / 2f
        // Only blocks at the extreme peak of the surge (~1.1 seconds every 16s)
        isBlocking = surgeStrength > 0.88f
        isWarning = surgeStrength in 0.72f..0.88f
        crestAlpha = surgeStrength.coerceIn(0.15f, 1.0f)

        y += vy * dt
        val lowerLimit = screenHeight * 0.76f
        val upperLimit = screenHeight * 0.32f
        if (y > lowerLimit) {
            y = upperLimit
        }
    }
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
