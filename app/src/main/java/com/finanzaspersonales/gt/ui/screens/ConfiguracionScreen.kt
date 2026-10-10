package com.finanzaspersonales.gt.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.compose.runtime.collectAsState
import com.finanzaspersonales.gt.ui.components.ConfirmDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfiguracionScreen(
    navController: NavHostController
) {
    val showDeleteConfirm = remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "CONFIGURACIÓN",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(16.dp))

        // APARIENCIA
        ConfigSection(title = "APARIENCIA") {
            ConfigRow(
                title = "Tema",
                subtitle = "Claro (predeterminado)",
                icon = Icons.Default.DarkMode,
                trailing = { Text("Claro", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant) }
            )
            ConfigRow(
                title = "Moneda",
                subtitle = "Quetzales (Q)",
                icon = Icons.Default.AttachMoney,
                trailing = { Text("GTQ", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant) }
            )
            ConfigRow(
                title = "Idioma",
                subtitle = "Español",
                icon = Icons.Default.Language,
                trailing = { Text("Español", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant) }
            )
        }

        // SEGURIDAD
        ConfigSection(title = "SEGURIDAD") {
            ConfigRow(
                title = "Autenticación biométrica",
                subtitle = "Usar huella o Face ID para desbloquear",
                icon = Icons.Default.Fingerprint,
                trailing = {
                    Switch(
                        checked = false,
                        onCheckedChange = { }
                    )
                }
            )
            ConfigRow(
                title = "Cambiar contraseña",
                subtitle = "Actualizar tu contraseña de acceso",
                icon = Icons.Default.Lock,
                trailing = { Icon(Icons.Default.ChevronRight, contentDescription = "Siguiente") }
            )
            ConfigRow(
                title = "Bloqueo automático",
                subtitle = "Bloquear app al salir",
                icon = Icons.Default.TimerOff,
                trailing = { Text("Inmediato", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant) }
            )
        }

        // NOTIFICACIONES
        ConfigSection(title = "NOTIFICACIONES") {
            ConfigRow(
                title = "Notificaciones push",
                subtitle = "Recibir recordatorios y alertas",
                icon = Icons.Default.Notifications,
                trailing = {
                    Switch(
                        checked = true,
                        onCheckedChange = { }
                    )
                }
            )
            ConfigRow(
                title = "Recordatorio de metas",
                subtitle = "Avisar cuando falte poco para una meta",
                icon = Icons.Default.EventAvailable,
                trailing = { Icon(Icons.Default.ChevronRight, contentDescription = "Configurar") }
            )
            ConfigRow(
                title = "Alertas de gastos",
                subtitle = "Notificar gastos altos inusuales",
                icon = Icons.Default.Warning,
                trailing = { Icon(Icons.Default.ChevronRight, contentDescription = "Configurar") }
            )
        }

        // DATOS
        ConfigSection(title = "DATOS Y RESPALDO") {
            ConfigRow(
                title = "Respaldo automático",
                subtitle = "Guardar copia de seguridad en almacenamiento",
                icon = Icons.Default.Backup,
                trailing = { Icon(Icons.Default.ChevronRight, contentDescription = "Configurar") }
            )
            ConfigRow(
                title = "Restaurar respaldo",
                subtitle = "Recuperar datos desde archivo de respaldo",
                icon = Icons.Default.Restore,
                trailing = { Icon(Icons.Default.ChevronRight, contentDescription = "Restaurar") }
            )
            ConfigRow(
                title = "Exportar datos",
                subtitle = "Generar reporte CSV/PDF",
                icon = Icons.Default.FileDownload,
                trailing = { Icon(Icons.Default.ChevronRight, contentDescription = "Exportar") }
            )
        }

        // ZONA DE PELIGRO
        ConfigSection(title = "ZONA DE PELIGRO", isDanger = true) {
            ConfigRow(
                title = "Eliminar todos mis datos",
                subtitle = "Borrar cuenta, transacciones, metas y deudas permanentemente",
                icon = Icons.Default.DeleteForever,
                iconColor = MaterialTheme.colorScheme.error,
                titleColor = MaterialTheme.colorScheme.error,
                trailing = { Icon(Icons.Default.ChevronRight, contentDescription = "Eliminar", tint = MaterialTheme.colorScheme.error) },
                onClick = { showDeleteConfirm.value = true }
            )
            ConfigRow(
                title = "Restablecer configuración",
                subtitle = "Volver a valores predeterminados",
                icon = Icons.Default.Refresh,
                trailing = { Icon(Icons.Default.ChevronRight, contentDescription = "Restablecer") }
            )
            ConfigRow(
                title = "Eliminar cuenta",
                subtitle = "Eliminar perfil y todos los datos asociados",
                icon = Icons.Default.PersonRemove,
                iconColor = MaterialTheme.colorScheme.error,
                titleColor = MaterialTheme.colorScheme.error,
                trailing = { Icon(Icons.Default.ChevronRight, contentDescription = "Eliminar", tint = MaterialTheme.colorScheme.error) }
            )
        }

        if (showDeleteConfirm.value) {
            ConfirmDialog(
                onDismiss = { showDeleteConfirm.value = false },
                onConfirm = { 
                    showDeleteConfirm.value = false 
                },
                title = "Eliminar todos los datos",
                message = "Esta acción eliminará PERMANENTEMENTE todas tus transacciones, deudas, metas y configuración. No se puede deshacer. ¿Estás seguro?",
                confirmText = "Eliminar todo",
                confirmColor = MaterialTheme.colorScheme.error
            )
        }
    }
}

@Composable
fun ConfigSection(
    title: String,
    isDanger: Boolean = false,
    content: @Composable () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(0.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelMedium,
            color = if (isDanger) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp, top = 8.dp, start = 4.dp)
        )
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = if (isDanger) MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.surface
            )
        ) {
            content()
        }
    }
}

@Composable
fun ConfigRow(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    trailing: @Composable () -> Unit,
    iconColor: androidx.compose.ui.graphics.Color = androidx.compose.ui.graphics.Color.Unspecified,
    titleColor: androidx.compose.ui.graphics.Color = androidx.compose.ui.graphics.Color.Unspecified,
    onClick: (() -> Unit)? = null
) {
    val modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp, vertical = 12.dp)

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = if (iconColor != androidx.compose.ui.graphics.Color.Unspecified) iconColor else MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp).padding(end = 16.dp)
            )
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge,
                    color = if (titleColor != androidx.compose.ui.graphics.Color.Unspecified) titleColor else MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        trailing()
    }
}
