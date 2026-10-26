package com.heimdall.aetherloop.game.model

/**
 * Realm Level Configuration.
 *
 * Defines the parameters, colors, conduit layout, loop speed, and objectives for each realm.
 */
data class RealmLevel(
    val levelNumber: Int,
    val realmName: String,
    val subtitle: String,
    val lore: String,
    val colors: List<AetherColor>,
    val particlesPerConduit: Int = 40,
    val baseTrackSpeed: Float = 0.09f, // Progress per second
    val flowRatePerSec: Float = 6.0f,  // Particles per second per conduit
    val availableVesselQueue: List<AetherColor>,
    val maxSpillAllowed: Int = 25
) {
    companion object {
        val ALL_LEVELS: List<RealmLevel> = listOf(
            RealmLevel(
                levelNumber = 1,
                realmName = "Idavoll",
                subtitle = "Iniciación de la Forja Solar",
                lore = "Alinea las vasijas solares para canalizar el éter dorado primigenio.",
                colors = listOf(AetherColor.SOLAR_GOLD),
                particlesPerConduit = 35,
                baseTrackSpeed = 0.08f,
                flowRatePerSec = 4.5f,
                availableVesselQueue = listOf(
                    AetherColor.SOLAR_GOLD,
                    AetherColor.SOLAR_GOLD,
                    AetherColor.SOLAR_GOLD
                ),
                maxSpillAllowed = 30
            ),
            RealmLevel(
                levelNumber = 2,
                realmName = "Bifröst",
                subtitle = "Sincronía del Puente del Arcoíris",
                lore = "Dos corrientes estelares fluyen a ritmos cruzados. Despacha con cadencia rítmica.",
                colors = listOf(AetherColor.SOLAR_GOLD, AetherColor.ASTRAL_CYAN),
                particlesPerConduit = 35,
                baseTrackSpeed = 0.09f,
                flowRatePerSec = 5.0f,
                availableVesselQueue = listOf(
                    AetherColor.SOLAR_GOLD,
                    AetherColor.ASTRAL_CYAN,
                    AetherColor.SOLAR_GOLD,
                    AetherColor.ASTRAL_CYAN,
                    AetherColor.ASTRAL_CYAN,
                    AetherColor.SOLAR_GOLD
                ),
                maxSpillAllowed = 35
            ),
            RealmLevel(
                levelNumber = 3,
                realmName = "Alfheim",
                subtitle = "Aurora de los Elfos de la Luz",
                lore = "El velo de aurora vibra en tres frecuencias. Coordina el orden de las vasijas.",
                colors = listOf(AetherColor.SOLAR_GOLD, AetherColor.ASTRAL_CYAN, AetherColor.AURORA_VIOLET),
                particlesPerConduit = 40,
                baseTrackSpeed = 0.095f,
                flowRatePerSec = 5.2f,
                availableVesselQueue = listOf(
                    AetherColor.AURORA_VIOLET,
                    AetherColor.SOLAR_GOLD,
                    AetherColor.ASTRAL_CYAN,
                    AetherColor.AURORA_VIOLET,
                    AetherColor.ASTRAL_CYAN,
                    AetherColor.SOLAR_GOLD,
                    AetherColor.AURORA_VIOLET
                ),
                maxSpillAllowed = 40
            ),
            RealmLevel(
                levelNumber = 4,
                realmName = "Muspelheim",
                subtitle = "Crisol de Llamas Cósmicas",
                lore = "Cuatro fuentes de plasma celestial. La velocidad del bucle se intensifica.",
                colors = listOf(
                    AetherColor.SOLAR_GOLD,
                    AetherColor.ASTRAL_CYAN,
                    AetherColor.AURORA_VIOLET,
                    AetherColor.CRIMSON_FLAME
                ),
                particlesPerConduit = 40,
                baseTrackSpeed = 0.105f,
                flowRatePerSec = 5.5f,
                availableVesselQueue = listOf(
                    AetherColor.CRIMSON_FLAME,
                    AetherColor.ASTRAL_CYAN,
                    AetherColor.SOLAR_GOLD,
                    AetherColor.AURORA_VIOLET,
                    AetherColor.CRIMSON_FLAME,
                    AetherColor.AURORA_VIOLET,
                    AetherColor.SOLAR_GOLD,
                    AetherColor.ASTRAL_CYAN
                ),
                maxSpillAllowed = 45
            ),
            RealmLevel(
                levelNumber = 5,
                realmName = "Yggdrasil",
                subtitle = "La Convergencia de los Nueve Mundos",
                lore = "Las cinco ramas del Árbol del Mundo canalizan su savia estelar en un frenesí armónico.",
                colors = listOf(
                    AetherColor.SOLAR_GOLD,
                    AetherColor.ASTRAL_CYAN,
                    AetherColor.AURORA_VIOLET,
                    AetherColor.CRIMSON_FLAME,
                    AetherColor.EMERALD_YGGDRASIL
                ),
                particlesPerConduit = 45,
                baseTrackSpeed = 0.115f,
                flowRatePerSec = 6.0f,
                availableVesselQueue = listOf(
                    AetherColor.EMERALD_YGGDRASIL,
                    AetherColor.AURORA_VIOLET,
                    AetherColor.ASTRAL_CYAN,
                    AetherColor.SOLAR_GOLD,
                    AetherColor.CRIMSON_FLAME,
                    AetherColor.EMERALD_YGGDRASIL,
                    AetherColor.AURORA_VIOLET,
                    AetherColor.ASTRAL_CYAN,
                    AetherColor.SOLAR_GOLD,
                    AetherColor.CRIMSON_FLAME
                ),
                maxSpillAllowed = 50
            )
        )
    }
}
