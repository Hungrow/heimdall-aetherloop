package com.heimdall.aetherloop.updater

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.core.content.FileProvider
import com.heimdall.aetherloop.core.HeimdallEcosystem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.util.concurrent.TimeUnit

/**
 * In-Device GitHub Releases OTA Updater.
 *
 * Allows users to check, download, and install application updates directly
 * from the official GitHub Releases repository on Android devices.
 */
class GitHubUpdateManager(private val context: Context) {

    private val client = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .build()

    private val _updateState = MutableStateFlow<UpdateState>(UpdateState.Idle)
    val updateState: StateFlow<UpdateState> = _updateState.asStateFlow()

    /**
     * Checks GitHub repository for the latest release.
     */
    suspend fun checkForUpdates(currentVersion: String = HeimdallEcosystem.VERSION_NAME) {
        _updateState.value = UpdateState.Checking
        withContext(Dispatchers.IO) {
            try {
                val request = Request.Builder()
                    .url(HeimdallEcosystem.GITHUB_API_URL)
                    .header("Accept", "application/vnd.github.v3+json")
                    .header("User-Agent", "Heimdall-AetherLoop-Updater")
                    .build()

                val response = client.newCall(request).execute()
                if (!response.isSuccessful) {
                    if (response.code == 404) {
                        _updateState.value = UpdateState.UpToDate(currentVersion)
                    } else {
                        _updateState.value = UpdateState.Error("Error de GitHub (${response.code})")
                    }
                    return@withContext
                }

                val bodyString = response.body?.string() ?: ""
                val json = JSONObject(bodyString)
                val release = GitHubRelease.fromJson(json)

                if (release.isNewerThan(currentVersion) && release.apkDownloadUrl != null) {
                    _updateState.value = UpdateState.Available(release)
                } else {
                    _updateState.value = UpdateState.UpToDate(currentVersion)
                }
            } catch (e: Exception) {
                _updateState.value = UpdateState.Error("Sin conexión al repositorio: ${e.localizedMessage ?: "Error de red"}")
            }
        }
    }

    /**
     * Automatically checks for updates on startup without showing error dialogs if offline.
     * If an update is available, sets state to Available so user can immediately update.
     */
    suspend fun checkForUpdatesSilent(currentVersion: String = HeimdallEcosystem.VERSION_NAME) {
        withContext(Dispatchers.IO) {
            try {
                val request = Request.Builder()
                    .url(HeimdallEcosystem.GITHUB_API_URL)
                    .header("Accept", "application/vnd.github.v3+json")
                    .header("User-Agent", "Heimdall-AetherLoop-Updater")
                    .build()

                val response = client.newCall(request).execute()
                if (response.isSuccessful) {
                    val bodyString = response.body?.string() ?: ""
                    val json = JSONObject(bodyString)
                    val release = GitHubRelease.fromJson(json)

                    if (release.isNewerThan(currentVersion) && release.apkDownloadUrl != null) {
                        _updateState.value = UpdateState.Available(release)
                    }
                }
            } catch (_: Throwable) {
                // Silent check ignores offline or transient errors
            }
        }
    }

    /**
     * Downloads the APK file from GitHub release assets with real-time progress.
     */
    suspend fun downloadUpdate(release: GitHubRelease) {
        val downloadUrl = release.apkDownloadUrl ?: run {
            _updateState.value = UpdateState.Error("Enlace de descarga no encontrado")
            return
        }

        withContext(Dispatchers.IO) {
            try {
                _updateState.value = UpdateState.Downloading(0f, 0f, 0f)

                val request = Request.Builder()
                    .url(downloadUrl)
                    .header("User-Agent", "Heimdall-AetherLoop-Updater")
                    .build()

                val response = client.newCall(request).execute()
                if (!response.isSuccessful) {
                    _updateState.value = UpdateState.Error("Fallo en la descarga (${response.code})")
                    return@withContext
                }

                val responseBody = response.body ?: run {
                    _updateState.value = UpdateState.Error("Respuesta vacía del servidor")
                    return@withContext
                }

                val totalBytes = responseBody.contentLength().takeIf { it > 0 } ?: release.apkSize
                val totalMb = totalBytes.toFloat() / (1024f * 1024f)

                // Save to internal cache dir for secure FileProvider sharing
                val updatesDir = File(context.cacheDir, "updates")
                if (!updatesDir.exists()) updatesDir.mkdirs()
                val targetFile = File(updatesDir, release.apkFileName)

                responseBody.byteStream().use { input ->
                    FileOutputStream(targetFile).use { output ->
                        val buffer = ByteArray(8 * 1024)
                        var bytesRead: Int
                        var downloadedBytes: Long = 0

                        while (input.read(buffer).also { bytesRead = it } != -1) {
                            output.write(buffer, 0, bytesRead)
                            downloadedBytes += bytesRead

                            val downloadedMb = downloadedBytes.toFloat() / (1024f * 1024f)
                            val progress = if (totalBytes > 0) {
                                (downloadedBytes.toFloat() / totalBytes.toFloat()).coerceIn(0f, 1f)
                            } else 0.5f

                            _updateState.value = UpdateState.Downloading(
                                progress = progress,
                                downloadedMb = downloadedMb,
                                totalMb = totalMb
                            )
                        }
                        output.flush()
                    }
                }

                _updateState.value = UpdateState.ReadyToInstall(release, targetFile)
            } catch (e: Exception) {
                _updateState.value = UpdateState.Error("Fallo al descargar la actualización: ${e.localizedMessage}")
            }
        }
    }

    /**
     * Prompts the user to install the downloaded APK.
     */
    fun installApk(file: File): Boolean {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                if (!context.packageManager.canRequestPackageInstalls()) {
                    val intent = Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES).apply {
                        data = Uri.parse("package:${context.packageName}")
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    }
                    context.startActivity(intent)
                    return false
                }
            }

            val contentUri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.provider",
                file
            )

            val installIntent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(contentUri, "application/vnd.android.package-archive")
                flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK
            }

            context.startActivity(installIntent)
            return true
        } catch (e: Exception) {
            _updateState.value = UpdateState.Error("No se pudo iniciar el instalador: ${e.localizedMessage}")
            return false
        }
    }

    fun dismiss() {
        _updateState.value = UpdateState.Idle
    }
}
