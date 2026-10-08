package com.finanzaspersonales.gt.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.finanzaspersonales.gt.R
import com.finanzaspersonales.gt.viewmodel.AuthViewModel

@Composable
fun LoginScreen(
    navController: NavHostController,
    authViewModel: AuthViewModel
) {
    var username by remember { mutableStateOf(TextFieldValue("")) }
    var password by remember { mutableStateOf(TextFieldValue("")) }
    var passwordVisible by remember { mutableStateOf(false) }
    val authState by authViewModel.authState.collectAsState()

    LaunchedEffect(authState) {
        when (authState) {
            is AuthState.Success -> {
                navController.navigate("inicio") {
                    popUpTo("login") { inclusive = true }
                }
            }
            else -> {}
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterVertically,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "INICIAR SESIÓN",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Nombre de usuario") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
            )
        )
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Contraseña") },
            singleLine = true,
            visualTransformation = if (passwordVisible) PasswordVisualTransformation() else PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            trailingIcon = {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Text(
                        text = if (passwordVisible) "Ocultar" else "Mostrar",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
            )
        )

        if (authState is AuthState.Error) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = (authState as AuthState.Error).message,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                authViewModel.login(username.text, password.text)
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            enabled = authState !is AuthState.Loading,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            )
        ) {
            if (authState is AuthState.Loading) {
                ProgressBar()
            } else {
                Text("INICIAR SESIÓN", style = MaterialTheme.typography.labelLarge)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextButton(
            onClick = { navController.navigate("register") }
        ) {
            Text(
                text = "¿No tienes cuenta? Créala aquí",
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
fun ProgressBar() {
    CircularProgressIndicator(
        modifier = Modifier.size(24.dp),
        color = MaterialTheme.colorScheme.onPrimary,
        strokeWidth = 2.dp
    )
}
