package com.example.notisaver

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate

/**
 * Small helper that persists the user's chosen theme (Light / Dark / System)
 * and applies it app-wide via AppCompatDelegate.
 */
object ThemeManager {
    private const val PREFS_NAME = "settings"
    private const val KEY_THEME_MODE = "theme_mode"

    const val MODE_LIGHT = "light"
    const val MODE_DARK = "dark"
    const val MODE_SYSTEM = "system"

    fun getSavedMode(context: Context): String {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getString(KEY_THEME_MODE, MODE_SYSTEM) ?: MODE_SYSTEM
    }

    fun setMode(context: Context, mode: String) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(KEY_THEME_MODE, mode).apply()
        applyMode(mode)
    }

    /** Call this as early as possible (e.g. start of onCreate) in every Activity. */
    fun applySavedTheme(context: Context) {
        applyMode(getSavedMode(context))
    }

    private fun applyMode(mode: String) {
        val nightMode = when (mode) {
            MODE_LIGHT -> AppCompatDelegate.MODE_NIGHT_NO
            MODE_DARK -> AppCompatDelegate.MODE_NIGHT_YES
            else -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
        }
        AppCompatDelegate.setDefaultNightMode(nightMode)
    }
}
