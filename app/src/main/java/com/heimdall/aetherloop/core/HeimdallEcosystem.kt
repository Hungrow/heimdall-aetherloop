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
    const val VERSION_NAME = "1.1.0"
    const val VERSION_CODE = 3

    const val GITHUB_REPO_OWNER = "Hungrow"
    const val GITHUB_REPO_NAME = "heimdall-aetherloop"
    const val GITHUB_API_URL = "https://api.github.com/repos/$GITHUB_REPO_OWNER/$GITHUB_REPO_NAME/releases/latest"

    /**
     * Lore description for the Heimdall resonance mechanic.
     */
    const val LORE_SYNOPSIS =
        "As Heimdall, sentinel of Asgard, you oversee the celestial Bifrost Resonance Conduits. " +
        "Cosmic aether cascades from the World Tree through realm rifts. Dispatch and synchronize " +
        "rune-forged vessels along the gravitational loop to harvest matching celestial energy, " +
        "stabilize the Nine Realms, and seal the cosmic fractures."
}
