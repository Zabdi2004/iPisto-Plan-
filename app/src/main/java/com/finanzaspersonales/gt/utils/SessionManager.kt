package com.finanzaspersonales.gt.utils

import android.content.Context

class SessionManager(private val context: Context) {
    private companion object {
        const val PREFS_NAME = "session"
        const val KEY_USER_ID = "user_id"
        const val KEY_USERNAME = "username"
        const val KEY_LOGGED_IN = "logged_in"
    }

    private fun getPrefs() = context.applicationContext
        .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun saveSession(userId: Long, username: String) {
        getPrefs().edit().apply {
            putBoolean(KEY_LOGGED_IN, true)
            putLong(KEY_USER_ID, userId)
            putString(KEY_USERNAME, username)
            apply()
        }
    }

    fun getCurrentUserId(): Long? {
        if (!isLoggedIn()) return null
        return getPrefs().getLong(KEY_USER_ID, -1L).takeIf { it != -1L }
    }

    fun getCurrentUsername(): String? {
        if (!isLoggedIn()) return null
        return getPrefs().getString(KEY_USERNAME, null)
    }

    fun isLoggedIn(): Boolean = getPrefs().getBoolean(KEY_LOGGED_IN, false)

    fun logout() {
        getPrefs().edit().apply {
            putBoolean(KEY_LOGGED_IN, false)
            remove(KEY_USER_ID)
            remove(KEY_USERNAME)
            apply()
        }
    }

    fun clearSession() = logout()
}
