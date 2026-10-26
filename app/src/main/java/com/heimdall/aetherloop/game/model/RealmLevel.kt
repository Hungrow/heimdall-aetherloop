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
    val particlesPerConduit: Int = 35,
    val baseTrackSpeed: Float = 0.085f,
    val flowRatePerSec: Float = 4.8f,
    val availableVesselQueue: List<AetherColor>,
    val targetScore: Int = 1200
) {
    val isEndless: Boolean
        get() = levelNumber >= 8

    companion object {
        val ALL_LEVELS: List<RealmLevel> = listOf(
            // 1. IDAVOLL
            RealmLevel(
                levelNumber = 1,
                realmName = "Idavoll",
                subtitle = "Iniciación de la Forja Solar",
                lore = "Canaliza el éter dorado primigenio sobre la planicie de los dioses.",
                colors = listOf(AetherColor.SOLAR_GOLD),
                particlesPerConduit = 30,
                baseTrackSpeed = 0.08f,
                flowRatePerSec = 4.0f,
                availableVesselQueue = listOf(
                    AetherColor.SOLAR_GOLD,
                    AetherColor.SOLAR_GOLD,
                    AetherColor.SOLAR_GOLD,
                    AetherColor.SOLAR_GOLD
                ),
                targetScore = 800
            ),
            // 2. BIFRÖST
            RealmLevel(
                levelNumber = 2,
                realmName = "Bifröst",
                subtitle = "Sincronía del Arcoíris",
                lore = "Dos corrientes estelares fluyen a ritmos cruzados entre Asgard y Midgard.",
                colors = listOf(AetherColor.SOLAR_GOLD, AetherColor.ASTRAL_CYAN),
                particlesPerConduit = 32,
                baseTrackSpeed = 0.09f,
                flowRatePerSec = 4.6f,
                availableVesselQueue = listOf(
                    AetherColor.SOLAR_GOLD,
                    AetherColor.ASTRAL_CYAN,
                    AetherColor.SOLAR_GOLD,
                    AetherColor.ASTRAL_CYAN,
                    AetherColor.ASTRAL_CYAN,
                    AetherColor.SOLAR_GOLD
                ),
                targetScore = 1400
            ),
            // 3. ALFHEIM
            RealmLevel(
                levelNumber = 3,
                realmName = "Alfheim",
                subtitle = "Alas de Valquiria",
                lore = "Tres frecuencias armónicas tejen el velo de aurora de los elfos de luz.",
                colors = listOf(AetherColor.SOLAR_GOLD, AetherColor.ASTRAL_CYAN, AetherColor.AURORA_VIOLET),
                particlesPerConduit = 35,
                baseTrackSpeed = 0.095f,
                flowRatePerSec = 5.0f,
                availableVesselQueue = listOf(
                    AetherColor.AURORA_VIOLET,
                    AetherColor.SOLAR_GOLD,
                    AetherColor.ASTRAL_CYAN,
                    AetherColor.AURORA_VIOLET,
                    AetherColor.ASTRAL_CYAN,
                    AetherColor.SOLAR_GOLD
                ),
                targetScore = 2000
            ),
            // 4. NIDAVELLIR
            RealmLevel(
                levelNumber = 4,
                realmName = "Nidavellir",
                subtitle = "Engranajes Enanos",
                lore = "Mecanismos celestiales forjados por los hijos de Ivaldi en el corazón de la montaña.",
                colors = listOf(AetherColor.ASTRAL_CYAN, AetherColor.AURORA_VIOLET, AetherColor.SOLAR_GOLD),
                particlesPerConduit = 36,
                baseTrackSpeed = 0.10f,
                flowRatePerSec = 5.2f,
                availableVesselQueue = listOf(
                    AetherColor.ASTRAL_CYAN,
                    AetherColor.AURORA_VIOLET,
                    AetherColor.SOLAR_GOLD,
                    AetherColor.ASTRAL_CYAN,
                    AetherColor.SOLAR_GOLD,
                    AetherColor.AURORA_VIOLET
                ),
                targetScore = 2400
            ),
            // 5. MUSPELHEIM
            RealmLevel(
                levelNumber = 5,
                realmName = "Muspelheim",
                subtitle = "Crisol de Surtr",
                lore = "Torrentes de plasma incandescente rugen a máxima cadencia rítmica.",
                colors = listOf(
                    AetherColor.SOLAR_GOLD,
                    AetherColor.ASTRAL_CYAN,
                    AetherColor.AURORA_VIOLET,
                    AetherColor.CRIMSON_FLAME
                ),
                particlesPerConduit = 38,
                baseTrackSpeed = 0.108f,
                flowRatePerSec = 5.6f,
                availableVesselQueue = listOf(
                    AetherColor.CRIMSON_FLAME,
                    AetherColor.ASTRAL_CYAN,
                    AetherColor.SOLAR_GOLD,
                    AetherColor.AURORA_VIOLET,
                    AetherColor.CRIMSON_FLAME,
                    AetherColor.AURORA_VIOLET
                ),
                targetScore = 3000
            ),
            // 6. NIFLHEIM
            RealmLevel(
                levelNumber = 6,
                realmName = "Niflheim",
                subtitle = "Cristal del Frío Primordial",
                lore = "Niebla helada y escarcha estelar que exige precisión milimétrica.",
                colors = listOf(
                    AetherColor.ASTRAL_CYAN,
                    AetherColor.EMERALD_YGGDRASIL,
                    AetherColor.AURORA_VIOLET,
                    AetherColor.CRIMSON_FLAME
                ),
                particlesPerConduit = 40,
                baseTrackSpeed = 0.114f,
                flowRatePerSec = 5.8f,
                availableVesselQueue = listOf(
                    AetherColor.EMERALD_YGGDRASIL,
                    AetherColor.ASTRAL_CYAN,
                    AetherColor.AURORA_VIOLET,
                    AetherColor.CRIMSON_FLAME,
                    AetherColor.EMERALD_YGGDRASIL,
                    AetherColor.ASTRAL_CYAN
                ),
                targetScore = 3600
            ),
            // 7. YGGDRASIL
            RealmLevel(
                levelNumber = 7,
                realmName = "Yggdrasil",
                subtitle = "La Gran Convergencia",
                lore = "Las cinco ramas del Árbol Cósmico armonizadas en perfecta resonancia.",
                colors = listOf(
                    AetherColor.SOLAR_GOLD,
                    AetherColor.ASTRAL_CYAN,
                    AetherColor.AURORA_VIOLET,
                    AetherColor.CRIMSON_FLAME,
                    AetherColor.EMERALD_YGGDRASIL
                ),
                particlesPerConduit = 42,
                baseTrackSpeed = 0.12f,
                flowRatePerSec = 6.0f,
                availableVesselQueue = listOf(
                    AetherColor.EMERALD_YGGDRASIL,
                    AetherColor.AURORA_VIOLET,
                    AetherColor.ASTRAL_CYAN,
                    AetherColor.SOLAR_GOLD,
                    AetherColor.CRIMSON_FLAME,
                    AetherColor.EMERALD_YGGDRASIL
                ),
                targetScore = 4500
            ),
            // 8. NEXUS INFINITO (ENDLESS)
            RealmLevel(
                levelNumber = 8,
                realmName = "Nexus Infinito",
                subtitle = "Incursión del Vacío",
                lore = "Prueba de maestría sin fin. La velocidad escala con cada constelación sellada.",
                colors = listOf(
                    AetherColor.SOLAR_GOLD,
                    AetherColor.ASTRAL_CYAN,
                    AetherColor.AURORA_VIOLET,
                    AetherColor.CRIMSON_FLAME,
                    AetherColor.EMERALD_YGGDRASIL
                ),
                particlesPerConduit = 50,
                baseTrackSpeed = 0.125f,
                flowRatePerSec = 6.5f,
                availableVesselQueue = listOf(
                    AetherColor.SOLAR_GOLD,
                    AetherColor.ASTRAL_CYAN,
                    AetherColor.AURORA_VIOLET,
                    AetherColor.CRIMSON_FLAME,
                    AetherColor.EMERALD_YGGDRASIL
                ),
                targetScore = 10000
            )
        )
    }
}
