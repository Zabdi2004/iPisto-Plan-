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

    // The remembered session identifies the account; this process-only flag grants UI access.
    private val _isUnlocked = MutableStateFlow(false)
    val isUnlocked: StateFlow<Boolean> = _isUnlocked.asStateFlow()

    private val _recoveryQuestion = MutableStateFlow<String?>(null)
    val recoveryQuestion: StateFlow<String?> = _recoveryQuestion.asStateFlow()

    fun register(
        username: String,
        password: String,
        confirmPassword: String,
        securityQuestion: String = "",
        securityAnswer: String = ""
    ) {
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
        if (securityQuestion.isNotBlank() && securityAnswer.isBlank()) {
            _authState.value = AuthState.Error("Debes ingresar una respuesta de seguridad")
            return
        }
        if (securityQuestion.isBlank() && securityAnswer.isNotBlank()) {
            _authState.value = AuthState.Error("Debes seleccionar o escribir una pregunta de seguridad")
            return
        }

        _authState.value = AuthState.Loading
        viewModelScope.launch {
            val passwordHash = withContext(Dispatchers.Default) { PasswordHasher.hashPassword(password) }
            val answerHash = if (securityAnswer.isNotBlank()) {
                withContext(Dispatchers.Default) {
                    PasswordHasher.hashPassword(securityAnswer.trim().lowercase())
                }
            } else null
            val user = User(
                nombreUsuario = username,
                passwordHash = passwordHash,
                fechaCreacion = System.currentTimeMillis(),
                preguntaSeguridad = securityQuestion.trim().takeIf { it.isNotBlank() },
                respuestaSeguridadHash = answerHash
            )
            val result = userRepository.registerUser(user)
            result.onSuccess { userId ->
                val registeredUser = userRepository.getUserById(userId)
                if (registeredUser != null) {
                    _currentUser.value = registeredUser
                    sessionManager.saveSession(registeredUser.id, registeredUser.nombreUsuario)
                    _isUnlocked.value = true
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
                _isUnlocked.value = true
                _authState.value = AuthState.Success
            }.onFailure {
                _authState.value = AuthState.Error("Nombre de usuario o contraseña incorrectos")
            }
        }
    }

    fun checkExistingSession(userId: Long) {
        _isUnlocked.value = false
        _authState.value = AuthState.Loading
        viewModelScope.launch {
            val user = runCatching { userRepository.getUserById(userId) }.getOrNull()
            if (user != null) {
                _currentUser.value = user
                sessionManager.saveSession(user.id, user.nombreUsuario)
                _authState.value = AuthState.Idle
            } else {
                sessionManager.logout()
                _currentUser.value = null
                _isUnlocked.value = false
                _authState.value = AuthState.Idle
            }
        }
    }

    fun unlock(password: String) {
        val user = _currentUser.value
        if (user == null || !sessionManager.isLoggedIn() || sessionManager.getCurrentUserId() != user.id) {
            _isUnlocked.value = false
            _authState.value = AuthState.Error("No hay una sesión válida para desbloquear")
            return
        }
        if (password.isBlank()) {
            _isUnlocked.value = false
            _authState.value = AuthState.Error("Escribe tu contraseña para continuar")
            return
        }

        _authState.value = AuthState.Loading
        viewModelScope.launch {
            val valid = runCatching {
                withContext(Dispatchers.Default) { PasswordHasher.verifyPassword(password, user.passwordHash) }
            }.getOrDefault(false)
            if (!valid) {
                _isUnlocked.value = false
                _authState.value = AuthState.Error("La contraseña es incorrecta")
                return@launch
            }

            val authenticatedUser = if (PasswordHasher.needsUpgrade(user.passwordHash)) {
                val upgradedHash = withContext(Dispatchers.Default) { PasswordHasher.hashPassword(password) }
                userRepository.updateUser(user.copy(passwordHash = upgradedHash))
                    .fold(onSuccess = { user.copy(passwordHash = upgradedHash) }, onFailure = { user })
            } else user
            _currentUser.value = authenticatedUser
            _isUnlocked.value = true
            _authState.value = AuthState.Success
        }
    }

    fun lock() {
        if (_currentUser.value != null && sessionManager.isLoggedIn()) {
            _isUnlocked.value = false
            _authState.value = AuthState.Idle
        }
    }

    fun logout() {
        sessionManager.logout()
        _currentUser.value = null
        _isUnlocked.value = false
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
                _isUnlocked.value = false
                _authState.value = AuthState.Idle
            }.onFailure { error ->
                _authState.value = AuthState.Error(error.message ?: "Error al eliminar cuenta")
            }
        }
    }

    fun loadSecurityQuestion(username: String) {
        val clean = username.trim()
        if (clean.isBlank()) {
            _authState.value = AuthState.Error("Ingresa tu nombre de usuario")
            return
        }
        _authState.value = AuthState.Loading
        viewModelScope.launch {
            val result = userRepository.getSecurityQuestion(clean)
            result.onSuccess { question ->
                _recoveryQuestion.value = question
                _authState.value = AuthState.Idle
            }.onFailure { error ->
                _recoveryQuestion.value = null
                _authState.value = AuthState.Error(error.message ?: "No se pudo obtener la pregunta de seguridad")
            }
        }
    }

    fun clearRecoveryQuestion() {
        _recoveryQuestion.value = null
    }

    fun recoverPassword(
        username: String,
        securityAnswer: String,
        newPassword: String,
        confirmPassword: String
    ) {
        val cleanUser = username.trim()
        if (cleanUser.isBlank()) {
            _authState.value = AuthState.Error("El nombre de usuario no puede estar vacío")
            return
        }
        if (securityAnswer.isBlank()) {
            _authState.value = AuthState.Error("Debes responder a la pregunta de seguridad")
            return
        }
        if (newPassword.length < 6) {
            _authState.value = AuthState.Error("La nueva contraseña debe tener al menos 6 caracteres")
            return
        }
        if (newPassword != confirmPassword) {
            _authState.value = AuthState.Error("Las nuevas contraseñas no coinciden")
            return
        }

        _authState.value = AuthState.Loading
        viewModelScope.launch {
            val newHash = withContext(Dispatchers.Default) { PasswordHasher.hashPassword(newPassword) }
            val result = userRepository.recoverPassword(cleanUser, securityAnswer, newHash)
            result.onSuccess { updatedUser ->
                _currentUser.value = updatedUser
                sessionManager.saveSession(updatedUser.id, updatedUser.nombreUsuario)
                _isUnlocked.value = true
                _authState.value = AuthState.Success
            }.onFailure { error ->
                _authState.value = AuthState.Error(error.message ?: "Error al recuperar la contraseña")
            }
        }
    }

    fun setSecurityQuestion(question: String, answer: String) {
        val user = _currentUser.value ?: run {
            _authState.value = AuthState.Error("Inicia sesión para configurar la pregunta de seguridad")
            return
        }
        if (question.isBlank()) {
            _authState.value = AuthState.Error("La pregunta no puede estar vacía")
            return
        }
        if (answer.isBlank()) {
            _authState.value = AuthState.Error("La respuesta no puede estar vacía")
            return
        }
        _authState.value = AuthState.Loading
        viewModelScope.launch {
            val answerHash = withContext(Dispatchers.Default) {
                PasswordHasher.hashPassword(answer.trim().lowercase())
            }
            val result = userRepository.updateSecurityQuestion(user.id, question.trim(), answerHash)
            result.onSuccess {
                _currentUser.value = user.copy(
                    preguntaSeguridad = question.trim(),
                    respuestaSeguridadHash = answerHash
                )
                _authState.value = AuthState.Success
            }.onFailure { error ->
                _authState.value = AuthState.Error(error.message ?: "No se pudo actualizar la pregunta de seguridad")
            }
        }
    }

    fun resetAuthState() {
        _authState.value = AuthState.Idle
    }
}
