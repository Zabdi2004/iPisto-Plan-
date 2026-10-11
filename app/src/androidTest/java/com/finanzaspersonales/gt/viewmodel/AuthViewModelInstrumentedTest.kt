package com.finanzaspersonales.gt.viewmodel

import android.content.Context
import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.finanzaspersonales.gt.data.local.AppDatabase
import com.finanzaspersonales.gt.data.repository.UserRepository
import com.finanzaspersonales.gt.utils.SessionManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.util.UUID

@RunWith(AndroidJUnit4::class)
class AuthViewModelInstrumentedTest {
    private lateinit var context: Context
    private lateinit var databaseName: String
    private lateinit var database: AppDatabase
    private lateinit var session: SessionManager
    private lateinit var viewModel: AuthViewModel

    @Before fun setUp() {
        context = InstrumentationRegistry.getInstrumentation().targetContext
        databaseName = "ipisto-auth-test-${UUID.randomUUID()}"
        database = Room.databaseBuilder(context, AppDatabase::class.java, databaseName).build()
        session = SessionManager(InstrumentationRegistry.getInstrumentation().context)
        session.logout()
        viewModel = AuthViewModel(UserRepository(database), session)
    }

    @After fun tearDown() {
        session.logout()
        database.close()
        context.deleteDatabase(databaseName)
    }

    @Test fun registrationLoginDuplicateRejectionAndSessionRestoration() = runBlocking {
        viewModel.register("ipisto_test", "secreto123", "secreto123")
        assertTrue(awaitState { it is AuthState.Success } is AuthState.Success)
        val userId = viewModel.currentUser.value?.id ?: error("El registro no restauró el usuario")
        assertEquals(userId, session.getCurrentUserId())

        viewModel.logout()
        assertNull(viewModel.currentUser.value)
        assertNull(session.getCurrentUserId())

        viewModel.checkExistingSession(userId)
        assertTrue(awaitState { it == AuthState.Idle } == AuthState.Idle)
        assertEquals("ipisto_test", viewModel.currentUser.value?.nombreUsuario)
        assertFalse(viewModel.isUnlocked.value)
        viewModel.unlock("incorrecta")
        assertTrue(awaitState { it is AuthState.Error } is AuthState.Error)
        assertFalse(viewModel.isUnlocked.value)
        viewModel.unlock("secreto123")
        assertTrue(awaitState { it is AuthState.Success } is AuthState.Success)
        assertTrue(viewModel.isUnlocked.value)
        viewModel.lock()
        assertFalse(viewModel.isUnlocked.value)

        viewModel.logout()
        viewModel.login("ipisto_test", "incorrecta")
        assertTrue(awaitState { it is AuthState.Error } is AuthState.Error)
        assertNull(session.getCurrentUserId())

        viewModel.login("ipisto_test", "secreto123")
        assertTrue(awaitState { it is AuthState.Success } is AuthState.Success)
        assertTrue(viewModel.isUnlocked.value)
        assertEquals(userId, session.getCurrentUserId())

        viewModel.logout()
        viewModel.register("ipisto_test", "secreto123", "secreto123")
        val duplicate = awaitState { it is AuthState.Error }
        assertTrue((duplicate as AuthState.Error).message.contains("ya existe", ignoreCase = true))

        viewModel.resetAuthState()
        viewModel.register("", "secreto123", "secreto123")
        assertTrue(viewModel.authState.value is AuthState.Error)
        assertFalse(session.isLoggedIn())
    }

    @Test fun profileRenamePersistsAndUpdatesSession() = runBlocking {
        viewModel.register("perfil_original", "secreto123", "secreto123")
        assertTrue(awaitState { it is AuthState.Success } is AuthState.Success)
        val userId = viewModel.currentUser.value?.id ?: error("No se creó el usuario")

        viewModel.renameCurrentUser("perfil_nuevo")
        assertTrue(awaitState { it is AuthState.Success } is AuthState.Success)
        assertEquals("perfil_nuevo", viewModel.currentUser.value?.nombreUsuario)
        assertEquals("perfil_nuevo", session.getCurrentUsername())
        assertEquals("perfil_nuevo", UserRepository(database).getUserById(userId)?.nombreUsuario)

        viewModel.renameCurrentUser("x")
        assertTrue(viewModel.authState.value is AuthState.Error)
        assertEquals("perfil_nuevo", viewModel.currentUser.value?.nombreUsuario)
    }

