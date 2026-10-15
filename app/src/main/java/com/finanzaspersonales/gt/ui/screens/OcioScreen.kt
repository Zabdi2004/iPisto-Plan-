package com.finanzaspersonales.gt.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.finanzaspersonales.gt.R
import com.finanzaspersonales.gt.data.local.entity.PresupuestoOcio
import com.finanzaspersonales.gt.data.local.entity.PeriodicidadPresupuesto
import com.finanzaspersonales.gt.domain.calculator.EstadoPresupuesto
import com.finanzaspersonales.gt.ui.components.QuetzalMascot
import com.finanzaspersonales.gt.ui.theme.IpistoPalette
import com.finanzaspersonales.gt.utils.MoneyInput
import com.finanzaspersonales.gt.viewmodel.FinanzasViewModel
import java.math.BigDecimal

@Composable
fun OcioScreen(
    navController: androidx.navigation.NavHostController,
    finanzasViewModel: FinanzasViewModel
) {
    val state by finanzasViewModel.uiState.collectAsState()
    val presupuesto = state.presupuestoOcio
    val presupuestoResult = state.presupuestoResult
    val dineroDisponible = state.dineroDisponibleOcio
    val balance = state.balance

    var showConfigDialog by rememberSaveable { mutableStateOf(false) }
    var showDeleteDialog by rememberSaveable { mutableStateOf(false) }
    var showSimulationDialog by rememberSaveable { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                stringResource(R.string.ocio_title),
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.weight(1f)
            )
            if (presupuesto != null) {
                IconButton(onClick = { showConfigDialog = true }) {
                    Icon(Icons.Default.Edit, contentDescription = stringResource(R.string.ocio_edit_budget), tint = MaterialTheme.colorScheme.primary)
                }
                IconButton(onClick = { showDeleteDialog = true }) {
                    Icon(Icons.Default.Delete, contentDescription = stringResource(R.string.ocio_delete_budget), tint = MaterialTheme.colorScheme.error)
                }
            }
        }

        if (state.isLoading) {
            Box(Modifier.fillMaxWidth().padding(vertical = 32.dp), contentAlignment = Alignment.Center) {
                QuetzalMascot(size = 48.dp)
            }
        } else {
            DineroDisponibleSection(
                dineroDisponible = dineroDisponible,
                balance = balance,
                formatter = { finanzasViewModel.formatCurrencyBd(it) }
            )

            if (presupuestoResult != null) {
                PresupuestoResumenSection(
                    result = presupuestoResult!!,
                    formatter = { finanzasViewModel.formatCurrencyBd(it) },
                    onSimulateClick = { showSimulationDialog = true }
                )
            } else {
                EmptyPresupuestoSection(
                    onConfigureClick = { showConfigDialog = true }
                )
            }

            val hayGastos = state.gastosVariables.isNotEmpty() || state.gastosFijos.isNotEmpty()
            if (!hayGastos) {
                Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
                    Text(
                        stringResource(R.string.ocio_gasto_ocio_vacio),
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                GastosOcioSection(gastos = state.gastosVariables, finanzasViewModel = finanzasViewModel)
                GastosFijosOcioSection(gastos = state.gastosFijos, finanzasViewModel = finanzasViewModel)
            }

            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = { showSimulationDialog = true },
                modifier = Modifier.fillMaxWidth(),
                enabled = presupuestoResult != null
            ) {
                Text(stringResource(R.string.ocio_simulation_title))
            }
            state.error?.let { errorMsg ->
                Text(
                    text = errorMsg,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    }

    if (showConfigDialog) {
        PresupuestoOcioDialog(
            onDismiss = { showConfigDialog = false },
            onConfirm = { presupuesto, onComplete ->
                finanzasViewModel.savePresupuestoOcio(presupuesto) {
                    showConfigDialog = false
                    onComplete(it)
                }
            },
            initialPresupuesto = presupuesto,
            formatter = { finanzasViewModel.formatCurrency(it) }
        )
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text(stringResource(R.string.ocio_delete_budget_confirm)) },
            text = { Text(stringResource(R.string.ocio_delete_budget_msg)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        finanzasViewModel.deletePresupuestoOcio { showDeleteDialog = false }
                    },
                    colors = androidx.compose.material3.ButtonDefaults.textButtonColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) { Text(stringResource(R.string.ocio_delete_budget), color = MaterialTheme.colorScheme.error) }
            },
            dismissButton = { TextButton(onClick = { showDeleteDialog = false }) { Text(stringResource(R.string.cancel_button)) } }
        )
    }

    if (showSimulationDialog) {
        val currentResult = state.presupuestoResult
        SimulationDialog(
            onDismiss = { showSimulationDialog = false },
            presupuestoResult = currentResult,
            simularCompra = { finanzasViewModel.simularCompra(it) },
            formatter = { finanzasViewModel.formatCurrencyBd(it) }
        )
    }
}

