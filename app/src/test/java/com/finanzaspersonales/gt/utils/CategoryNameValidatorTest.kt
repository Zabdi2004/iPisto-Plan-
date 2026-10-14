package com.finanzaspersonales.gt.utils

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class CategoryNameValidatorTest {
    @Test fun rejectsBlankAndDuplicateNamesIgnoringCase() {
        assertEquals(CategoryNameValidator.Error.EMPTY, CategoryNameValidator.validate("  ", emptyList()))
        assertEquals(CategoryNameValidator.Error.DUPLICATE, CategoryNameValidator.validate("  ALQUILER ", listOf("Alquiler")))
        assertNull(CategoryNameValidator.validate("Mascotas", listOf("Alquiler", "Salud")))
    }
}
