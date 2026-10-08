package com.finanzaspersonales.gt.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.finanzaspersonales.gt.data.local.entity.User
import com.finanzaspersonales.gt.data.repository.UserRepository
import com.finanzaspersonales.gt.utils.PasswordHasher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    object Success : AuthState()
    data class Error(val message: String) : AuthState()
}

class AuthViewModel(
    private val userRepository: UserRepository
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
            val passwordHash = PasswordHasher.hashPassword(password)
            val user = User(
                nombreUsuario = username,
                passwordHash = passwordHash,
                fechaCreacion = System.currentTimeMillis()
            )
            val result = userRepository.registerUser(user)
            result.onSuccess { userId ->
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
            val passwordHash = PasswordHasher.hashPassword(password)
            val result = userRepository.authenticateUser(username, passwordHash)
            result.onSuccess { user ->
                _currentUser.value = user
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
                _authState.value = AuthState.Success
            } else {
                _authState.value = AuthState.Idle
            }
        }
    }

    fun logout() {
        _currentUser.value = null
        _authState.value = AuthState.Idle
    }

    fun deleteUser(userId: Long) {
        _authState.value = AuthState.Loading
        viewModelScope.launch {
            val result = userRepository.deleteUser(userId)
            result.onSuccess {
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
