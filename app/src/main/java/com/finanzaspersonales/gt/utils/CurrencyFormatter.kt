package com.finanzaspersonales.gt.utils

import java.text.NumberFormat
import java.util.Locale

object CurrencyFormatter {
    @Volatile
    private var currencyCode: String = "gtq"

    fun setCurrency(code: String) {
        currencyCode = code.lowercase()
    }

    fun getCurrency(): String = currencyCode

    private fun symbol(): String = when (currencyCode) {
        "usd" -> "$"
        "eur" -> "€"
        else -> "Q"
    }

    private fun locale(): Locale = when (currencyCode) {
        "usd" -> Locale.US
        "eur" -> Locale("es", "ES")
        else -> Locale("es", "GT")
    }

    fun format(amount: Double): String {
        val formatter = NumberFormat.getNumberInstance(locale()).apply {
            minimumFractionDigits = 2
            maximumFractionDigits = 2
        }
        return "${symbol()}${formatter.format(amount)}"
    }

    fun formatCompact(amount: Double): String {
        return when {
            amount >= 1_000_000 -> String.format(locale(), "%s%.2fM", symbol(), amount / 1_000_000)
            amount >= 1_000 -> String.format(locale(), "%s%.2fK", symbol(), amount / 1_000)
            else -> format(amount)
        }
    }
}