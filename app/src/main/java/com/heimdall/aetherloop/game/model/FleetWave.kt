package com.heimdall.aetherloop.game.model

/**
 * Level & Fleet Wave Configuration.
 */
data class FleetWave(
    val waveNumber: Int,
    val title: String,
    val subtitle: String,
    val lore: String,
    val targetScore: Int = 2000
) {
    companion object {
        val ALL_WAVES = listOf(
            FleetWave(
                waveNumber = 1,
                title = "Vanguardia de Incursión",
                subtitle = "Fiordos de Idavoll",
                lore = "Una flotilla de barcos ligeros Karve intenta infiltrar la desembocadura sagrada.",
                targetScore = 1500
            ),
            FleetWave(
                waveNumber = 2,
                title = "Muro de Escudos",
                subtitle = "Canal del Bifröst",
                lore = "Drakkars acorazados forman una barrera de madera de roble y escudos de bronce.",
                targetScore = 2800
            ),
            FleetWave(
                waveNumber = 3,
                title = "Incursión de Jötunheim",
                subtitle = "Aguas de Hielo de Niflheim",
                lore = "Bergantines esculpidos en escarcha pura navegan bajo la ventisca nórdica.",
                targetScore = 4200
            ),
            FleetWave(
                waveNumber = 4,
                title = "Armada de Fuego de Surtr",
                subtitle = "Estrecho de Muspelheim",
                lore = "Acorazados de madera negra y hierro fundido que bombardean las costas celestiales.",
                targetScore = 6000
            ),
            FleetWave(
                waveNumber = 5,
                title = "La Ira de Naglfar",
                subtitle = "Confrontación con el Barco del Destino",
                lore = "El legendario navío colosal navega hacia las puertas de Asgard escoltado por la flota del fin del mundo.",
                targetScore = 10000
            )
        )
    }
}
