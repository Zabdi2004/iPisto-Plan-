package com.finanzaspersonales.gt.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.collectAsState
import com.finanzaspersonales.gt.viewmodel.FinanzasViewModel

@Composable
fun GraficasScreen(
    navController: NavHostController,
    finanzasViewModel: FinanzasViewModel = viewModel()
) {
    val uiState: androidx.compose.runtime.State<com.finanzaspersonales.gt.viewmodel.FinanzasUiState> = 
        finanzasViewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "GRÁFICAS",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(16.dp))

        if (uiState.value.balance == null) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Agrega datos financieros para visualizar las gráficas.",
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
        } else {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "RESUMEN FINANCIERO",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Ingresos: ${finanzasViewModel.formatCurrency(uiState.value.balance!!.ingresosMensuales)}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = "Gastos Fijos: ${finanzasViewModel.formatCurrency(uiState.value.balance!!.gastosFijosMensuales)}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = "Gastos Variables: ${finanzasViewModel.formatCurrency(uiState.value.balance!!.gastosVariablesMensuales)}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = "Pagos Deuda: ${finanzasViewModel.formatCurrency(uiState.value.balance!!.pagosDeudaMensuales)}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = "Dinero Disponible: ${finanzasViewModel.formatCurrency(uiState.value.balance!!.dineroDisponible)}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}
