package com.heimdall.aetherloop.core

import android.content.Context
import android.content.SharedPreferences
import com.heimdall.aetherloop.R

data class AvatarProfile(
    val id: String,
    val name: String,
    val title: String,
    val drawableResId: Int
)

object AvatarCatalog {
    val ALL = listOf(
        AvatarProfile("avatar_ragnar", "Ragnar", "Jarl de Kattegat", R.drawable.avatar_ragnar),
        AvatarProfile("avatar_lagertha", "Lagertha", "Doncella Escudera", R.drawable.avatar_lagertha),
        AvatarProfile("avatar_heimdall", "Heimdall", "Guardián del Bifröst", R.drawable.avatar_heimdall),
        AvatarProfile("avatar_bjorn", "Björn", "Berserker de la Piel de Oso", R.drawable.avatar_bjorn),
        AvatarProfile("avatar_odin", "Odín", "Padre de Todo", R.drawable.avatar_odin),
        AvatarProfile("avatar_brunhild", "Brunhild", "Valquiria de la Aurora", R.drawable.avatar_brunhild)
    )

    fun find(id: String): AvatarProfile = ALL.firstOrNull { it.id == id } ?: ALL.first()
}

/**
 * Persistent Player Progress and High-Score Manager.
 */
class PlayerProgressManager(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("aetherloop_player_progress", Context.MODE_PRIVATE)

    var playerName: String
        get() = prefs.getString(KEY_PLAYER_NAME, "Jarl Ragnar") ?: "Jarl Ragnar"
        set(value) = prefs.edit().putString(KEY_PLAYER_NAME, value).apply()

    var avatarId: String
        get() = prefs.getString(KEY_AVATAR_ID, "avatar_ragnar") ?: "avatar_ragnar"
        set(value) = prefs.edit().putString(KEY_AVATAR_ID, value).apply()

    var currentCampaignLevel: Int
        get() = prefs.getInt(KEY_CURRENT_CAMPAIGN_LEVEL, 1)
        set(value) = prefs.edit().putInt(KEY_CURRENT_CAMPAIGN_LEVEL, value.coerceIn(1, 100)).apply()

    var highestCampaignLevel: Int
        get() = prefs.getInt(KEY_HIGHEST_CAMPAIGN_LEVEL, 1)
        set(value) {
            if (value > highestCampaignLevel) {
                prefs.edit().putInt(KEY_HIGHEST_CAMPAIGN_LEVEL, value.coerceIn(1, 100)).apply()
            }
        }

    var highestUnlockedRealm: Int
        get() = prefs.getInt(KEY_HIGHEST_UNLOCKED, 0)
        set(value) = prefs.edit().putInt(KEY_HIGHEST_UNLOCKED, value).apply()

    var totalLifetimeAether: Long
        get() = prefs.getLong(KEY_LIFETIME_AETHER, 0L)
        set(value) = prefs.edit().putLong(KEY_LIFETIME_AETHER, value).apply()

    var endlessHighScore: Int
        get() = prefs.getInt(KEY_ENDLESS_HIGH_SCORE, 0)
        set(value) {
            if (value > endlessHighScore) {
                prefs.edit().putInt(KEY_ENDLESS_HIGH_SCORE, value).apply()
            }
        }

    var endlessHighestWave: Int
        get() = prefs.getInt(KEY_ENDLESS_HIGHEST_WAVE, 1)
        set(value) {
            if (value > endlessHighestWave) {
                prefs.edit().putInt(KEY_ENDLESS_HIGHEST_WAVE, value).apply()
            }
        }

    fun getLevelStars(level: Int): Int {
        return prefs.getInt(KEY_LEVEL_STARS_PREFIX + level, 0)
    }

    fun setLevelStars(level: Int, stars: Int) {
        val current = getLevelStars(level)
        if (stars > current) {
            prefs.edit().putInt(KEY_LEVEL_STARS_PREFIX + level, stars).apply()
        }
    }

    fun getLevelHighScore(level: Int): Int {
        return prefs.getInt(KEY_LEVEL_SCORE_PREFIX + level, 0)
    }

    fun setLevelHighScore(level: Int, score: Int): Boolean {
        val current = getLevelHighScore(level)
        if (score > current) {
            prefs.edit().putInt(KEY_LEVEL_SCORE_PREFIX + level, score).apply()
            return true
        }
        return false
    }

    fun getRealmStars(realmIndex: Int): Int = getLevelStars(realmIndex + 1)
    fun setRealmStars(realmIndex: Int, stars: Int) = setLevelStars(realmIndex + 1, stars)
    fun getRealmHighScore(realmIndex: Int): Int = getLevelHighScore(realmIndex + 1)
    fun setRealmHighScore(realmIndex: Int, score: Int): Boolean = setLevelHighScore(realmIndex + 1, score)

    fun isSoundEnabled(): Boolean {
        return prefs.getBoolean(KEY_SOUND_ENABLED, true)
    }

    fun setSoundEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_SOUND_ENABLED, enabled).apply()
    }

    fun isMusicEnabled(): Boolean {
        return prefs.getBoolean(KEY_MUSIC_ENABLED, true)
    }

    fun setMusicEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_MUSIC_ENABLED, enabled).apply()
    }

    fun isHapticsEnabled(): Boolean {
        return prefs.getBoolean(KEY_HAPTICS_ENABLED, true)
    }

    fun setHapticsEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_HAPTICS_ENABLED, enabled).apply()
    }

    fun addHarvestedAether(amount: Int) {
        totalLifetimeAether += amount
    }

    companion object {
        private const val KEY_PLAYER_NAME = "player_name"
        private const val KEY_AVATAR_ID = "player_avatar_id"
        private const val KEY_CURRENT_CAMPAIGN_LEVEL = "current_campaign_level"
        private const val KEY_HIGHEST_CAMPAIGN_LEVEL = "highest_campaign_level"
        private const val KEY_ENDLESS_HIGH_SCORE = "endless_high_score"
        private const val KEY_ENDLESS_HIGHEST_WAVE = "endless_highest_wave"

        private const val KEY_HIGHEST_UNLOCKED = "highest_unlocked_realm"
        private const val KEY_LIFETIME_AETHER = "lifetime_aether"
        private const val KEY_LEVEL_STARS_PREFIX = "level_stars_"
        private const val KEY_LEVEL_SCORE_PREFIX = "level_score_"
        private const val KEY_SOUND_ENABLED = "pref_sound_enabled"
        private const val KEY_MUSIC_ENABLED = "pref_music_enabled"
        private const val KEY_HAPTICS_ENABLED = "pref_haptics_enabled"
    }
}
