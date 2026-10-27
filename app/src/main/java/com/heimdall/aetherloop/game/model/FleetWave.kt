package com.heimdall.aetherloop.game.model

enum class GameMode {
    STORY_CAMPAIGN,
    ENDLESS_SURVIVAL
}

/**
 * Level & Fleet Wave Configuration for 100 Campaign Levels and Endless Mode.
 */
data class FleetWave(
    val waveNumber: Int,
    val title: String,
    val subtitle: String,
    val lore: String,
    val targetScore: Int = 2000,
    val isBossLevel: Boolean = false,
    val bossType: ShipType? = null
) {
    companion object {
        fun forLevel(level: Int): FleetWave {
            val lvl = level.coerceIn(1, 100)
            val isBoss = (lvl % 25 == 0)
            val bossType = when (lvl) {
                25 -> ShipType.BOSS_JORMUNGANDR
                50 -> ShipType.BOSS_SURTR
                75 -> ShipType.BOSS_NIDHOGGR
                100 -> ShipType.BOSS_NAGLFAR
                else -> null
            }

            val (chapterTitle, subtitle, lore) = when (lvl) {
                in 1..24 -> Triple(
                    "Incursión en el Fiordo de Vigrid • Nivel $lvl",
                    "Costas de Midgard",
                    "Drakkars rebeldes penetran las aguas bajas de Midgard. Desvía sus proyectiles y hunde sus exploradores."
                )
                25 -> Triple(
                    "⚔️ JEFE: EL DROMON JÖRMUNGANDR",
                    "Desembocadura del Mar de las Serpientes",
                    "¡La bestia de los mares despierta! El acorazado Jörmungandr abre fuego con sus baterías de cañones gemelos."
                )
                in 26..49 -> Triple(
                    "Estrecho Volcánico de Muspelheim • Nivel $lvl",
                    "El Océano de Fuego y Cenizas",
                    "Las aguas hierven con magma. Los acorazados de Surtr avanzan disparando bolas de cañón incandescentes."
                )
                50 -> Triple(
                    "⚔️ JEFE: EL LEVIATÁN DE SURTR",
                    "Fisura del Fuego Eterno",
                    "Surtr envía su buque fortaleza. Sus cañones de lava amenazan con calcinar el buque insignia de Heimdall."
                )
                in 51..74 -> Triple(
                    "Mar Helado de Niflheim • Nivel $lvl",
                    "Aguas de Escarcha y Sombras",
                    "Los témpanos flotantes complican la navegación mientras navíos congelados disparan ráfagas heladas."
                )
                75 -> Triple(
                    "⚔️ JEFE: EL DRAGÓN NÍÐHÖGGR",
                    "Las Raíces de Yggdrasil",
                    "El dragón que roe el Árbol del Mundo emerge como buque insignia acorazado con blindaje de escarcha."
                )
                in 76..99 -> Triple(
                    "El Asedio al Puente del Bifröst • Nivel $lvl",
                    "Umbral de Asgard",
                    "La armada completa de los Nueve Reinos converge en la última línea de defensa de los dioses."
                )
                100 -> Triple(
                    "⚔️ JEFE FINAL: NAGLFAR RAGNARÖK",
                    "El Fin de los Tiempos",
                    "¡El mítico barco de uñas y huesos zarpa para devorar Asgard! La batalla naval definitiva por el cosmos."
                )
                else -> Triple("Batalla de los Fiordos", "Mundo Desconocido", "El destino de Asgard se decide en las olas.")
            }

            return FleetWave(
                waveNumber = lvl,
                title = chapterTitle,
                subtitle = subtitle,
                lore = lore,
                targetScore = 1500 + lvl * 150,
                isBossLevel = isBoss,
                bossType = bossType
            )
        }

        fun forEndless(wave: Int): FleetWave {
            return FleetWave(
                waveNumber = wave,
                title = "Oleada Infinita $wave",
                subtitle = "Supervivencia del Valhalla",
                lore = "Oleadas ininterrumpidas de naves enemigas con velocidad y cadencia de fuego crecientes.",
                targetScore = 2000 * wave,
                isBossLevel = (wave % 5 == 0),
                bossType = if (wave % 5 == 0) ShipType.BOSS_NAGLFAR else null
            )
        }

        val ALL_WAVES = (1..5).map { forLevel(it) }
    }
}
