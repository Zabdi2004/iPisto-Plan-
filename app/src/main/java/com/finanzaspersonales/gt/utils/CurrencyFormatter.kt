package com.finanzaspersonales.gt.utils

import java.text.NumberFormat
import java.util.Locale

object CurrencyFormatter {
    private const val CURRENCY_SYMBOL = "Q"
    private val GUATEMALA = Locale("es", "GT")

    fun format(amount: Double): String {
        val formatter = NumberFormat.getNumberInstance(GUATEMALA).apply {
            minimumFractionDigits = 2
            maximumFractionDigits = 2
        }
        return "$CURRENCY_SYMBOL${formatter.format(amount)}"
    }

    fun formatCompact(amount: Double): String {
        return when {
            amount >= 1_000_000 -> String.format(GUATEMALA, "%s%.2fM", CURRENCY_SYMBOL, amount / 1_000_000)
            amount >= 1_000 -> String.format(GUATEMALA, "%s%.2fK", CURRENCY_SYMBOL, amount / 1_000)
            else -> format(amount)
        }
    }
}
