package com.finanzaspersonales.gt.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.finanzaspersonales.gt.ui.components.ProgressBar
import com.finanzaspersonales.gt.ui.components.QuetzalMascot
import com.finanzaspersonales.gt.viewmodel.AuthState
import com.finanzaspersonales.gt.viewmodel.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecuperarPasswordScreen(
    initialUsername: String = "",
    authViewModel: AuthViewModel,
    onBack: () -> Unit
) {
    var username by remember { mutableStateOf(initialUsername) }
    var securityAnswer by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var newPasswordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    val authState by authViewModel.authState.collectAsState()
    val recoveryQuestion by authViewModel.recoveryQuestion.collectAsState()
    val loading = authState is AuthState.Loading

    // If initialUsername is provided (e.g. from lock screen), automatically load the question
    LaunchedEffect(initialUsername) {
        if (initialUsername.isNotBlank() && recoveryQuestion == null) {
            authViewModel.loadSecurityQuestion(initialUsername)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Recuperar contraseña") },
                navigationIcon = {
                    IconButton(onClick = onBack, enabled = !loading) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .imePadding()
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            QuetzalMascot(size = 72.dp)

            Text(
                text = "Restablece tu acceso local",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground
            )

            // Step 1: Username
            OutlinedTextField(
                value = username,
                onValueChange = {
                    username = it
                    if (recoveryQuestion != null) {
                        authViewModel.clearRecoveryQuestion()
                    }
                },
                label = { Text("Nombre de usuario") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                enabled = !loading && initialUsername.isBlank(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                ),
                trailingIcon = {
                    if (initialUsername.isBlank()) {
                        IconButton(
                            onClick = { authViewModel.loadSecurityQuestion(username) },
                            enabled = !loading && username.isNotBlank()
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Search,
                                contentDescription = "Buscar pregunta"
                            )
                        }
                    }
                }
            )

            if (recoveryQuestion == null && initialUsername.isBlank()) {
                Button(
                    onClick = { authViewModel.loadSecurityQuestion(username) },
                    enabled = !loading && username.isNotBlank(),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (loading) {
                        ProgressBar()
                        Spacer(Modifier.width(8.dp))
                        Text("Buscando…")
                    } else {
                        Text("Verificar usuario")
                    }
                }
            }

            // Step 2: Security Question & Reset Password Form
            recoveryQuestion?.let { question ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Pregunta de seguridad:",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = question,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                OutlinedTextField(
                    value = securityAnswer,
                    onValueChange = { securityAnswer = it },
                    label = { Text("Tu respuesta") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !loading,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                )

                OutlinedTextField(
                    value = newPassword,
                    onValueChange = { newPassword = it },
                    label = { Text("Nueva contraseña") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !loading,
                    visualTransformation = if (newPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    supportingText = { Text("Mínimo 6 caracteres") },
                    trailingIcon = {
                        IconButton(onClick = { newPasswordVisible = !newPasswordVisible }, enabled = !loading) {
                            Icon(
                                imageVector = if (newPasswordVisible) Icons.Outlined.VisibilityOff else Icons.Outlined.Visibility,
                                contentDescription = if (newPasswordVisible) "Ocultar" else "Mostrar"
                            )
                        }
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                )

                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    label = { Text("Confirmar nueva contraseña") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !loading,
                    visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }, enabled = !loading) {
                            Icon(
                                imageVector = if (confirmPasswordVisible) Icons.Outlined.VisibilityOff else Icons.Outlined.Visibility,
                                contentDescription = if (confirmPasswordVisible) "Ocultar" else "Mostrar"
                            )
                        }
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                )

                Button(
                    onClick = {
                        authViewModel.recoverPassword(
                            username = username,
                            securityAnswer = securityAnswer,
                            newPassword = newPassword,
                            confirmPassword = confirmPassword
                        )
                    },
                    enabled = !loading && securityAnswer.isNotBlank() && newPassword.isNotBlank() && confirmPassword.isNotBlank(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp),
                    shape = MaterialTheme.shapes.large
                ) {
                    if (loading) {
                        ProgressBar()
                        Spacer(Modifier.width(12.dp))
                        Text("Restableciendo…")
                    } else {
                        Text("Restablecer contraseña")
                    }
                }
            }

            if (authState is AuthState.Error) {
                Text(
                    text = (authState as AuthState.Error).message,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}
