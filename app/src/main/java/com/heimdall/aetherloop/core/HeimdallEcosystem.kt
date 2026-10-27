package com.heimdall.aetherloop.core

/**
 * Heimdall Ecosystem Integration and Manifest.
 *
 * Heimdall: AetherLoop is a physics-based celestial particle sorting game
 * designed as an official module of the Heimdall Ecosystem.
 *
 * Provisional Name: "Heimdall: AetherLoop" (Module Code: BIFROST-RESONANCE-01)
 * Architecture: Clean, modular, reactive, self-updating via GitHub Releases.
 */
object HeimdallEcosystem {
    const val ECOSYSTEM_NAME = "Heimdall Universe"
    const val APP_NAME = "AetherLoop"
    const val MODULE_ID = "heimdall.ecosystem.aetherloop"
    const val VERSION_NAME = "2.1.0"
    const val VERSION_CODE = 6

    const val GITHUB_REPO_OWNER = "Hungrow"
    const val GITHUB_REPO_NAME = "heimdall-aetherloop"
    const val GITHUB_API_URL = "https://api.github.com/repos/$GITHUB_REPO_OWNER/$GITHUB_REPO_NAME/releases/latest"

    /**
     * Lore description for the Heimdall naval combat mechanic.
     */
    const val LORE_SYNOPSIS =
        "Hostile longship armadas cross the celestial fjords of the Nine Realms. " +
        "As Heimdall's guardian vanguard, command the Asgardian aegis paddle, " +
        "launch rune-charged orbs into the enemy battle lines, deflect devastating counter-fire, " +
        "and summon legendary powers — Mjölnir, Surtr's Flame, and Dual Ballistas — to crush Naglfar's fleet!"
}
