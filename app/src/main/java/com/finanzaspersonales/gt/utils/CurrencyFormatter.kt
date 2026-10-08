package com.finanzaspersonales.gt.utils

import java.util.Locale

object CurrencyFormatter {
    private const val CURRENCY_SYMBOL = "Q"

    fun format(amount: Double): String {
        return String.format(Locale.US, "%s%,.2f", CURRENCY_SYMBOL, amount)
    }

    fun formatCompact(amount: Double): String {
        return when {
            amount >= 1_000_000 -> String.format(Locale.US, "%s%.2fM", CURRENCY_SYMBOL, amount / 1_000_000)
            amount >= 1_000 -> String.format(Locale.US, "%s%.2fK", CURRENCY_SYMBOL, amount / 1_000)
            else -> format(amount)
        }
    }
}
