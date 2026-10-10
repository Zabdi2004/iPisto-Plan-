package com.finanzaspersonales.gt.utils

import android.content.Context
import android.content.res.Configuration
import android.os.Build
import android.os.LocaleList
import java.util.Locale

object PreferencesHelper {
    private const val PREFS_NAME = "ipisto_prefs"
    private const val KEY_LANGUAGE = "language"
    private const val KEY_THEME = "theme"

    fun getPreferences(context: Context) =
        context.applicationContext
            .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun saveLanguage(context: Context, language: String) {
        getPreferences(context).edit().putString(KEY_LANGUAGE, language).apply()
    }

    fun getLanguage(context: Context): String =
        getPreferences(context).getString(KEY_LANGUAGE, "es") ?: "es"

    fun applyLanguage(context: Context, languageCode: String) {
        val locale = when (languageCode) {
            "en" -> Locale.ENGLISH
            "qu" -> Locale("qu")
            else -> Locale("es")
        }
        Locale.setDefault(locale)
        val config = Configuration(context.resources.configuration)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            config.setLocales(LocaleList(locale))
        } else {
            @Suppress("DEPRECATION")
            config.locale = locale
        }
        context.resources.updateConfiguration(config, context.resources.displayMetrics)
    }
}