    @Test fun passwordRecoveryFlowWithSecurityQuestion() = runBlocking {
        viewModel.register("recovery_user", "claveOriginal1", "claveOriginal1", "¿Nombre de mascota?", "Firulais")
        assertTrue(awaitState { it is AuthState.Success } is AuthState.Success)
        val userId = viewModel.currentUser.value?.id ?: error("Usuario no creado")

        viewModel.lock()
        assertFalse(viewModel.isUnlocked.value)

        viewModel.loadSecurityQuestion("recovery_user")
        withTimeout(5_000) {
            while (viewModel.recoveryQuestion.value == null) delay(25)
        }
        assertEquals("¿Nombre de mascota?", viewModel.recoveryQuestion.value)

        viewModel.recoverPassword("recovery_user", "RespuestaIncorrecta", "nuevaClave123", "nuevaClave123")
        val wrongAnswerState = awaitState { it is AuthState.Error }
        assertTrue((wrongAnswerState as AuthState.Error).message.contains("incorrecta", ignoreCase = true))

        viewModel.recoverPassword("recovery_user", "Firulais", "nuevaClave123", "otraClave")
        assertTrue(viewModel.authState.value is AuthState.Error)

        viewModel.recoverPassword("recovery_user", "  firulais  ", "nuevaClave123", "nuevaClave123")
        assertTrue(awaitState { it is AuthState.Success } is AuthState.Success)
        assertTrue(viewModel.isUnlocked.value)
        assertEquals(userId, session.getCurrentUserId())

        viewModel.logout()
        viewModel.login("recovery_user", "claveOriginal1")
        assertTrue(awaitState { it is AuthState.Error } is AuthState.Error)

        viewModel.login("recovery_user", "nuevaClave123")
        assertTrue(awaitState { it is AuthState.Success } is AuthState.Success)
        assertTrue(viewModel.isUnlocked.value)
    }

    @Test fun profileSecurityQuestionConfigurationAndUserWithoutRecovery() = runBlocking {
        viewModel.register("no_recovery_user", "clave123", "clave123")
        assertTrue(awaitState { it is AuthState.Success } is AuthState.Success)

        viewModel.logout()
        viewModel.loadSecurityQuestion("no_recovery_user")
        val errorState = awaitState { it is AuthState.Error }
        assertTrue((errorState as AuthState.Error).message.contains("no tiene", ignoreCase = true))

        viewModel.login("no_recovery_user", "clave123")
        assertTrue(awaitState { it is AuthState.Success } is AuthState.Success)

        viewModel.setSecurityQuestion("¿Ciudad de nacimiento?", "Antigua Guatemala")
        assertTrue(awaitState { it is AuthState.Success } is AuthState.Success)

        viewModel.logout()
        viewModel.loadSecurityQuestion("no_recovery_user")
        withTimeout(5_000) {
            while (viewModel.recoveryQuestion.value == null) delay(25)
        }
        assertEquals("¿Ciudad de nacimiento?", viewModel.recoveryQuestion.value)

        viewModel.recoverPassword("no_recovery_user", "antigua guatemala", "nuevaClave456", "nuevaClave456")
        assertTrue(awaitState { it is AuthState.Success } is AuthState.Success)
        assertTrue(viewModel.isUnlocked.value)
    }

    private suspend fun awaitState(predicate: (AuthState) -> Boolean): AuthState = withTimeout(30_000) {
        while (!predicate(viewModel.authState.value)) delay(25)
        viewModel.authState.value
    }
}
