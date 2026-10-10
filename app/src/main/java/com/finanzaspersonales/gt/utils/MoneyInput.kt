package com.finanzaspersonales.gt.utils

import java.math.BigDecimal

/** Pure input helpers shared by every editable monetary field. Values are not grouped while typing. */
object MoneyInput {
    data class EditingValue(val text: String, val selectionStart: Int, val selectionEnd: Int)

    /** Keeps digits and one decimal separator, normalizes it to '.', and maps the caret to the new text. */
    fun normalizeEditingValue(source: String, selectionStart: Int, selectionEnd: Int = selectionStart): EditingValue {
        val hasDot = source.contains('.')
        val hasComma = source.contains(',')
        val decimal = when {
            hasDot && hasComma -> if (source.lastIndexOf('.') > source.lastIndexOf(',')) '.' else ','
            hasComma -> ','
            else -> '.'
        }
        val normalized = StringBuilder()
        var decimalAdded = false
        val safeStart = selectionStart.coerceIn(0, source.length)
        val safeEnd = selectionEnd.coerceIn(0, source.length)
        var mappedStart = 0
        var mappedEnd = 0
        source.forEachIndexed { index, char ->
            val accepted = when {
                char.isDigit() -> char
                (char == '.' || char == ',') && !decimalAdded && char == decimal -> {
                    decimalAdded = true
                    '.'
                }
                else -> null
            }
            if (accepted != null) normalized.append(accepted)
            if (index < safeStart && accepted != null) mappedStart++
            if (index < safeEnd && accepted != null) mappedEnd++
        }
        return EditingValue(
            normalized.toString(),
            mappedStart.coerceIn(0, normalized.length),
            mappedEnd.coerceIn(0, normalized.length)
        )
    }

    /** Parses an editable value without locale-dependent Double parsing. */
    fun parseAmount(text: String): Double? {
        val normalized = normalizeEditingValue(text, text.length).text
        if (normalized.isBlank() || normalized == ".") return null
        return runCatching { BigDecimal(normalized).toDouble().takeIf { it.isFinite() } }.getOrNull()
    }
}
