package com.finanzaspersonales.gt.utils

import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.Signature
import android.util.Base64
import java.security.MessageDigest
import java.util.UUID

class SessionManager(private val context: Context) {
    private val prefs = context.preferences
    private companion object {
        const val PREFS_NAME = "session"
        const val KEY_USER_ID = "user_id"
        const val KEY_USERNAME = "username"
        const val KEY_LOGGED_IN = "logged_in"
    }

    fun saveSession(userId: Long, username: String) {
        prefs.edit().apply {
            putBoolean(KEY_LOGGED_IN, true)
            putLong(KEY_USER_ID, userId)
            putString(KEY_USERNAME, username)
            apply()
        }
    }

    fun getCurrentUserId(): Long? {
        if (!isLoggedIn()) return null
        return prefs.getLong(KEY_USER_ID, -1L).takeIf { it != -1L }
    }

    fun getCurrentUsername(): String? {
        if (!isLoggedIn()) return null
        return prefs.getString(KEY_USERNAME, null)
    }

    fun isLoggedIn(): Boolean = prefs.getBoolean(KEY_LOGGED_IN, false)

    fun logout() {
        prefs.edit().apply {
            putBoolean(KEY_LOGGED_IN, false)
            remove(KEY_USER_ID)
            remove(KEY_USERNAME)
            apply()
        }
    }

    fun clearSession() = logout()
}
