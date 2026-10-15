package com.finanzaspersonales.gt.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.background
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.finanzaspersonales.gt.R
import com.finanzaspersonales.gt.data.local.entity.Ingreso
import com.finanzaspersonales.gt.data.local.entity.GastoFijo
import com.finanzaspersonales.gt.data.local.entity.GastoVariable
import com.finanzaspersonales.gt.data.local.entity.Deuda
import com.finanzaspersonales.gt.data.local.entity.MetaAhorro
import com.finanzaspersonales.gt.ui.components.ConfirmDialog
import com.finanzaspersonales.gt.utils.CurrencyFormatter
import com.finanzaspersonales.gt.utils.formatMonthly
import com.finanzaspersonales.gt.ui.theme.IpistoPalette

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IngresoSection(
    ingresos: List<Ingreso>,
    onAdd: () -> Unit,
    onEdit: (Ingreso) -> Unit,
    onDelete: (Ingreso) -> Unit,
    formatter: (Double) -> String
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(stringResource(R.string.income), style = MaterialTheme.typography.titleMedium, color = IpistoPalette.Income)
                IconButton(onClick = onAdd) {
                    Icon(Icons.Filled.Add, contentDescription = stringResource(R.string.add_income), tint = IpistoPalette.Income)
                }
            }
            Spacer(modifier = Modifier.height(8.dp))

            if (ingresos.isEmpty()) {
                Text(
                    stringResource(R.string.no_income_registered),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp)
                )
            } else {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ingresos.forEach { ingreso ->
                        IngresoItem(
                            ingreso = ingreso,
                            onEdit = { onEdit(ingreso) },
                            onDelete = { onDelete(ingreso) },
                            formatter = formatter
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun IngresoItem(
    ingreso: Ingreso,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    formatter: (Double) -> String
) {
    val monthly = ingreso.formatMonthly()
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(Modifier.weight(1f)) {
                Text(ingreso.nombre, style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(4.dp))
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(formatter(monthly), style = MaterialTheme.typography.titleMedium, color = IpistoPalette.Income)
                    Text(
                        stringResource(R.string.monthly_equivalent_detail, ingreso.periodicidad, formatter(ingreso.cantidad)),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onEdit) {
                    Icon(Icons.Filled.Edit, contentDescription = stringResource(R.string.edit), tint = MaterialTheme.colorScheme.primary)
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Filled.Delete, contentDescription = stringResource(R.string.remove), tint = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GastoFijoSection(
    gastos: List<GastoFijo>,
    onAdd: () -> Unit,
    onEdit: (GastoFijo) -> Unit,
    onDelete: (GastoFijo) -> Unit,
    formatter: (Double) -> String
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(stringResource(R.string.fixed_expenses), style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.error)
                IconButton(onClick = onAdd) {
                    Icon(Icons.Filled.Add, contentDescription = stringResource(R.string.add_fixed_expense), tint = MaterialTheme.colorScheme.error)
                }
            }
            Spacer(modifier = Modifier.height(8.dp))

            if (gastos.isEmpty()) {
                Text(
                    stringResource(R.string.no_fixed_expenses_registered),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp)
                )
            } else {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    gastos.forEach { gasto ->
                        GastoFijoItem(
                            gasto = gasto,
                            onEdit = { onEdit(gasto) },
                            onDelete = { onDelete(gasto) },
                            formatter = formatter
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun GastoFijoItem(
    gasto: GastoFijo,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    formatter: (Double) -> String
) {
    val monthly = gasto.formatMonthly()
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(Modifier.weight(1f)) {
                Text(gasto.nombre, style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(4.dp))
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(gasto.categoria, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(formatter(monthly), style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.error)
                    Text(
                        stringResource(R.string.monthly_equivalent_detail, gasto.periodicidad, formatter(gasto.cantidad)),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onEdit) {
                    Icon(Icons.Filled.Edit, contentDescription = stringResource(R.string.edit), tint = MaterialTheme.colorScheme.primary)
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Filled.Delete, contentDescription = stringResource(R.string.remove), tint = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GastoVariableSection(
    gastos: List<GastoVariable>,
    onAdd: () -> Unit,
    onEdit: (GastoVariable) -> Unit,
    onDelete: (GastoVariable) -> Unit,
    formatter: (Double) -> String
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(stringResource(R.string.variable_expenses), style = MaterialTheme.typography.titleMedium, color = IpistoPalette.Expense)
                IconButton(onClick = onAdd) {
                    Icon(Icons.Filled.Add, contentDescription = stringResource(R.string.add_variable_expense), tint = IpistoPalette.Expense)
                }
            }
            Spacer(modifier = Modifier.height(8.dp))

            if (gastos.isEmpty()) {
                Text(
                    stringResource(R.string.no_variable_expenses_registered),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp)
                )
            } else {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    gastos.forEach { gasto ->
                        GastoVariableItem(
                            gasto = gasto,
                            onEdit = { onEdit(gasto) },
                            onDelete = { onDelete(gasto) },
                            formatter = formatter
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun GastoVariableItem(
    gasto: GastoVariable,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    formatter: (Double) -> String
) {
    val monthly = gasto.formatMonthly()
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(Modifier.weight(1f)) {
                Text(gasto.nombre, style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(4.dp))
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(gasto.categoria, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(formatter(monthly), style = MaterialTheme.typography.titleMedium, color = IpistoPalette.Expense)
                    Text(
                        stringResource(R.string.monthly_equivalent_detail, gasto.periodicidad, formatter(gasto.cantidad)),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onEdit) {
                    Icon(Icons.Filled.Edit, contentDescription = stringResource(R.string.edit), tint = MaterialTheme.colorScheme.primary)
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Filled.Delete, contentDescription = stringResource(R.string.remove), tint = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeudaSection(
    deudas: List<Deuda>,
    onAdd: () -> Unit,
    onEdit: (Deuda) -> Unit,
    onDelete: (Deuda) -> Unit,
    formatter: (Double) -> String
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(stringResource(R.string.debts), style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.error)
                IconButton(onClick = onAdd) {
                    Icon(Icons.Filled.Add, contentDescription = stringResource(R.string.add_debt), tint = MaterialTheme.colorScheme.error)
                }
            }
            Spacer(modifier = Modifier.height(8.dp))

            if (deudas.isEmpty()) {
                Text(
                    stringResource(R.string.no_debts_registered),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp)
                )
            } else {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    deudas.forEach { deuda ->
                        DeudaItem(
                            deuda = deuda,
                            onEdit = { onEdit(deuda) },
                            onDelete = { onDelete(deuda) },
                            formatter = formatter
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DeudaItem(
    deuda: Deuda,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    formatter: (Double) -> String
) {
    val monthlyPayment = deuda.formatMonthly()
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(deuda.nombre, style = MaterialTheme.typography.titleMedium)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onEdit) {
                        Icon(Icons.Filled.Edit, contentDescription = stringResource(R.string.edit), tint = MaterialTheme.colorScheme.primary)
                    }
                    IconButton(onClick = onDelete) {
                        Icon(Icons.Filled.Delete, contentDescription = stringResource(R.string.remove), tint = MaterialTheme.colorScheme.error)
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(Modifier.weight(1f)) {
                    Text(stringResource(R.string.total_amount, formatter(deuda.montoTotal)), style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(stringResource(R.string.monthly_payment_estimated, formatter(monthlyPayment)), style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.error)
                }
                Text(
                    stringResource(R.string.debt_payment_period_detail, deuda.periodicidad, formatter(deuda.pagoPeriodico)),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MetaAhorroSection(
    metas: List<MetaAhorro>,
    onAdd: () -> Unit,
    onEdit: (MetaAhorro) -> Unit,
    onDelete: (MetaAhorro) -> Unit,
    formatter: (Double) -> String
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(stringResource(R.string.goals), style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.secondary)
                IconButton(onClick = onAdd) {
                    Icon(Icons.Filled.Add, contentDescription = stringResource(R.string.add_goal), tint = MaterialTheme.colorScheme.secondary)
                }
            }
            Spacer(modifier = Modifier.height(8.dp))

            if (metas.isEmpty()) {
                Text(
                    stringResource(R.string.no_goals_registered),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp)
                )
            } else {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    metas.forEach { meta ->
                        MetaAhorroItem(
                            meta = meta,
                            onEdit = { onEdit(meta) },
                            onDelete = { onDelete(meta) },
                            formatter = formatter
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MetaAhorroItem(
    meta: MetaAhorro,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    formatter: (Double) -> String
) {
    val progress = if (meta.cantidadObjetivo > 0) (meta.cantidadAhorrada / meta.cantidadObjetivo).coerceIn(0.0, 1.0) else 0.0
    val progressFloat = progress.toFloat()
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(meta.nombre, style = MaterialTheme.typography.titleMedium)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onEdit) {
                        Icon(Icons.Filled.Edit, contentDescription = stringResource(R.string.edit), tint = MaterialTheme.colorScheme.primary)
                    }
                    IconButton(onClick = onDelete) {
                        Icon(Icons.Filled.Delete, contentDescription = stringResource(R.string.remove), tint = MaterialTheme.colorScheme.error)
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(stringResource(R.string.saved_objective, formatter(meta.cantidadAhorrada), formatter(meta.cantidadObjetivo)), style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(1f))
                    Text(stringResource(R.string.progress_percent, (progress * 100).toInt()), style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.secondary)
                }
                Spacer(modifier = Modifier.height(4.dp))
                // Custom progress bar using Row with weight
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .background(MaterialTheme.colorScheme.secondaryContainer)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .weight(progressFloat)
                            .background(MaterialTheme.colorScheme.secondary)
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .weight(1.0f - progressFloat)
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    stringResource(R.string.monthly_contribution_label, formatter(meta.aporteMensual)),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
