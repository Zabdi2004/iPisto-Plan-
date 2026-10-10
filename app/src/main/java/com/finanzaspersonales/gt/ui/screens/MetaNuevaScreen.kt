package com.finanzaspersonales.gt.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.lifecycle.viewmodel.compose.viewModel
import com.finanzaspersonales.gt.viewmodel.FinanzasViewModel
import com.finanzaspersonales.gt.ui.components.MetaAhorroDialog

@Composable
fun MetaNuevaScreen(
    navController: NavHostController,
    finanzasViewModel: FinanzasViewModel = viewModel()
) {
    val uiState by finanzasViewModel.uiState.collectAsState()
    var showDialog by remember { mutableStateOf(true) }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp)
    ) {
        uiState.error?.let { error ->
            Text("No se completó la operación: $error", color = MaterialTheme.colorScheme.error)
        }
        if (showDialog) {
            MetaAhorroDialog(
                onDismiss = { showDialog = false; navController.popBackStack() },
                onConfirm = { name, objetivo, ahorrado, aporte ->
                    val meta = com.finanzaspersonales.gt.data.local.entity.MetaAhorro(
                        id = 0,
                        userId = 0,
                        nombre = name,
                        cantidadObjetivo = objetivo,
                        cantidadAhorrada = ahorrado,
                        aporteMensual = aporte
                    )
                    finanzasViewModel.insertMeta(meta) { saved ->
                        if (saved) { showDialog = false; navController.popBackStack() }
                    }
                },
                isEditing = false,
                saveError = uiState.error
            )
        }
    }
}
