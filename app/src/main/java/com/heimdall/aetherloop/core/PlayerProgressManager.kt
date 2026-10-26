package com.heimdall.aetherloop.core

import android.content.Context
import android.content.SharedPreferences

/**
 * Persistent Player Progress and High-Score Manager.
 *
 * Saves unlocked realms, 3-star ratings, high scores, total lifetime aether,
 * and audio/haptic preferences into SharedPreferences.
 */
class PlayerProgressManager(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("aetherloop_player_progress", Context.MODE_PRIVATE)

    var highestUnlockedRealm: Int
        get() = prefs.getInt(KEY_HIGHEST_UNLOCKED, 0)
        set(value) = prefs.edit().putInt(KEY_HIGHEST_UNLOCKED, value).apply()

    var totalLifetimeAether: Long
        get() = prefs.getLong(KEY_LIFETIME_AETHER, 0L)
        set(value) = prefs.edit().putLong(KEY_LIFETIME_AETHER, value).apply()

    fun getRealmStars(realmIndex: Int): Int {
        return prefs.getInt(KEY_STARS_PREFIX + realmIndex, 0)
    }

    fun setRealmStars(realmIndex: Int, stars: Int) {
        val current = getRealmStars(realmIndex)
        if (stars > current) {
            prefs.edit().putInt(KEY_STARS_PREFIX + realmIndex, stars).apply()
        }
    }

    fun getRealmHighScore(realmIndex: Int): Int {
        return prefs.getInt(KEY_SCORE_PREFIX + realmIndex, 0)
    }

    fun setRealmHighScore(realmIndex: Int, score: Int): Boolean {
        val current = getRealmHighScore(realmIndex)
        if (score > current) {
            prefs.edit().putInt(KEY_SCORE_PREFIX + realmIndex, score).apply()
            return true // New high score!
        }
        return false
    }

    fun isSoundEnabled(): Boolean {
        return prefs.getBoolean(KEY_SOUND_ENABLED, true)
    }

    fun setSoundEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_SOUND_ENABLED, enabled).apply()
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
        private const val KEY_HIGHEST_UNLOCKED = "highest_unlocked_realm"
        private const val KEY_LIFETIME_AETHER = "lifetime_aether"
        private const val KEY_STARS_PREFIX = "realm_stars_"
        private const val KEY_SCORE_PREFIX = "realm_score_"
        private const val KEY_SOUND_ENABLED = "pref_sound_enabled"
        private const val KEY_HAPTICS_ENABLED = "pref_haptics_enabled"
    }
}
