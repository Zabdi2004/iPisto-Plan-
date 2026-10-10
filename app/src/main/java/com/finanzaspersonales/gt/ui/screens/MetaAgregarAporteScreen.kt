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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import com.finanzaspersonales.gt.data.local.entity.MetaAhorro
import com.finanzaspersonales.gt.viewmodel.FinanzasViewModel
import com.finanzaspersonales.gt.ui.components.AmountTextField
import com.finanzaspersonales.gt.ui.components.ConfirmDialog
import com.finanzaspersonales.gt.utils.MoneyInput

@Composable
fun MetaAgregarAporteScreen(
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

    var aporte by remember { mutableStateOf("") }
    var aporteError by remember { mutableStateOf(false) }
    var showConfirm by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("AGREGAR APORTE", style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.height(8.dp))
        Text("Meta: ${meta.nombre}", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text("Ahorrado: ${finanzasViewModel.formatCurrency(meta.cantidadAhorrada)} / ${finanzasViewModel.formatCurrency(meta.cantidadObjetivo)}", style = MaterialTheme.typography.bodyMedium)
        Spacer(modifier = Modifier.height(16.dp))

        AmountTextField(
            value = androidx.compose.ui.text.input.TextFieldValue(aporte),
            onValueChange = { aporte = it.text; aporteError = false },
            label = "Cantidad a aportar (Q)",
            isError = aporteError,
            errorText = if (aporteError) "Ingrese una cantidad válida" else null,
            modifier = Modifier.fillMaxWidth()
        )

        Button(onClick = {
            val amount = MoneyInput.parseAmount(aporte)
            if (amount != null && amount.isFinite() && amount > 0 && (meta.cantidadAhorrada + amount).isFinite()) {
                showConfirm = true
            } else {
                aporteError = true
            }
        }, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)) {
            Text("AGREGAR APORTE")
        }

        if (showConfirm) {
            ConfirmDialog(
                onDismiss = { showConfirm = false },
                onConfirm = {
                    val amount = MoneyInput.parseAmount(aporte) ?: 0.0
                    val updatedMeta = meta.copy(cantidadAhorrada = meta.cantidadAhorrada + amount)
                    finanzasViewModel.updateMeta(updatedMeta)
                    showConfirm = false
                    navController.popBackStack()
                },
                title = "Confirmar aporte",
                message = "¿Agregar ${finanzasViewModel.formatCurrency(MoneyInput.parseAmount(aporte) ?: 0.0)} a \"${meta.nombre}\"?",
                confirmText = "Confirmar",
                confirmColor = MaterialTheme.colorScheme.primary
            )
        }
    }
}
