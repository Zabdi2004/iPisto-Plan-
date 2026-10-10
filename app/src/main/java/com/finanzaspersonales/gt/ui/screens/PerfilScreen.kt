package com.finanzaspersonales.gt.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.navigation.NavHostController
import com.finanzaspersonales.gt.data.local.entity.User
import com.finanzaspersonales.gt.ui.components.QuetzalMascot
import com.finanzaspersonales.gt.viewmodel.AuthState
import com.finanzaspersonales.gt.viewmodel.AuthViewModel
import com.finanzaspersonales.gt.viewmodel.FinanzasViewModel

@Composable
fun PerfilScreen(navController: NavHostController, authViewModel: AuthViewModel, financeViewModel: FinanzasViewModel? = null) {
    val currentUser: User? by authViewModel.currentUser.collectAsState()
    val authState by authViewModel.authState.collectAsState()
    val financeState = financeViewModel?.uiState?.collectAsState()?.value
    var showPasswordDialog by remember { mutableStateOf(false) }
    var showNameDialog by remember { mutableStateOf(false) }
    var editableName by remember { mutableStateOf("") }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var deleteRequested by remember { mutableStateOf(false) }
    var oldPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    LaunchedEffect(authState, currentUser, showPasswordDialog, showNameDialog, deleteRequested) {
        if (authState == AuthState.Success && showPasswordDialog) {
            showPasswordDialog = false
            oldPassword = ""
            newPassword = ""
            confirmPassword = ""
            authViewModel.resetAuthState()
        }
        if (authState == AuthState.Success && showNameDialog) {
            showNameDialog = false
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

    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Text("Tu perfil", style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.primary)
        currentUser?.let { user ->
            Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant), shape = MaterialTheme.shapes.large) {
                Row(Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                    Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(user.nombreUsuario, style = MaterialTheme.typography.headlineSmall)
                        Text("Cuenta local · ${java.text.DateFormat.getDateInstance().format(java.util.Date(user.fechaCreacion))}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        TextButton(onClick = { editableName = user.nombreUsuario; authViewModel.resetAuthState(); showNameDialog = true }) {
                            Text("Editar nombre")
                        }
                    }
                    QuetzalMascot(size = 62.dp)
                }
            }
            financeState?.let { state -> state.balance?.let { balance ->
                Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                    Column(Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text("Resumen guardado", style = MaterialTheme.typography.titleMedium)
                        ProfileMetric("Ingresos equivalentes al mes", financeViewModel.formatCurrency(balance.ingresosMensuales))
                        ProfileMetric("Gastos y pagos mensuales", financeViewModel.formatCurrency(balance.gastosFijosMensuales + balance.gastosVariablesMensuales + balance.pagosDeudaMensuales))
                        ProfileMetric("Registros", (state.ingresos.size + state.gastosFijos.size + state.gastosVariables.size + state.deudas.size).toString())
                    }
                }
            } }
            Text("Herramientas", style = MaterialTheme.typography.titleLarge)
            OutlinedButton(onClick = { navController.navigate("metas") }, modifier = Modifier.fillMaxWidth()) { Text("Plan de ahorros") }
            OutlinedButton(onClick = { navController.navigate("graficas") }, modifier = Modifier.fillMaxWidth()) { Text("Informes") }
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

    if (showNameDialog) AlertDialog(
        onDismissRequest = { if (authState != AuthState.Loading) { showNameDialog = false; authViewModel.resetAuthState() } },
        title = { Text("Editar nombre de usuario") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = editableName,
                    onValueChange = { editableName = it },
                    label = { Text("Nombre de usuario") },
                    singleLine = true
                )
                (authState as? AuthState.Error)?.let { Text(it.message, color = MaterialTheme.colorScheme.error) }
            }
        },
        confirmButton = {
            TextButton(enabled = authState != AuthState.Loading, onClick = { authViewModel.renameCurrentUser(editableName) }) {
                Text(if (authState == AuthState.Loading) "Guardando…" else "Guardar")
            }
        },
        dismissButton = { TextButton(onClick = { showNameDialog = false; authViewModel.resetAuthState() }) { Text("Cancelar") } }
    )

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

@Composable
private fun ProfileMetric(label: String, value: String) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
        Text(label, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.onSurface)
    }
}
