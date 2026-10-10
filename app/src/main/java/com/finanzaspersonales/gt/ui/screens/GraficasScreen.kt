package com.finanzaspersonales.gt.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.finanzaspersonales.gt.ui.components.FoxMascot
import com.finanzaspersonales.gt.domain.calculator.BalanceCalculator
import com.finanzaspersonales.gt.viewmodel.FinanzasViewModel

private data class ChartRow(val label: String, val amount: Double, val detail: String? = null)

@Composable
fun GraficasScreen(finanzasViewModel: FinanzasViewModel) {
    val state by finanzasViewModel.uiState.collectAsState()
    val balance = state.balance
    val gastosPorCategoria = buildMap<String, Double> {
        state.gastosFijos.forEach { gasto ->
            put(gasto.categoria, (get(gasto.categoria) ?: 0.0) + BalanceCalculator.normalizarAMensual(gasto.cantidad, gasto.periodicidad))
        }
        state.gastosVariables.forEach { gasto ->
            put(gasto.categoria, (get(gasto.categoria) ?: 0.0) + BalanceCalculator.normalizarAMensual(gasto.cantidad, gasto.periodicidad))
        }
    }.filterValues { it > 0.0 }.toList().sortedByDescending { it.second }

    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Gráficas y resúmenes", style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.primary)
        Text(
            "Importes mensuales normalizados a partir de tus registros guardados.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        if (state.isLoading) {
            Text("Cargando tus datos…", style = MaterialTheme.typography.bodyMedium)
        } else if (state.error != null) {
            Text(state.error ?: "No se pudieron cargar los datos.", color = MaterialTheme.colorScheme.error)
        } else if (balance == null || (state.ingresos.isEmpty() && state.gastosFijos.isEmpty() && state.gastosVariables.isEmpty() && state.deudas.isEmpty() && state.metas.isEmpty())) {
            EmptyChartMessage("Aún no hay datos financieros para resumir. Agrega ingresos, gastos, deudas o metas.")
        } else {
            ChartSection("Ingresos y gastos mensuales") {
                val rows = listOf(
                    ChartRow("Ingresos", balance.ingresosMensuales),
                    ChartRow("Gastos fijos", balance.gastosFijosMensuales),
                    ChartRow("Gastos variables", balance.gastosVariablesMensuales),
                    ChartRow("Pagos de deuda", balance.pagosDeudaMensuales),
                    ChartRow("Disponible", balance.dineroDisponible)
                )
                AmountBars(rows, finanzasViewModel, useAbsoluteScale = true)
                Spacer(Modifier.height(8.dp))
                Text(
                    "Período mensual equivalente. El saldo disponible conserva valores negativos.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            ChartSection("Distribución de gastos por categoría") {
                if (gastosPorCategoria.isEmpty()) EmptyChartMessage("Todavía no hay gastos registrados.")
                else AmountBars(gastosPorCategoria.map { (category, amount) -> ChartRow(category, amount) }, finanzasViewModel)
            }

            ChartSection("Deudas registradas") {
                val debts = state.deudas.map {
                    ChartRow(it.nombre, it.montoTotal, "Pago: ${finanzasViewModel.formatCurrency(BalanceCalculator.normalizarAMensual(it.pagoPeriodico, it.periodicidad))}/mes")
                }
                if (debts.isEmpty()) EmptyChartMessage("No tienes deudas registradas.")
                else AmountBars(debts, finanzasViewModel)
            }

            ChartSection("Progreso de metas de ahorro") {
                if (state.metas.isEmpty()) EmptyChartMessage("Todavía no hay metas de ahorro.")
                else state.metas.forEach { meta ->
                    val progress = if (meta.cantidadObjetivo > 0.0) (meta.cantidadAhorrada / meta.cantidadObjetivo).coerceIn(0.0, 1.0) else 0.0
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp)) {
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Text(meta.nombre, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(1f))
                            Text("${(progress * 100).toInt()}%", style = MaterialTheme.typography.labelLarge)
                        }
                        LinearProgressIndicator(progress = { progress.toFloat() }, modifier = Modifier.fillMaxWidth())
                        Text(
                            "${finanzasViewModel.formatCurrency(meta.cantidadAhorrada)} de ${finanzasViewModel.formatCurrency(meta.cantidadObjetivo)}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ChartSection(title: String, content: @Composable () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(title, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
            content()
        }
    }
}

@Composable
private fun AmountBars(rows: List<ChartRow>, viewModel: FinanzasViewModel, useAbsoluteScale: Boolean = false) {
    val maxAmount = rows.maxOfOrNull { kotlin.math.abs(it.amount) } ?: 0.0
    rows.forEach { row ->
        Column(Modifier.fillMaxWidth().padding(vertical = 4.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(row.label, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(1f))
                Text(viewModel.formatCurrency(row.amount), style = MaterialTheme.typography.labelLarge)
            }
            val fraction = if (maxAmount > 0.0) (kotlin.math.abs(row.amount) / maxAmount).toFloat().coerceIn(0f, 1f) else 0f
            LinearProgressIndicator(
                progress = { fraction },
                modifier = Modifier.fillMaxWidth(),
                color = if (useAbsoluteScale && row.amount < 0.0) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.secondary
            )
            row.detail?.let { Text(it, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant) }
        }
    }
}

@Composable
private fun EmptyChartMessage(message: String) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        FoxMascot(size = 46.dp)
        Text(message, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = TextAlign.Start)
    }
}
