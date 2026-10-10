package com.finanzaspersonales.gt.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.finanzaspersonales.gt.data.local.entity.User
import com.finanzaspersonales.gt.data.repository.UserRepository
import com.finanzaspersonales.gt.utils.PasswordHasher
import com.finanzaspersonales.gt.utils.SessionManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    object Success : AuthState()
    data class Error(val message: String) : AuthState()
}

class AuthViewModel(
    private val userRepository: UserRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    fun register(username: String, password: String, confirmPassword: String) {
        if (username.isBlank()) {
            _authState.value = AuthState.Error("El nombre de usuario no puede estar vacío")
            return
        }
        if (password != confirmPassword) {
            _authState.value = AuthState.Error("Las contraseñas no coinciden")
            return
        }
        if (password.length < 6) {
            _authState.value = AuthState.Error("La contraseña debe tener al menos 6 caracteres")
            return
        }
        if (!username.matches(Regex("^[a-zA-Z0-9_]+$"))) {
            _authState.value = AuthState.Error("El nombre de usuario solo puede contener letras, números y guiones bajos")
            return
        }

        _authState.value = AuthState.Loading
        viewModelScope.launch {
            val passwordHash = withContext(Dispatchers.Default) { PasswordHasher.hashPassword(password) }
            val user = User(
                nombreUsuario = username,
                passwordHash = passwordHash,
                fechaCreacion = System.currentTimeMillis()
            )
            val result = userRepository.registerUser(user)
            result.onSuccess { userId ->
                val registeredUser = userRepository.getUserById(userId)
                if (registeredUser != null) {
                    _currentUser.value = registeredUser
                    sessionManager.saveSession(registeredUser.id, registeredUser.nombreUsuario)
                }
                _authState.value = AuthState.Success
            }.onFailure { error ->
                _authState.value = AuthState.Error(error.message ?: "Error al registrar usuario")
            }
        }
    }

    fun login(username: String, password: String) {
        if (username.isBlank() || password.isBlank()) {
            _authState.value = AuthState.Error("Nombre de usuario o contraseña incorrectos")
            return
        }
        _authState.value = AuthState.Loading
        viewModelScope.launch {
            val result = userRepository.findByUsername(username)?.let { user ->
                val valid = withContext(Dispatchers.Default) { PasswordHasher.verifyPassword(password, user.passwordHash) }
                if (valid) {
                    val authenticatedUser = if (PasswordHasher.needsUpgrade(user.passwordHash)) {
                        val upgradedHash = withContext(Dispatchers.Default) { PasswordHasher.hashPassword(password) }
                        userRepository.updateUser(user.copy(passwordHash = upgradedHash))
                            .fold(onSuccess = { user.copy(passwordHash = upgradedHash) }, onFailure = { user })
                    } else user
                    Result.success(authenticatedUser)
                }
                else Result.failure(IllegalArgumentException("Credenciales incorrectas"))
            } ?: Result.failure(IllegalArgumentException("Credenciales incorrectas"))
            result.onSuccess { user ->
                _currentUser.value = user
                sessionManager.saveSession(user.id, user.nombreUsuario)
                _authState.value = AuthState.Success
            }.onFailure {
                _authState.value = AuthState.Error("Nombre de usuario o contraseña incorrectos")
            }
        }
    }

    fun checkExistingSession(userId: Long) {
        viewModelScope.launch {
            val user = userRepository.getUserById(userId)
            if (user != null) {
                _currentUser.value = user
                sessionManager.saveSession(user.id, user.nombreUsuario)
                _authState.value = AuthState.Success
            } else {
                sessionManager.logout()
                _currentUser.value = null
                _authState.value = AuthState.Idle
            }
        }
    }

    fun logout() {
        sessionManager.logout()
        _currentUser.value = null
        _authState.value = AuthState.Idle
    }

    fun renameCurrentUser(username: String) {
        val user = _currentUser.value ?: run {
            _authState.value = AuthState.Error("Inicia sesión para editar el perfil")
            return
        }
        val normalized = username.trim()
        if (!normalized.matches(Regex("^[a-zA-Z0-9_]{3,24}$"))) {
            _authState.value = AuthState.Error("Usa entre 3 y 24 letras, números o guiones bajos")
            return
        }
        _authState.value = AuthState.Loading
        viewModelScope.launch {
            userRepository.renameUser(user.id, normalized).fold(
                onSuccess = {
                    val updated = user.copy(nombreUsuario = normalized)
                    _currentUser.value = updated
                    sessionManager.saveSession(updated.id, updated.nombreUsuario)
                    _authState.value = AuthState.Success
                },
                onFailure = { _authState.value = AuthState.Error(it.localizedMessage ?: "No se pudo actualizar el nombre") }
            )
        }
    }

    fun changePassword(currentPassword: String, newPassword: String, confirmation: String) {
        val user = _currentUser.value ?: run {
            _authState.value = AuthState.Error("Inicia sesión para cambiar la contraseña")
            return
        }
        if (newPassword.length < 8) {
            _authState.value = AuthState.Error("La nueva contraseña debe tener al menos 8 caracteres")
            return
        }
        if (newPassword != confirmation) {
            _authState.value = AuthState.Error("Las contraseñas nuevas no coinciden")
            return
        }
        _authState.value = AuthState.Loading
        viewModelScope.launch {
            val valid = withContext(Dispatchers.Default) { PasswordHasher.verifyPassword(currentPassword, user.passwordHash) }
            if (!valid) {
                _authState.value = AuthState.Error("La contraseña actual es incorrecta")
                return@launch
            }
            val hash = withContext(Dispatchers.Default) { PasswordHasher.hashPassword(newPassword) }
            userRepository.updateUser(user.copy(passwordHash = hash)).fold(
                onSuccess = {
                    _currentUser.value = user.copy(passwordHash = hash)
                    _authState.value = AuthState.Success
                },
                onFailure = { _authState.value = AuthState.Error(it.message ?: "No se pudo cambiar la contraseña") }
            )
        }
    }

    fun deleteUser(userId: Long) {
        _authState.value = AuthState.Loading
        viewModelScope.launch {
            val result = userRepository.deleteUser(userId)
            result.onSuccess {
                sessionManager.logout()
                _currentUser.value = null
                _authState.value = AuthState.Idle
            }.onFailure { error ->
                _authState.value = AuthState.Error(error.message ?: "Error al eliminar cuenta")
            }
        }
    }

    fun resetAuthState() {
        _authState.value = AuthState.Idle
    }
}
