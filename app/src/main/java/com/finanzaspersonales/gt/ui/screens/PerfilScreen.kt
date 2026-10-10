package com.finanzaspersonales.gt.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.finanzaspersonales.gt.data.local.entity.User
import com.finanzaspersonales.gt.viewmodel.AuthState
import com.finanzaspersonales.gt.viewmodel.AuthViewModel

@Composable
fun PerfilScreen(navController: NavHostController, authViewModel: AuthViewModel) {
    val currentUser: User? by authViewModel.currentUser.collectAsState()
    val authState by authViewModel.authState.collectAsState()
    var showPasswordDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var deleteRequested by remember { mutableStateOf(false) }
    var oldPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    LaunchedEffect(authState, currentUser, showPasswordDialog, deleteRequested) {
        if (authState == AuthState.Success && showPasswordDialog) {
            showPasswordDialog = false
            oldPassword = ""
            newPassword = ""
            confirmPassword = ""
            authViewModel.resetAuthState()
        }
        if (deleteRequested && authState == AuthState.Idle && currentUser == null) {
            deleteRequested = false
            showDeleteDialog = false
            navController.navigate("welcome") { popUpTo(navController.graph.id) { inclusive = true }; launchSingleTop = true }
        }
        if (deleteRequested && authState is AuthState.Error) {
            deleteRequested = false
        }
    }

    Column(Modifier.fillMaxSize().padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("PERFIL", style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.primary)
        currentUser?.let { user ->
            Text("Usuario: ${user.nombreUsuario}", style = MaterialTheme.typography.titleLarge)
            Text("Cuenta creada: ${java.text.DateFormat.getDateInstance().format(java.util.Date(user.fechaCreacion))}")
            Button(onClick = { showPasswordDialog = true; authViewModel.resetAuthState() }, modifier = Modifier.fillMaxWidth()) {
                Text("Cambiar contraseña")
            }
            OutlinedButton(onClick = { navController.navigate("configuracion") }, modifier = Modifier.fillMaxWidth()) { Text("Idioma y configuración") }
            OutlinedButton(onClick = { navController.navigate("educacion_financiera") }, modifier = Modifier.fillMaxWidth()) { Text("Educación financiera") }
            Button(onClick = {
                authViewModel.logout()
                navController.navigate("login") { popUpTo(navController.graph.id) { inclusive = true }; launchSingleTop = true }
            }, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)) {
                Text("Cerrar sesión")
            }
            OutlinedButton(onClick = { showDeleteDialog = true }, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)) {
                Text("Eliminar cuenta y datos")
            }
        }
    }

    if (showPasswordDialog) AlertDialog(
        onDismissRequest = { if (authState != AuthState.Loading) showPasswordDialog = false },
        title = { Text("Cambiar contraseña") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(oldPassword, { oldPassword = it }, label = { Text("Contraseña actual") }, singleLine = true, visualTransformation = PasswordVisualTransformation())
                OutlinedTextField(newPassword, { newPassword = it }, label = { Text("Nueva contraseña") }, singleLine = true, visualTransformation = PasswordVisualTransformation())
                OutlinedTextField(confirmPassword, { confirmPassword = it }, label = { Text("Confirmar contraseña") }, singleLine = true, visualTransformation = PasswordVisualTransformation())
                (authState as? AuthState.Error)?.let { Text(it.message, color = MaterialTheme.colorScheme.error) }
            }
        },
        confirmButton = {
            TextButton(enabled = authState != AuthState.Loading, onClick = { authViewModel.changePassword(oldPassword, newPassword, confirmPassword) }) {
                Text(if (authState == AuthState.Loading) "Guardando…" else "Guardar")
            }
        },
        dismissButton = { TextButton(onClick = { showPasswordDialog = false }) { Text("Cancelar") } }
    )

    if (showDeleteDialog) AlertDialog(
        onDismissRequest = { showDeleteDialog = false },
        title = { Text("Eliminar cuenta") },
        text = {
            Column {
                Text("Se eliminarán tu cuenta y todos sus registros financieros. Esta acción no se puede deshacer.")
                (authState as? AuthState.Error)?.let { Text(it.message, color = MaterialTheme.colorScheme.error) }
            }
        },
        confirmButton = {
            TextButton(enabled = authState != AuthState.Loading, onClick = {
                currentUser?.let { deleteRequested = true; authViewModel.deleteUser(it.id) }
            }) { Text(if (authState == AuthState.Loading) "Eliminando…" else "Eliminar", color = MaterialTheme.colorScheme.error) }
        },
        dismissButton = { TextButton(onClick = { showDeleteDialog = false }) { Text("Cancelar") } }
    )
}
