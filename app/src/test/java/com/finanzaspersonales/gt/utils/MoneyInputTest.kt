package com.finanzaspersonales.gt.utils

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class MoneyInputTest {
    @Test fun keepsDigitsAndCaretWhileTyping() {
        val typed = "3500".fold("") { current, digit ->
            MoneyInput.normalizeEditingValue(current + digit, current.length + 1).text
        }
        assertEquals("3500", typed)

        val inserted = MoneyInput.normalizeEditingValue("35X00", 3)
        assertEquals("3500", inserted.text)
        assertEquals(2, inserted.selectionStart)
        assertEquals(2, inserted.selectionEnd)
    }

    @Test fun editingDoesNotReorderDigitsOrLoseCursorPosition() {
        assertEquals("0.50", MoneyInput.normalizeEditingValue("0.50", 4).text)
        assertEquals("125.75", MoneyInput.normalizeEditingValue("125.75", 6).text)
        val deletedMiddleDigit = "3500".removeRange(1, 2)
        assertEquals("300", MoneyInput.normalizeEditingValue(deletedMiddleDigit, 1).text)
        val replacedSelection = MoneyInput.normalizeEditingValue("2468", 2)
        assertEquals("2468", replacedSelection.text)
        assertEquals(2, replacedSelection.selectionStart)
        assertNull(MoneyInput.parseAmount("125.755"))
    }

    @Test fun acceptsDotAndCommaDecimalAndGroupedInput() {
        assertEquals(125.50, MoneyInput.parseAmount("125.50")!!, 0.0)
        assertEquals(125.50, MoneyInput.parseAmount("125,50")!!, 0.0)
        assertEquals(1234.56, MoneyInput.parseAmount("1,234.56")!!, 0.0)
        assertEquals(1250.50, MoneyInput.parseAmount("1,250.50")!!, 0.0)
        assertEquals(1234.56, MoneyInput.parseAmount("1.234,56")!!, 0.0)
    }

    @Test fun rejectsEmptyInvalidAndOverflowValues() {
        assertNull(MoneyInput.parseAmount(""))
        assertNull(MoneyInput.parseAmount("."))
        assertNull(MoneyInput.parseAmount("no válido"))
        assertNull(MoneyInput.parseAmount("9".repeat(400)))
        assertNull(MoneyInput.parseAmount("-25.00"))
        assertNull(MoneyInput.parseAmount("1000000000001"))
    }
}