@Composable
private fun DineroDisponibleSection(
    dineroDisponible: com.finanzaspersonales.gt.domain.calculator.DineroDisponibleOcioResult?,
    balance: com.finanzaspersonales.gt.domain.calculator.BalanceFinanciero?,
    formatter: (BigDecimal) -> String
) {
    Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(stringResource(R.string.ocio_available_money), style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.height(4.dp))
            if (dineroDisponible != null) {
                Text(
                    formatter(dineroDisponible.dineroDisponible),
                    style = MaterialTheme.typography.headlineLarge,
                    color = if (dineroDisponible.dineroDisponible.compareTo(BigDecimal.ZERO) >= 0) IpistoPalette.Balance else IpistoPalette.Expense
                )
            } else {
                Text(
                    formatter(BigDecimal.ZERO),
                    style = MaterialTheme.typography.headlineLarge,
                    color = IpistoPalette.Balance
                )
            }
        }
    }
}

@Composable
private fun PresupuestoResumenSection(
    result: com.finanzaspersonales.gt.domain.calculator.OcioPresupuestoResult,
    formatter: (BigDecimal) -> String,
    onSimulateClick: () -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(stringResource(R.string.ocio_title), style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text(stringResource(R.string.ocio_budget_initial), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(formatter(result.presupuestoConfigurado), style = MaterialTheme.typography.titleMedium, color = IpistoPalette.Balance)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(stringResource(R.string.ocio_remaining), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(formatter(result.presupuestoRestante), style = MaterialTheme.typography.titleMedium, color = if (result.presupuestoRestante.compareTo(BigDecimal.ZERO) > 0) IpistoPalette.Income else IpistoPalette.Expense)
                }
            }

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text(stringResource(R.string.ocio_spent), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(formatter(result.gastosOcioDelPeriodo), style = MaterialTheme.typography.titleMedium, color = IpistoPalette.Expense)
                }
                Column {
                    Text(stringResource(R.string.ocio_consumed_percent, result.porcentajeConsumido.toInt()), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("${result.porcentajeConsumido.toInt()}%", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
                }
            }

            LinearProgressIndicator(
                progress = { (result.porcentajeConsumido / BigDecimal(100)).toFloat().coerceIn(0f, 1f) },
                modifier = Modifier.fillMaxWidth().height(8.dp),
                color = when (result.estadoPresupuesto) {
                    EstadoPresupuesto.ADVERTENCIA_75 -> IpistoPalette.Peach
                    EstadoPresupuesto.ADVERTENCIA_90 -> IpistoPalette.Expense
                    EstadoPresupuesto.AGOTADO -> IpistoPalette.Expense
                    else -> IpistoPalette.Accent
                },
                trackColor = MaterialTheme.colorScheme.surface
            )

            when (result.estadoPresupuesto) {
                EstadoPresupuesto.ADVERTENCIA_75 -> {
                    Text(stringResource(R.string.ocio_warning_75), color = IpistoPalette.Peach, style = MaterialTheme.typography.bodySmall)
                }
                EstadoPresupuesto.ADVERTENCIA_90 -> {
                    Text(stringResource(R.string.ocio_warning_90), color = IpistoPalette.Expense, style = MaterialTheme.typography.bodySmall)
                }
                EstadoPresupuesto.AGOTADO -> {
                    Text(stringResource(R.string.ocio_budget_consumed), color = IpistoPalette.Expense, style = MaterialTheme.typography.bodySmall)
                }
                EstadoPresupuesto.SIN_PRESUPUESTO -> {
                    Text(stringResource(R.string.ocio_zero_budget), color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.bodySmall)
                }
                else -> {}
            }

            if (result.remanenteAcumulado.compareTo(BigDecimal.ZERO) > 0) {
                Text(
                    stringResource(R.string.ocio_rollover_enabled, formatter(result.remanenteAcumulado)),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.secondary
                )
            } else {
                Text(stringResource(R.string.ocio_rollover_disabled), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            TextButton(onClick = onSimulateClick, modifier = Modifier.fillMaxWidth()) {
                Text(stringResource(R.string.ocio_simulation_title))
            }
        }
    }
}

@Composable
private fun EmptyPresupuestoSection(onConfigureClick: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
        Column(Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(12.dp)) {
            QuetzalMascot(size = 48.dp)
            Text(stringResource(R.string.ocio_no_budget), style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
            Text(stringResource(R.string.ocio_no_budget_desc), style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = TextAlign.Center)
            Button(onClick = onConfigureClick) { Text(stringResource(R.string.ocio_configure_budget)) }
        }
    }
}

@Composable
private fun GastosOcioSection(gastos: List<com.finanzaspersonales.gt.data.local.entity.GastoVariable>, finanzasViewModel: FinanzasViewModel) {
    if (gastos.isEmpty()) return

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp)) {
            Text(stringResource(R.string.ocio_gasto_ocio_variable_title), style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.height(4.dp))
            Text(stringResource(R.string.ocio_clasificar_hint), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.height(8.dp))
            gastos.forEach { gasto ->
                OcioClasificacionRow(
                    nombre = gasto.nombre,
                    categoria = gasto.categoria,
                    cantidad = gasto.cantidad,
                    esOcio = gasto.esOcio,
                    formatter = finanzasViewModel::formatCurrency,
                    onToggle = { finanzasViewModel.marcarGastoVariableComoOcio(gasto.id, !gasto.esOcio) }
                )
            }
        }
    }
}

