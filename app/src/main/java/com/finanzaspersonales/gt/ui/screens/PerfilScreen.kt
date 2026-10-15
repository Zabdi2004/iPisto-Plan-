package com.finanzaspersonales.gt.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.finanzaspersonales.gt.data.local.entity.User
import com.finanzaspersonales.gt.ui.components.CircleIconButton
import com.finanzaspersonales.gt.ui.components.QuetzalMascot
import com.finanzaspersonales.gt.ui.theme.IpistoPalette
import com.finanzaspersonales.gt.viewmodel.AuthState
import com.finanzaspersonales.gt.viewmodel.AuthViewModel
import com.finanzaspersonales.gt.viewmodel.FinanzasUiState
import com.finanzaspersonales.gt.viewmodel.FinanzasViewModel

// Radios del diseño (sin definir colores nuevos).
private val ProfileTileShape = RoundedCornerShape(14.dp)
private val ProfileSheetShape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
private val ProfileContentMaxWidth = 390.dp

/**
 * Perfil con el diseño de dos zonas: un HEADER (avatar + datos + acciones) y una TARJETA inferior
 * con esquinas superiores redondeadas que se superpone al header.
 */
@Composable
fun PerfilScreen(
    navController: NavHostController,
    authViewModel: AuthViewModel,
    financeViewModel: FinanzasViewModel? = null
) {
    val currentUser: User? by authViewModel.currentUser.collectAsState()
    val authState by authViewModel.authState.collectAsState()
    val financeState: FinanzasUiState? = financeViewModel?.uiState?.collectAsState()?.value

    var showNameDialog by remember { mutableStateOf(false) }
    var editableName by remember { mutableStateOf("") }

    val onLogout: () -> Unit = {
        authViewModel.logout()
        navController.navigate("welcome") { popUpTo(navController.graph.id) { inclusive = true }; launchSingleTop = true }
    }
    val onEditName: () -> Unit = {
        editableName = currentUser?.nombreUsuario ?: ""
        authViewModel.resetAuthState()
        showNameDialog = true
    }

    LaunchedEffect(authState, currentUser, showNameDialog) {
        if (authState == AuthState.Success && showNameDialog) {
            showNameDialog = false
            authViewModel.resetAuthState()
        }
    }

    BoxWithConstraints(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {
        val headerMinHeight = maxHeight * 0.35f
        Column(
            modifier = Modifier
                .widthIn(max = ProfileContentMaxWidth)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            ProfileHeader(
                minHeight = headerMinHeight,
                user = currentUser,
                onSettings = { navController.navigate("configuracion") },
                onSecurity = { navController.navigate("seguridad") },
                onEditName = onEditName,
                onLogout = onLogout
            )
            ProfileContentCard(
                modifier = Modifier.offset(y = (-24).dp),
                financeState = financeState,
                financeViewModel = financeViewModel
            )
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
}

@Composable
private fun ProfileHeader(
    minHeight: Dp,
    user: User?,
    onSettings: () -> Unit,
    onSecurity: () -> Unit,
    onEditName: () -> Unit,
    onLogout: () -> Unit
) {
    Surface(
        color = MaterialTheme.colorScheme.surfaceVariant,
        modifier = Modifier.fillMaxWidth().heightIn(min = minHeight)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Barra superior mínima: tuerca (ajustes) + puerta (cerrar sesión).
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                CircleIconButton(
                    Icons.Filled.Settings, "Ajustes", onSettings,
                    size = 42.dp, iconSize = 20.dp,
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.width(10.dp))
                CircleIconButton(
                    Icons.AutoMirrored.Filled.Logout, "Cerrar sesión", onLogout,
                    size = 42.dp, iconSize = 20.dp,
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Avatar a la derecha; nombre y rol a su izquierda.
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(
                        text = user?.nombreUsuario ?: "Usuario",
                        fontSize = 26.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Cuenta local",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Normal,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Spacer(Modifier.width(12.dp))
                ProfileAvatar(size = 80.dp)
            }

            // Fila de acciones circulares: Seguridad y Editar nombre (con marco redondo visible).
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                CircleIconButton(
                    Icons.Filled.Security, "Seguridad", onSecurity,
                    size = 48.dp, iconSize = 24.dp,
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.primary
                )
                CircleIconButton(
                    Icons.Filled.Edit, "Editar nombre", onEditName,
                    size = 48.dp, iconSize = 24.dp,
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.primary
                )
            }

            // Línea de metadatos con iconos pequeños.
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Filled.DateRange,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(12.dp)
                )
                Spacer(Modifier.width(6.dp))
                Text(
                    text = user?.fechaCreacion?.let {
                        "Miembro desde " + java.text.DateFormat.getDateInstance().format(java.util.Date(it))
                    } ?: "Miembro del club iPisto",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Espacio extra entre la leyenda "Miembro desde…" y la tarjeta inferior.
            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
private fun ProfileAvatar(size: Dp) {
    Box(
        modifier = Modifier
            .size(size)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.surface),
        contentAlignment = Alignment.Center
    ) {
        QuetzalMascot(size = size * 0.78f)
    }
}

@Composable
private fun ProfileContentCard(
    modifier: Modifier,
    financeState: FinanzasUiState?,
    financeViewModel: FinanzasViewModel?
) {
    val balance = financeState?.balance
    val money: (Double) -> String = { financeViewModel?.formatCurrency(it) ?: "—" }
    val registros = financeState?.let { it.ingresos.size + it.gastosFijos.size + it.gastosVariables.size + it.deudas.size }

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = ProfileSheetShape,
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 12.dp
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Fila tipo "dirección" (pin) con separador sutil debajo.
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Filled.Place,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(Modifier.width(6.dp))
                Text(
                    text = "Datos guardados localmente en este dispositivo",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.weight(1f)
                )
            }
            HorizontalDivider(thickness = 1.dp, color = MaterialTheme.colorScheme.outlineVariant)

            // Resumen completo (los informes quedan integrados dentro del resumen).
            SectionHeader("Resumen")
            SummaryTile("Dinero disponible", balance?.let { money(it.dineroDisponible) } ?: "—", IpistoPalette.Balance, Modifier.fillMaxWidth())
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                SummaryTile("Ingresos", balance?.let { money(it.ingresosMensuales) } ?: "—", IpistoPalette.Income, Modifier.weight(1f))
                SummaryTile("Gastos", balance?.let { money(it.gastosFijosMensuales + it.gastosVariablesMensuales + it.pagosDeudaMensuales) } ?: "—", IpistoPalette.Expense, Modifier.weight(1f))
            }
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                SummaryTile("Deudas", balance?.let { money(it.deudaTotal) } ?: "—", MaterialTheme.colorScheme.tertiary, Modifier.weight(1f))
                SummaryTile("Registros", registros?.toString() ?: "—", MaterialTheme.colorScheme.onSurface, Modifier.weight(1f))
            }

            // Toda la pantalla de informes, integrada debajo del resumen.
            SectionHeader("Informes")
            financeViewModel?.let { InformesContent(it) }

            Spacer(Modifier.height(8.dp))
        }
    }
}

@Composable
private fun SectionHeader(title: String, modifier: Modifier = Modifier) {
    Text(
        text = title,
        modifier = modifier,
        fontSize = 13.sp,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.primary
    )
}

@Composable
private fun SummaryTile(label: String, value: String, valueColor: Color, modifier: Modifier = Modifier) {
    Surface(shape = ProfileTileShape, color = MaterialTheme.colorScheme.surfaceVariant, modifier = modifier) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(text = label, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(text = value, fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = valueColor)
        }
    }
}
