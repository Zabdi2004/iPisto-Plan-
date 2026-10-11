package com.finanzaspersonales.gt.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FilterChip
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import com.finanzaspersonales.gt.domain.calculator.ChartType
import com.finanzaspersonales.gt.domain.calculator.FinancialChartData
import com.finanzaspersonales.gt.ui.components.QuetzalMascot
import com.finanzaspersonales.gt.domain.calculator.BalanceCalculator
import com.finanzaspersonales.gt.viewmodel.FinanzasViewModel

private data class ChartRow(val label: String, val amount: Double, val detail: String? = null)

@Composable
fun GraficasScreen(finanzasViewModel: FinanzasViewModel) {
    var chartType by rememberSaveable { mutableStateOf(ChartType.LINES) }
    val state by finanzasViewModel.uiState.collectAsState()
    val balance = state.balance
    val gastosPorCategoria = buildMap<String, Double> {
        state.gastosFijos.forEach { gasto ->
            put(gasto.categoria, (get(gasto.categoria) ?: 0.0) + BalanceCalculator.equivalenteMensualEstimado(gasto.cantidad, gasto.periodicidad, gasto.fechaMillis))
        }
        state.gastosVariables.forEach { gasto ->
            put(gasto.categoria, (get(gasto.categoria) ?: 0.0) + BalanceCalculator.equivalenteMensualEstimado(gasto.cantidad, gasto.periodicidad, gasto.fechaMillis))
        }
    }.filterValues { it > 0.0 }.toList().sortedByDescending { it.second }

    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Gráficas y resúmenes", style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.primary)
        Text(
            "Equivalentes mensuales estimados a partir de tus registros; no son totales recibidos o pagados en un mes específico.",
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
            ChartSection("Equivalente mensual estimado") {
                val rows = FinancialChartData.fromBalance(balance)
                Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf(
                        ChartType.LINES to androidx.compose.ui.res.stringResource(com.finanzaspersonales.gt.R.string.chart_lines),
                        ChartType.BARS to androidx.compose.ui.res.stringResource(com.finanzaspersonales.gt.R.string.chart_bars),
                        ChartType.SCATTER to androidx.compose.ui.res.stringResource(com.finanzaspersonales.gt.R.string.chart_scatter)
                    ).forEach { (type, label) ->
                        FilterChip(selected = chartType == type, onClick = { chartType = type }, label = { Text(label) })
                    }
                }
                FinancialChart(points = rows, chartType = chartType, formatAmount = finanzasViewModel::formatCurrency)
                rows.forEach { point ->
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(point.label, style = MaterialTheme.typography.bodySmall)
                        Text(finanzasViewModel.formatCurrency(point.amount), style = MaterialTheme.typography.bodySmall)
                    }
                }
                Spacer(Modifier.height(8.dp))
                Text(
                    "Proyección mensual promedio: semanal × 52/12, quincenal (cada 15 días) × 365/15/12, diario × 365/12. Los movimientos únicos solo cuentan en el mes de su fecha. El saldo conserva valores negativos.",
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
                    ChartRow(it.nombre, it.montoTotal, "Pago estimado: ${finanzasViewModel.formatCurrency(BalanceCalculator.equivalenteMensualEstimado(it.pagoPeriodico, it.periodicidad, it.fechaMillis))}/mes")
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
private fun FinancialChart(
    points: List<com.finanzaspersonales.gt.domain.calculator.FinancialChartPoint>,
    chartType: ChartType,
    formatAmount: (Double) -> String
) {
    val lineColor = MaterialTheme.colorScheme.primary
    val expenseColor = MaterialTheme.colorScheme.error
    val incomeColor = MaterialTheme.colorScheme.tertiary
    val gridColor = MaterialTheme.colorScheme.outlineVariant
    Column {
        androidx.compose.foundation.Canvas(Modifier.fillMaxWidth().height(190.dp).semantics { contentDescription = "Gráfica $chartType: ${points.joinToString { "${it.label} ${formatAmount(it.amount)}" }}" }) {
            val left = 18.dp.toPx(); val right = size.width - 18.dp.toPx()
            val top = 12.dp.toPx(); val bottom = size.height - 18.dp.toPx()
            val max = (points.maxOfOrNull { kotlin.math.abs(it.amount) } ?: 0.0).coerceAtLeast(1.0)
            val zeroY = top + ((max / (max * 2.0)) * (bottom - top)).toFloat()
            drawLine(gridColor, Offset(left, zeroY), Offset(right, zeroY), strokeWidth = 1.dp.toPx())
            if (points.isNotEmpty()) {
                val dx = if (points.size == 1) 0f else (right - left) / (points.size - 1)
                val coords = points.mapIndexed { index, point ->
                    val x = if (points.size == 1) (left + right) / 2 else left + index * dx
                    val y = zeroY - (point.amount / max).toFloat() * ((bottom - top) / 2f)
                    Offset(x, y)
                }
                if (chartType == ChartType.LINES && coords.size > 1) {
                    val path = Path().apply { moveTo(coords.first().x, coords.first().y); coords.drop(1).forEach { lineTo(it.x, it.y) } }
                    drawPath(path, lineColor, style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round))
                }
                coords.forEachIndexed { index, point ->
                    val amount = points[index].amount
                    val color = if (index == 0) incomeColor else if (amount < 0 || index in 1..3) expenseColor else lineColor
                    if (chartType == ChartType.BARS) {
                        val barWidth = (right - left) / points.size * 0.48f
                        drawLine(color, Offset(point.x, zeroY), point, strokeWidth = barWidth.coerceAtLeast(4.dp.toPx()), cap = StrokeCap.Round)
                    }
                    drawCircle(if (chartType == ChartType.SCATTER) lineColor else color, radius = 5.dp.toPx(), center = point)
                }
            }
        }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("−${formatAmount(points.maxOfOrNull { kotlin.math.abs(it.amount) } ?: 0.0)}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text("Q0.00", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text("+${formatAmount(points.maxOfOrNull { kotlin.math.abs(it.amount) } ?: 0.0)}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
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
        QuetzalMascot(size = 46.dp)
        Text(message, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = TextAlign.Start)
    }
}
