package com.finanzaspersonales.gt.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.finanzaspersonales.gt.R

@Composable
fun ConfirmDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    title: String,
    message: String,
    confirmText: String? = null,
    confirmColor: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.error,
    dismissOnConfirm: Boolean = true,
    confirmEnabled: Boolean = true
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title, style = MaterialTheme.typography.headlineSmall) },
        text = { Text(message, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(24.dp).widthIn(max = 320.dp)) },
        confirmButton = {
            TextButton(
                enabled = confirmEnabled,
                onClick = {
                    onConfirm()
                    if (dismissOnConfirm) onDismiss()
                },
                colors = ButtonDefaults.buttonColors(containerColor = confirmColor)
            ) { Text(confirmText ?: stringResource(R.string.confirm_button)) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(R.string.cancel_button)) }
        }
    )
}
