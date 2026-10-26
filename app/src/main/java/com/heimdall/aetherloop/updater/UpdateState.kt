package com.heimdall.aetherloop.updater

import java.io.File

/**
 * Reactive states for the GitHub OTA Updater.
 */
sealed class UpdateState {
    data object Idle : UpdateState()
    data object Checking : UpdateState()
    data class UpToDate(val version: String) : UpdateState()
    data class Available(val release: GitHubRelease) : UpdateState()
    data class Downloading(
        val progress: Float,
        val downloadedMb: Float,
        val totalMb: Float
    ) : UpdateState()
    data class ReadyToInstall(val release: GitHubRelease, val apkFile: File) : UpdateState()
    data class Error(val message: String) : UpdateState()
}
