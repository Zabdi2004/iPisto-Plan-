package com.finanzaspersonales.gt.utils

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SecurityInstrumentedTest {
    @Test fun passwordHashesAreSaltedAndRejectWrongCredentials() {
        val first = PasswordHasher.hashPassword("secreto123")
        val second = PasswordHasher.hashPassword("secreto123")
        assertNotEquals(first, second)
        assertTrue(PasswordHasher.verifyPassword("secreto123", first))
        assertFalse(PasswordHasher.verifyPassword("incorrecta", first))
    }

    @Test fun sessionCanBeRestoredAndCleared() {
        val context = InstrumentationRegistry.getInstrumentation().context
        val session = SessionManager(context)
        session.logout()
        session.saveSession(42L, "usuario")

        val restoredSession = SessionManager(context)
        assertTrue(restoredSession.isLoggedIn())
        assertEquals(42L, restoredSession.getCurrentUserId())
        assertEquals("usuario", restoredSession.getCurrentUsername())
        restoredSession.logout()
        assertNull(SessionManager(context).getCurrentUserId())
    }
}