@Composable
private fun GastosFijosOcioSection(gastos: List<com.finanzaspersonales.gt.data.local.entity.GastoFijo>, finanzasViewModel: FinanzasViewModel) {
    if (gastos.isEmpty()) return

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp)) {
            Text(stringResource(R.string.ocio_gasto_ocio_fijo_title), style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.height(8.dp))
            gastos.forEach { gasto ->
                OcioClasificacionRow(
                    nombre = gasto.nombre,
                    categoria = gasto.categoria,
                    cantidad = gasto.cantidad,
                    esOcio = gasto.esOcio,
                    formatter = finanzasViewModel::formatCurrency,
                    onToggle = { finanzasViewModel.marcarGastoFijoComoOcio(gasto.id, !gasto.esOcio) }
                )
            }
        }
    }
}

@Composable
private fun OcioClasificacionRow(
    nombre: String,
    categoria: String,
    cantidad: Double,
    esOcio: Boolean,
    formatter: (Double) -> String,
    onToggle: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(Modifier.weight(1f)) {
            Text(nombre, style = MaterialTheme.typography.bodyMedium, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text(categoria, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Text(
            formatter(cantidad),
            style = MaterialTheme.typography.bodyMedium,
            color = if (esOcio) IpistoPalette.Expense else MaterialTheme.colorScheme.onSurface
        )
        FilterChip(
            selected = esOcio,
            onClick = onToggle,
            label = { Text(stringResource(R.string.ocio_chip_label)) }
        )
    }
}

@Composable
fun PresupuestoOcioDialog(
    onDismiss: () -> Unit,
    onConfirm: (PresupuestoOcio, (Boolean) -> Unit) -> Unit,
    initialPresupuesto: PresupuestoOcio?,
    formatter: (Double) -> String
) {
    val periodicidades = listOf(PeriodicidadPresupuesto.SEMANAL, PeriodicidadPresupuesto.MENSUAL)
    var amount by remember { mutableStateOf(initialPresupuesto?.montoPresupuestado?.toString() ?: "") }
    var periodicity by remember { mutableStateOf(initialPresupuesto?.periodicidad ?: PeriodicidadPresupuesto.MENSUAL.name.lowercase()) }
    var rollover by remember { mutableStateOf(initialPresupuesto?.rolloverEnabled ?: false) }
    var remanenteAcumulado by remember { mutableStateOf(initialPresupuesto?.remanenteAcumulado ?: 0.0) }

    var amountError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val amountInvalidMessage = stringResource(R.string.amount_invalid_greater_zero)
    val budgetSaveErrorMessage = stringResource(R.string.budget_save_error)

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                if (initialPresupuesto != null) stringResource(R.string.ocio_edit_budget)
                else stringResource(R.string.ocio_configure_budget),
                style = MaterialTheme.typography.headlineSmall
            )
        },
        text = {
            Column(
                modifier = Modifier.padding(24.dp).widthIn(max = 320.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = amount,
                    onValueChange = {
                        amount = it
                        amountError = false
                    },
                    label = { Text(stringResource(R.string.ocio_budget_amount_label)) },
                    supportingText = if (amountError) ({ Text(amountInvalidMessage, color = MaterialTheme.colorScheme.error) }) else null,
                    singleLine = true,
                    isError = amountError,
                    colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                )

                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    periodicidades.forEach { p ->
                        FilterChip(
                            selected = periodicity == p.name.lowercase(),
                            onClick = { periodicity = p.name.lowercase() },
                            label = { Text(if (p == PeriodicidadPresupuesto.SEMANAL) stringResource(R.string.ocio_weekly) else stringResource(R.string.ocio_monthly)) }
                        )
                    }
                }

                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(stringResource(R.string.ocio_rollover_label), style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface)
                    androidx.compose.material3.Switch(
                        checked = rollover,
                        onCheckedChange = { rollover = it },
                        colors = androidx.compose.material3.SwitchDefaults.colors(
                            checkedBorderColor = MaterialTheme.colorScheme.primary,
                            checkedThumbColor = MaterialTheme.colorScheme.primary
                        )
                    )
                }

                if (errorMessage != null) {
                    Text(errorMessage!!, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                val parsed = MoneyInput.parseAmount(amount)
                if (parsed == null || parsed <= 0) {
                    amountError = true
                    errorMessage = amountInvalidMessage
                } else {
                    amountError = false
                    errorMessage = null
                    val presupuesto = PresupuestoOcio(
                        id = initialPresupuesto?.id ?: 0,
                        userId = 0,
                        montoPresupuestado = parsed,
                        periodicidad = periodicity,
                        rolloverEnabled = rollover,
                        fechaInicio = initialPresupuesto?.fechaInicio ?: System.currentTimeMillis(),
                        remanenteAcumulado = if (initialPresupuesto != null) initialPresupuesto.remanenteAcumulado else 0.0
                    )
                    onConfirm(presupuesto) { success ->
                        if (!success) {
                            errorMessage = budgetSaveErrorMessage
                        }
                    }
                }
            }) { Text(stringResource(R.string.ocio_save_budget)) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(R.string.cancel_button)) }
        }
    )
}

