package com.finanzaspersonales.gt.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import com.finanzaspersonales.gt.data.local.entity.MetaAhorro
import com.finanzaspersonales.gt.viewmodel.FinanzasViewModel
import com.finanzaspersonales.gt.ui.components.MetaAhorroDialog

@Composable
fun MetaEditarScreen(
    navController: NavHostController,
    metaId: Long,
    finanzasViewModel: FinanzasViewModel = viewModel()
) {
    val uiState: androidx.compose.runtime.State<com.finanzaspersonales.gt.viewmodel.FinanzasUiState> = 
        finanzasViewModel.uiState.collectAsState()
    
    val meta = remember(metaId) {
        derivedStateOf { uiState.value.metas.find { it.id == metaId } }
    }.value

    if (meta == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("Meta no encontrada", style = MaterialTheme.typography.bodyLarge) }
        return
    }

    var showDialog by remember { mutableStateOf(true) }
    var showDiscardConfirmation by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp)
    ) {
        uiState.value.error?.let { error ->
            Text("No se completó la operación: $error", color = MaterialTheme.colorScheme.error)
        }
        if (showDialog) {
            MetaAhorroDialog(
                onDismiss = { showDiscardConfirmation = true },
                onConfirm = { name, objetivo, ahorrado, aporte ->
                    val updatedMeta = meta.copy(
                        nombre = name,
                        cantidadObjetivo = objetivo,
                        cantidadAhorrada = ahorrado,
                        aporteMensual = aporte
                    )
                    finanzasViewModel.updateMeta(updatedMeta) { saved ->
                        if (saved) { showDialog = false; navController.popBackStack() }
                    }
                },
                initialName = meta.nombre,
                initialObjetivo = meta.cantidadObjetivo.toString(),
                initialAhorrado = meta.cantidadAhorrada.toString(),
                initialAporte = meta.aporteMensual.toString(),
                isEditing = true,
                saveError = uiState.value.error
            )
        }
    }

    if (showDiscardConfirmation) {
        AlertDialog(
            onDismissRequest = { showDiscardConfirmation = false },
            title = { Text("Descartar cambios") },
            text = { Text("Hay una edición en curso. ¿Quieres salir sin guardar los cambios?") },
            confirmButton = {
                TextButton(onClick = {
                    showDiscardConfirmation = false
                    showDialog = false
                    navController.popBackStack()
                }) { Text("Descartar") }
            },
            dismissButton = { TextButton(onClick = { showDiscardConfirmation = false }) { Text("Seguir editando") } }
        )
    }
}
