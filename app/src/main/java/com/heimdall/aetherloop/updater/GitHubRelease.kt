package com.heimdall.aetherloop.updater

import org.json.JSONObject

/**
 * Data Model for a GitHub Release Asset and Metadata.
 */
data class GitHubRelease(
    val tagName: String,
    val title: String,
    val releaseNotes: String,
    val publishedAt: String,
    val apkDownloadUrl: String?,
    val apkFileName: String,
    val apkSize: Long
) {
    /**
     * Checks if this release tag is strictly newer than current installed version.
     * e.g., "v1.0.1" vs "1.0.0" -> true
     */
    fun isNewerThan(currentVersionName: String): Boolean {
        val cleanTag = tagName.removePrefix("v").trim()
        val cleanCurrent = currentVersionName.removePrefix("v").trim()

        val tagParts = cleanTag.split(".").mapNotNull { it.toIntOrNull() }
        val curParts = cleanCurrent.split(".").mapNotNull { it.toIntOrNull() }

        val maxLen = maxOf(tagParts.size, curParts.size)
        for (i in 0 until maxLen) {
            val tagPart = tagParts.getOrElse(i) { 0 }
            val curPart = curParts.getOrElse(i) { 0 }
            if (tagPart > curPart) return true
            if (tagPart < curPart) return false
        }
        return false
    }

    companion object {
        fun fromJson(json: JSONObject): GitHubRelease {
            val tagName = json.optString("tag_name", "v1.0.0")
            val title = json.optString("name", "Actualización $tagName")
            val releaseNotes = json.optString("body", "Mejoras de rendimiento y optimización del ecosistema Heimdall.")
            val publishedAt = json.optString("published_at", "")

            var downloadUrl: String? = null
            var fileName = "Heimdall-AetherLoop-$tagName.apk"
            var fileSize = 0L

            val assets = json.optJSONArray("assets")
            if (assets != null) {
                for (i in 0 until assets.length()) {
                    val asset = assets.getJSONObject(i)
                    val name = asset.optString("name", "")
                    if (name.endsWith(".apk", ignoreCase = true)) {
                        downloadUrl = asset.optString("browser_download_url")
                        fileName = name
                        fileSize = asset.optLong("size", 0L)
                        break
                    }
                }
            }

            return GitHubRelease(
                tagName = tagName,
                title = title,
                releaseNotes = releaseNotes,
                publishedAt = publishedAt,
                apkDownloadUrl = downloadUrl,
                apkFileName = fileName,
                apkSize = fileSize
            )
        }
    }
}
