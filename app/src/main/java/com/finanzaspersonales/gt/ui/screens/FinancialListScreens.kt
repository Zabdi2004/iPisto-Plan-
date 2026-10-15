package com.finanzaspersonales.gt.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.finanzaspersonales.gt.data.local.entity.Deuda
import com.finanzaspersonales.gt.data.local.entity.GastoFijo
import com.finanzaspersonales.gt.data.local.entity.GastoVariable
import com.finanzaspersonales.gt.data.local.entity.Ingreso
import com.finanzaspersonales.gt.ui.components.ConfirmDialog
import com.finanzaspersonales.gt.ui.components.DeudaDialog
import com.finanzaspersonales.gt.ui.components.GastoFijoDialog
import com.finanzaspersonales.gt.ui.components.GastoVariableDialog
import com.finanzaspersonales.gt.ui.components.IngresoDialog
import com.finanzaspersonales.gt.viewmodel.FinanzasViewModel

@Composable
private fun FinancialListHeader(title: String, onBack: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onBack) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
        }
        Text(
            text = title,
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun IngresosScreen(
    navController: NavHostController,
    finanzasViewModel: FinanzasViewModel = viewModel()
) {
    val uiState by finanzasViewModel.uiState.collectAsState()
    var showDialog by remember { mutableStateOf(false) }
    var editingIngreso by remember { mutableStateOf<Ingreso?>(null) }
    var ingresoAEliminar by remember { mutableStateOf<Ingreso?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        FinancialListHeader(title = "Ingresos", onBack = { navController.popBackStack() })
        uiState.error?.let { message ->
            Text(
                text = "No se completó la operación: $message",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium
            )
        }
        IngresoSection(
            ingresos = uiState.ingresos,
            onAdd = { editingIngreso = null; showDialog = true },
            onEdit = { editingIngreso = it; showDialog = true },
            onDelete = { ingresoAEliminar = it },
            formatter = finanzasViewModel::formatCurrency
        )
    }

    if (showDialog) {
        IngresoDialog(
            onDismiss = { showDialog = false; editingIngreso = null },
            onConfirm = { name, amount, periodicity, dateMillis ->
                val ingreso = Ingreso(
                    id = editingIngreso?.id ?: 0,
                    userId = 0,
                    nombre = name,
                    cantidad = amount,
                    periodicidad = periodicity,
                    fechaMillis = if (periodicity == "Único") dateMillis else null
                )
                val close = { saved: Boolean ->
                    if (saved) {
                        showDialog = false
                        editingIngreso = null
                    }
                }
                if (editingIngreso != null) {
                    finanzasViewModel.updateIngreso(ingreso, close)
                } else {
                    finanzasViewModel.insertIngreso(ingreso, close)
                }
            },
            initialName = editingIngreso?.nombre ?: "",
            initialAmount = editingIngreso?.cantidad?.toString() ?: "",
            initialPeriodicity = editingIngreso?.periodicidad ?: "Mensual",
            isEditing = editingIngreso != null,
            initialDateMillis = editingIngreso?.fechaMillis ?: System.currentTimeMillis(),
            saveError = uiState.error
        )
    }

    ingresoAEliminar?.let { ingreso ->
        ConfirmDialog(
            onDismiss = { ingresoAEliminar = null },
            onConfirm = { finanzasViewModel.deleteIngreso(ingreso) },
            title = "Eliminar ingreso",
            message = "¿Estás seguro de eliminar \"${ingreso.nombre}\"? Esta acción no se puede deshacer.",
            confirmText = "Eliminar",
            confirmColor = MaterialTheme.colorScheme.error
        )
    }
}

@Composable
fun GastosScreen(
    navController: NavHostController,
    finanzasViewModel: FinanzasViewModel = viewModel()
) {
    val uiState by finanzasViewModel.uiState.collectAsState()
    var showGastoFijoDialog by remember { mutableStateOf(false) }
    var editingGastoFijo by remember { mutableStateOf<GastoFijo?>(null) }
    var showGastoVariableDialog by remember { mutableStateOf(false) }
    var editingGastoVariable by remember { mutableStateOf<GastoVariable?>(null) }
    var gastoFijoAEliminar by remember { mutableStateOf<GastoFijo?>(null) }
    var gastoVariableAEliminar by remember { mutableStateOf<GastoVariable?>(null) }

    val existingCategories = (uiState.gastosFijos.map { it.categoria } + uiState.gastosVariables.map { it.categoria })
        .distinct()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        FinancialListHeader(title = "Gastos", onBack = { navController.popBackStack() })
        uiState.error?.let { message ->
            Text(
                text = "No se completó la operación: $message",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium
            )
        }
        GastoFijoSection(
            gastos = uiState.gastosFijos,
            onAdd = { editingGastoFijo = null; showGastoFijoDialog = true },
            onEdit = { editingGastoFijo = it; showGastoFijoDialog = true },
            onDelete = { gastoFijoAEliminar = it },
            formatter = finanzasViewModel::formatCurrency
        )
        GastoVariableSection(
            gastos = uiState.gastosVariables,
            onAdd = { editingGastoVariable = null; showGastoVariableDialog = true },
            onEdit = { editingGastoVariable = it; showGastoVariableDialog = true },
            onDelete = { gastoVariableAEliminar = it },
            formatter = finanzasViewModel::formatCurrency
        )
    }

    if (showGastoFijoDialog) {
        GastoFijoDialog(
            onDismiss = { showGastoFijoDialog = false; editingGastoFijo = null },
            onConfirm = { name, category, amount, periodicity, dateMillis ->
                val gasto = GastoFijo(
                    id = editingGastoFijo?.id ?: 0,
                    userId = 0,
                    nombre = name,
                    categoria = category,
                    cantidad = amount,
                    periodicidad = periodicity,
                    fechaMillis = if (periodicity == "Único") dateMillis else null
                )
                val close = { saved: Boolean ->
                    if (saved) {
                        showGastoFijoDialog = false
                        editingGastoFijo = null
                    }
                }
                if (editingGastoFijo != null) {
                    finanzasViewModel.updateGastoFijo(gasto, close)
                } else {
                    finanzasViewModel.insertGastoFijo(gasto, close)
                }
            },
            initialName = editingGastoFijo?.nombre ?: "",
            initialCategory = editingGastoFijo?.categoria ?: "Alimentación",
            existingCategories = existingCategories.filterNot { it.equals(editingGastoFijo?.categoria, ignoreCase = true) },
            initialAmount = editingGastoFijo?.cantidad?.toString() ?: "",
            initialPeriodicity = editingGastoFijo?.periodicidad ?: "Mensual",
            isEditing = editingGastoFijo != null,
            initialDateMillis = editingGastoFijo?.fechaMillis ?: System.currentTimeMillis(),
            saveError = uiState.error
        )
    }

    if (showGastoVariableDialog) {
        GastoVariableDialog(
            onDismiss = { showGastoVariableDialog = false; editingGastoVariable = null },
            onConfirm = { name, category, amount, periodicity, dateMillis ->
                val gasto = GastoVariable(
                    id = editingGastoVariable?.id ?: 0,
                    userId = 0,
                    nombre = name,
                    categoria = category,
                    cantidad = amount,
                    periodicidad = periodicity,
                    fechaMillis = if (periodicity == "Único") dateMillis else null
                )
                val close = { saved: Boolean ->
                    if (saved) {
                        showGastoVariableDialog = false
                        editingGastoVariable = null
                    }
                }
                if (editingGastoVariable != null) {
                    finanzasViewModel.updateGastoVariable(gasto, close)
                } else {
                    finanzasViewModel.insertGastoVariable(gasto, close)
                }
            },
            initialName = editingGastoVariable?.nombre ?: "",
            initialCategory = editingGastoVariable?.categoria ?: "Entretenimiento",
            existingCategories = existingCategories.filterNot { it.equals(editingGastoVariable?.categoria, ignoreCase = true) },
            initialAmount = editingGastoVariable?.cantidad?.toString() ?: "",
            initialPeriodicity = editingGastoVariable?.periodicidad ?: "Mensual",
            isEditing = editingGastoVariable != null,
            initialDateMillis = editingGastoVariable?.fechaMillis ?: System.currentTimeMillis(),
            saveError = uiState.error
        )
    }

    gastoFijoAEliminar?.let { gasto ->
        ConfirmDialog(
            onDismiss = { gastoFijoAEliminar = null },
            onConfirm = { finanzasViewModel.deleteGastoFijo(gasto) },
            title = "Eliminar gasto fijo",
            message = "¿Estás seguro de eliminar \"${gasto.nombre}\"? Esta acción no se puede deshacer.",
            confirmText = "Eliminar",
            confirmColor = MaterialTheme.colorScheme.error
        )
    }

    gastoVariableAEliminar?.let { gasto ->
        ConfirmDialog(
            onDismiss = { gastoVariableAEliminar = null },
            onConfirm = { finanzasViewModel.deleteGastoVariable(gasto) },
            title = "Eliminar gasto variable",
            message = "¿Estás seguro de eliminar \"${gasto.nombre}\"? Esta acción no se puede deshacer.",
            confirmText = "Eliminar",
            confirmColor = MaterialTheme.colorScheme.error
        )
    }
}

