package com.heimdall.aetherloop.game.model

/**
 * Relic Power-Ups of the Heimdall Pantheon.
 *
 * Tactical abilities charged by harvesting cosmic aether.
 */
enum class AetherPower(
    val id: String,
    val title: String,
    val runeSymbol: String,
    val chargeThreshold: Int,
    val activeDurationSec: Float,
    val description: String
) {
    CHRONO_STASIS(
        id = "chrono_stasis",
        title = "Dilatación Temporal",
        runeSymbol = "ᛞ", // Dagaz (Dawn/Time Transformation)
        chargeThreshold = 45,
        activeDurationSec = 5.0f,
        description = "Ralentiza el circuito gravitatorio al 40% para una sincronización milimétrica."
    ),
    GJALLARHORN_SURGE(
        id = "gjallarhorn_surge",
        title = "Oleada del Gjallarhorn",
        runeSymbol = "ᚦ", // Thurisaz (Sonic Force/Gatebreaker)
        chargeThreshold = 65,
        activeDurationSec = 4.0f,
        description = "Onda expansiva que atrae magnéticamente todo el polvo estelar a la vasija adecuada."
    )
}
