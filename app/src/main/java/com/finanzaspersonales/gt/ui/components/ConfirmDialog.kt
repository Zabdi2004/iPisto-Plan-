package com.finanzaspersonales.gt.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ConfirmDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    title: String,
    message: String,
    confirmText: String = "Confirmar",
    confirmColor: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.error,
    dismissOnConfirm: Boolean = true,
    confirmEnabled: Boolean = true
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title, style = MaterialTheme.typography.headlineSmall) },
        text = { Text(message, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(24.dp).width(320.dp)) },
        confirmButton = {
            TextButton(
                enabled = confirmEnabled,
                onClick = {
                    onConfirm()
                    if (dismissOnConfirm) onDismiss()
                },
                colors = ButtonDefaults.buttonColors(containerColor = confirmColor)
            ) { Text(confirmText) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        }
    )
}