@Composable
fun SimulationDialog(
    onDismiss: () -> Unit,
    presupuestoResult: com.finanzaspersonales.gt.domain.calculator.OcioPresupuestoResult?,
    simularCompra: (String) -> com.finanzaspersonales.gt.domain.calculator.SimulacionCompraResult?,
    formatter: (BigDecimal) -> String
) {
    var amountText by remember { mutableStateOf("") }

    val simulacion = if (presupuestoResult != null) simularCompra(amountText) else null

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.ocio_simulation_title), style = MaterialTheme.typography.headlineSmall) },
        text = {
            Column(
                modifier = Modifier.widthIn(min = 320.dp, max = 360.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = amountText,
                    onValueChange = { newValue ->
                        amountText = MoneyInput.normalizeEditingValue(newValue, newValue.length).text
                    },
                    label = { Text(stringResource(R.string.ocio_purchase_amount_label)) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                )
                if (simulacion == null) {
                    Text(stringResource(R.string.ocio_simulation_close_to_limit), color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.bodyMedium)
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(stringResource(R.string.ocio_simulation_available_before), style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(formatter(simulacion.disponibleAntes), style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface)
                        }
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(stringResource(R.string.ocio_simulation_available_after), style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(formatter(simulacion.disponibleDespues), style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface)
                        }
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(stringResource(R.string.ocio_simulation_consumed_percent), style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("${simulacion.porcentajeConsumido.toInt()}%", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface)
                        }
                        Text(stringResource(R.string.ocio_simulation_recommendation), style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(simulacion.recomendacion, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.primary)
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(R.string.cancel_button)) }
        }
    )
}
