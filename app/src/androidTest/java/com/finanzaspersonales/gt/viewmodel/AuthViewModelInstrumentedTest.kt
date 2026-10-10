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
        assertTrue(awaitState { it is AuthState.Success } is AuthState.Success)
        assertEquals("ipisto_test", viewModel.currentUser.value?.nombreUsuario)

        viewModel.logout()
        viewModel.login("ipisto_test", "incorrecta")
        assertTrue(awaitState { it is AuthState.Error } is AuthState.Error)
        assertNull(session.getCurrentUserId())

        viewModel.login("ipisto_test", "secreto123")
        assertTrue(awaitState { it is AuthState.Success } is AuthState.Success)
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

    private suspend fun awaitState(predicate: (AuthState) -> Boolean): AuthState = withTimeout(30_000) {
        while (!predicate(viewModel.authState.value)) delay(25)
        viewModel.authState.value
    }
}
