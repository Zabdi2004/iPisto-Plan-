package com.finanzaspersonales.gt.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.background
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.finanzaspersonales.gt.data.local.entity.Ingreso
import com.finanzaspersonales.gt.data.local.entity.GastoFijo
import com.finanzaspersonales.gt.data.local.entity.GastoVariable
import com.finanzaspersonales.gt.data.local.entity.Deuda
import com.finanzaspersonales.gt.data.local.entity.MetaAhorro
import com.finanzaspersonales.gt.ui.components.ConfirmDialog
import com.finanzaspersonales.gt.utils.CurrencyFormatter
import com.finanzaspersonales.gt.utils.formatMonthly

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
                Text("INGRESOS", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
                IconButton(onClick = onAdd) {
                    Icon(Icons.Filled.Add, contentDescription = "Agregar ingreso", tint = MaterialTheme.colorScheme.primary)
                }
            }
            Spacer(modifier = Modifier.height(8.dp))

            if (ingresos.isEmpty()) {
                Text(
                    "No hay ingresos registrados",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp)
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(ingresos) { ingreso ->
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
            Column {
                Text(ingreso.nombre, style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(formatter(monthly), style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "/mes (${ingreso.periodicidad}: ${formatter(ingreso.cantidad)})",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onEdit) {
                    Icon(Icons.Filled.Edit, contentDescription = "Editar", tint = MaterialTheme.colorScheme.primary)
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Filled.Delete, contentDescription = "Eliminar", tint = MaterialTheme.colorScheme.error)
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
                Text("GASTOS FIJOS", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.error)
                IconButton(onClick = onAdd) {
                    Icon(Icons.Filled.Add, contentDescription = "Agregar gasto fijo", tint = MaterialTheme.colorScheme.error)
                }
            }
            Spacer(modifier = Modifier.height(8.dp))

            if (gastos.isEmpty()) {
                Text(
                    "No hay gastos fijos registrados",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp)
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(gastos) { gasto ->
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
            Column {
                Text(gasto.nombre, style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(gasto.categoria, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(formatter(monthly), style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.error)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "/mes (${gasto.periodicidad}: ${formatter(gasto.cantidad)})",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onEdit) {
                    Icon(Icons.Filled.Edit, contentDescription = "Editar", tint = MaterialTheme.colorScheme.primary)
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Filled.Delete, contentDescription = "Eliminar", tint = MaterialTheme.colorScheme.error)
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
                Text("GASTOS VARIABLES", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.tertiary)
                IconButton(onClick = onAdd) {
                    Icon(Icons.Filled.Add, contentDescription = "Agregar gasto variable", tint = MaterialTheme.colorScheme.tertiary)
                }
            }
            Spacer(modifier = Modifier.height(8.dp))

            if (gastos.isEmpty()) {
                Text(
                    "No hay gastos variables registrados",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp)
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(gastos) { gasto ->
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
            Column {
                Text(gasto.nombre, style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(gasto.categoria, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(formatter(monthly), style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.tertiary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "/mes (${gasto.periodicidad}: ${formatter(gasto.cantidad)})",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onEdit) {
                    Icon(Icons.Filled.Edit, contentDescription = "Editar", tint = MaterialTheme.colorScheme.primary)
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Filled.Delete, contentDescription = "Eliminar", tint = MaterialTheme.colorScheme.error)
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
                Text("DEUDAS", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.error)
                IconButton(onClick = onAdd) {
                    Icon(Icons.Filled.Add, contentDescription = "Agregar deuda", tint = MaterialTheme.colorScheme.error)
                }
            }
            Spacer(modifier = Modifier.height(8.dp))

            if (deudas.isEmpty()) {
                Text(
                    "No hay deudas registradas",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp)
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(deudas) { deuda ->
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
                        Icon(Icons.Filled.Edit, contentDescription = "Editar", tint = MaterialTheme.colorScheme.primary)
                    }
                    IconButton(onClick = onDelete) {
                        Icon(Icons.Filled.Delete, contentDescription = "Eliminar", tint = MaterialTheme.colorScheme.error)
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Monto total: ${formatter(deuda.montoTotal)}", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("Pago mensual: ${formatter(monthlyPayment)}", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.error)
                }
                Text(
                    "(${deuda.periodicidad}: ${formatter(deuda.pagoPeriodico)})",
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
                Text("METAS DE AHORRO", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.secondary)
                IconButton(onClick = onAdd) {
                    Icon(Icons.Filled.Add, contentDescription = "Agregar meta", tint = MaterialTheme.colorScheme.secondary)
                }
            }
            Spacer(modifier = Modifier.height(8.dp))

            if (metas.isEmpty()) {
                Text(
                    "No hay metas de ahorro registradas",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp)
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(metas) { meta ->
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
                        Icon(Icons.Filled.Edit, contentDescription = "Editar", tint = MaterialTheme.colorScheme.primary)
                    }
                    IconButton(onClick = onDelete) {
                        Icon(Icons.Filled.Delete, contentDescription = "Eliminar", tint = MaterialTheme.colorScheme.error)
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Ahorrado: ${formatter(meta.cantidadAhorrada)} / ${formatter(meta.cantidadObjetivo)}", style = MaterialTheme.typography.bodyMedium)
                    Text("${(progress * 100).toInt()}%", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.secondary)
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
                    "Aporte mensual: ${formatter(meta.aporteMensual)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