@Composable
fun DeudasScreen(
    navController: NavHostController,
    finanzasViewModel: FinanzasViewModel = viewModel()
) {
    val uiState by finanzasViewModel.uiState.collectAsState()
    var showDialog by remember { mutableStateOf(false) }
    var editingDeuda by remember { mutableStateOf<Deuda?>(null) }
    var deudaAEliminar by remember { mutableStateOf<Deuda?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        FinancialListHeader(title = "Deudas", onBack = { navController.popBackStack() })
        uiState.error?.let { message ->
            Text(
                text = "No se completó la operación: $message",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium
            )
        }
        DeudaSection(
            deudas = uiState.deudas,
            onAdd = { editingDeuda = null; showDialog = true },
            onEdit = { editingDeuda = it; showDialog = true },
            onDelete = { deudaAEliminar = it },
            formatter = finanzasViewModel::formatCurrency
        )
    }

    if (showDialog) {
        DeudaDialog(
            onDismiss = { showDialog = false; editingDeuda = null },
            onConfirm = { name, montoTotal, pagoPeriodico, periodicity, dateMillis ->
                val deuda = Deuda(
                    id = editingDeuda?.id ?: 0,
                    userId = 0,
                    nombre = name,
                    montoTotal = montoTotal,
                    pagoPeriodico = pagoPeriodico,
                    periodicidad = periodicity,
                    fechaMillis = if (periodicity == "Único") dateMillis else null
                )
                val close = { saved: Boolean ->
                    if (saved) {
                        showDialog = false
                        editingDeuda = null
                    }
                }
                if (editingDeuda != null) {
                    finanzasViewModel.updateDeuda(deuda, close)
                } else {
                    finanzasViewModel.insertDeuda(deuda, close)
                }
            },
            initialName = editingDeuda?.nombre ?: "",
            initialMontoTotal = editingDeuda?.montoTotal?.toString() ?: "",
            initialPagoPeriodico = editingDeuda?.pagoPeriodico?.toString() ?: "",
            initialPeriodicity = editingDeuda?.periodicidad ?: "Mensual",
            isEditing = editingDeuda != null,
            initialDateMillis = editingDeuda?.fechaMillis ?: System.currentTimeMillis(),
            saveError = uiState.error
        )
    }

    deudaAEliminar?.let { deuda ->
        ConfirmDialog(
            onDismiss = { deudaAEliminar = null },
            onConfirm = { finanzasViewModel.deleteDeuda(deuda) },
            title = "Eliminar deuda",
            message = "¿Estás seguro de eliminar \"${deuda.nombre}\"? Esta acción no se puede deshacer.",
            confirmText = "Eliminar",
            confirmColor = MaterialTheme.colorScheme.error
        )
    }
}
