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

    @Test fun securityAnswerVerificationIsNormalizedAndSalted() {
        val answer = "Mi Mascota Favorita"
        val normalized = answer.trim().lowercase()
        val hash1 = PasswordHasher.hashPassword(normalized)
        val hash2 = PasswordHasher.hashPassword(normalized)
        assertNotEquals(hash1, hash2)
        assertTrue(PasswordHasher.verifyPassword("mi mascota favorita", hash1))
        assertTrue(PasswordHasher.verifyPassword("  Mi Mascota Favorita  ".trim().lowercase(), hash2))
        assertFalse(PasswordHasher.verifyPassword("otra respuesta", hash1))
    }

    @Test fun migrationFromV2ToV3AddsSecurityColumnsWithoutDataLoss() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val dbName = "migration-test-${java.util.UUID.randomUUID()}"
        val helper = androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory()
            .create(androidx.sqlite.db.SupportSQLiteOpenHelper.Configuration.builder(context)
                .name(dbName)
                .callback(object : androidx.sqlite.db.SupportSQLiteOpenHelper.Callback(2) {
                    override fun onCreate(db: androidx.sqlite.db.SupportSQLiteDatabase) {
                        db.execSQL("CREATE TABLE IF NOT EXISTS User (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `nombreUsuario` TEXT NOT NULL, `passwordHash` TEXT NOT NULL, `fechaCreacion` INTEGER NOT NULL)")
                    }
                    override fun onUpgrade(db: androidx.sqlite.db.SupportSQLiteDatabase, oldVersion: Int, newVersion: Int) {}
                })
                .build())
        val db = helper.writableDatabase
        db.execSQL("INSERT INTO User (id, nombreUsuario, passwordHash, fechaCreacion) VALUES (1, 'migrado', 'hash123', 1000)")

        // Execute Room Migration 2 -> 3
        com.finanzaspersonales.gt.data.local.AppDatabase.MIGRATION_2_3.migrate(db)

        val cursor = db.query("SELECT id, nombreUsuario, passwordHash, fechaCreacion, preguntaSeguridad, respuestaSeguridadHash FROM User WHERE id = 1")
        assertTrue(cursor.moveToFirst())
        assertEquals(1L, cursor.getLong(0))
        assertEquals("migrado", cursor.getString(1))
        assertEquals("hash123", cursor.getString(2))
        assertEquals(1000L, cursor.getLong(3))
        assertNull(cursor.getString(4))
        assertNull(cursor.getString(5))
        cursor.close()
        db.close()
        context.deleteDatabase(dbName)
    }
}
