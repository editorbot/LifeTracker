package com.example.lifetracker.data.preferences

import android.content.Context

// data/preferences/UserPreferences.kt
class UserPreferences(context: Context) {

    private val prefs = context.getSharedPreferences(
        "lifetracker_prefs", Context.MODE_PRIVATE
    )

    // Each setting is a simple get/set pair
    var userName: String
        get() = prefs.getString("user_name", "User") ?: "User"
        set(value) = prefs.edit().putString("user_name", value).apply()

    var isDarkMode: Boolean
        get() = prefs.getBoolean("dark_mode", false)
        set(value) = prefs.edit().putBoolean("dark_mode", value).apply()

    var areNotificationsEnabled: Boolean
        get() = prefs.getBoolean("notifications", true)
        set(value) = prefs.edit().putBoolean("notifications", value).apply()
}