package com.finanzaspersonales.gt.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController

@Composable
fun ConfiguracionScreen(navController: NavHostController) {
    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Configuración", style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.primary)

        Card(Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text("Preferencias actuales", style = MaterialTheme.typography.titleMedium)
                PreferenceInfo(Icons.Default.Language, "Idioma", "Español disponible. Las traducciones a inglés y K’iche’ todavía no están completas.")
                PreferenceInfo(Icons.Default.Storage, "Moneda", "Quetzal guatemalteco (Q / GTQ).")
                PreferenceInfo(Icons.Default.Lock, "Seguridad", "Contraseña y eliminación de cuenta se administran desde tu perfil.")
            }
        }

        Button(onClick = { navController.navigate("perfil") }, modifier = Modifier.fillMaxWidth()) {
            Text("Abrir perfil y seguridad")
        }

        Spacer(Modifier.height(4.dp))
        Text(
            "Los registros se guardan localmente en este dispositivo. Respaldos, exportación, biometría, notificaciones y cambio de tema no están disponibles en esta versión.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun PreferenceInfo(icon: androidx.compose.ui.graphics.vector.ImageVector, title: String, description: String) {
    androidx.compose.foundation.layout.Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
        Column {
            Text(title, style = MaterialTheme.typography.titleSmall)
            Text(description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
