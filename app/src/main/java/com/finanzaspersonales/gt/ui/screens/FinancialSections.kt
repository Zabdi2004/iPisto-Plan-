package com.finanzaspersonales.gt.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.navigation.NavHostController
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.finanzaspersonales.gt.data.local.entity.Ingreso
import com.finanzaspersonales.gt.data.local.entity.GastoFijo
import com.finanzaspersonales.gt.data.local.entity.GastoVariable
import com.finanzaspersonales.gt.data.local.entity.Deuda
import com.finanzaspersonales.gt.viewmodel.FinanzasViewModel
import com.finanzaspersonales.gt.ui.components.*
import com.finanzaspersonales.gt.utils.CurrencyFormatter
import com.finanzaspersonales.gt.navigation.Screen
import com.finanzaspersonales.gt.ui.theme.IpistoPalette

@Composable
fun InicioScreen(
    navController: NavHostController,
    finanzasViewModel: FinanzasViewModel = viewModel()
) {
    val uiState: androidx.compose.runtime.State<com.finanzaspersonales.gt.viewmodel.FinanzasUiState> = 
        finanzasViewModel.uiState.collectAsState()

    var showIngresoDialog by remember { mutableStateOf(false) }
    var editingIngreso by remember { mutableStateOf<Ingreso?>(null) }
    var showGastoFijoDialog by remember { mutableStateOf(false) }
    var editingGastoFijo by remember { mutableStateOf<GastoFijo?>(null) }
    var showGastoVariableDialog by remember { mutableStateOf(false) }
    var editingGastoVariable by remember { mutableStateOf<GastoVariable?>(null) }
    var showDeudaDialog by remember { mutableStateOf(false) }
    var editingDeuda by remember { mutableStateOf<Deuda?>(null) }
    var showMetaDialog by remember { mutableStateOf(false) }
    var editingMeta by remember { mutableStateOf<com.finanzaspersonales.gt.data.local.entity.MetaAhorro?>(null) }
    var showNuevaEvaluacionDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text(
            text = "INICIO",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary
        )
        uiState.value.error?.let { message ->
            Text(
                text = "No se completó la operación: $message",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            shape = MaterialTheme.shapes.large
        ) {
            Row(Modifier.fillMaxWidth().padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("Un consejo para hoy", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
                    Text("Anota tus gastos pequeños: juntos también cuentan.", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface)
                }
                FoxMascot(size = 58.dp)
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        if (uiState.value.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                ProgressBar()
            }
        } else {
            val balance = uiState.value.balance
            if (balance != null) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("DINERO DISPONIBLE", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = finanzasViewModel.formatCurrency(balance.dineroDisponible),
                            style = MaterialTheme.typography.headlineLarge,
                            color = if (balance.dineroDisponible >= 0) IpistoPalette.Balance else IpistoPalette.Expense
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Card(
                        modifier = Modifier.weight(1f),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("INGRESOS", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(finanzasViewModel.formatCurrency(balance.ingresosMensuales), style = MaterialTheme.typography.titleMedium, color = IpistoPalette.Income)
                        }
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Card(
                        modifier = Modifier.weight(1f),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("DEUDA TOTAL", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(finanzasViewModel.formatCurrency(balance.deudaTotal), style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.error)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = { finanzasViewModel.calcularBalance() },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text("CALCULAR BALANCE")
                }
                OutlinedButton(
                    onClick = { navController.navigate(Screen.Graficas) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("VER GRÁFICAS")
                }
            } else {
                Text(
                    "No hay datos suficientes. Comienza agregando ingresos y gastos.",
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth().padding(vertical = 24.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Secciones CRUD
        FinancialSections(
            uiState = uiState.value,
            finanzasViewModel = finanzasViewModel,
            onAddIngreso = { showIngresoDialog = true },
            onEditIngreso = { editingIngreso = it; showIngresoDialog = true },
            onDeleteIngreso = { finanzasViewModel.deleteIngreso(it) },
            onAddGastoFijo = { showGastoFijoDialog = true },
            onEditGastoFijo = { editingGastoFijo = it; showGastoFijoDialog = true },
            onDeleteGastoFijo = { finanzasViewModel.deleteGastoFijo(it) },
            onAddGastoVariable = { showGastoVariableDialog = true },
            onEditGastoVariable = { editingGastoVariable = it; showGastoVariableDialog = true },
            onDeleteGastoVariable = { finanzasViewModel.deleteGastoVariable(it) },
            onAddDeuda = { showDeudaDialog = true },
            onEditDeuda = { editingDeuda = it; showDeudaDialog = true },
            onDeleteDeuda = { finanzasViewModel.deleteDeuda(it) },
            onAddMeta = { showMetaDialog = true },
            onEditMeta = { editingMeta = it; showMetaDialog = true },
            onDeleteMeta = { finanzasViewModel.deleteMeta(it) },
            onNuevaEvaluacion = { showNuevaEvaluacionDialog = true },
            showIngresoDialog = showIngresoDialog,
            onDismissIngresoDialog = { showIngresoDialog = false; editingIngreso = null },
            editingIngreso = editingIngreso,
            showGastoFijoDialog = showGastoFijoDialog,
            onDismissGastoFijoDialog = { showGastoFijoDialog = false; editingGastoFijo = null },
            editingGastoFijo = editingGastoFijo,
            showGastoVariableDialog = showGastoVariableDialog,
            onDismissGastoVariableDialog = { showGastoVariableDialog = false; editingGastoVariable = null },
            editingGastoVariable = editingGastoVariable,
            showDeudaDialog = showDeudaDialog,
            onDismissDeudaDialog = { showDeudaDialog = false; editingDeuda = null },
            editingDeuda = editingDeuda,
            showMetaDialog = showMetaDialog,
            onDismissMetaDialog = { showMetaDialog = false; editingMeta = null },
            editingMeta = editingMeta,
            showNuevaEvaluacionDialog = showNuevaEvaluacionDialog,
            onDismissNuevaEvaluacionDialog = { showNuevaEvaluacionDialog = false }
        )
    }
}

@Composable
fun ProgressBar() {
    CircularProgressIndicator(
        modifier = Modifier.size(24.dp),
        color = MaterialTheme.colorScheme.primary,
        strokeWidth = 2.dp
    )
}

@Composable
fun FinancialSections(
    uiState: com.finanzaspersonales.gt.viewmodel.FinanzasUiState,
    finanzasViewModel: FinanzasViewModel,
    onAddIngreso: () -> Unit,
    onEditIngreso: (Ingreso) -> Unit,
    onDeleteIngreso: (Ingreso) -> Unit,
    onAddGastoFijo: () -> Unit,
    onEditGastoFijo: (GastoFijo) -> Unit,
    onDeleteGastoFijo: (GastoFijo) -> Unit,
    onAddGastoVariable: () -> Unit,
    onEditGastoVariable: (GastoVariable) -> Unit,
    onDeleteGastoVariable: (GastoVariable) -> Unit,
    onAddDeuda: () -> Unit,
    onEditDeuda: (Deuda) -> Unit,
    onDeleteDeuda: (Deuda) -> Unit,
    onAddMeta: () -> Unit,
    onEditMeta: (com.finanzaspersonales.gt.data.local.entity.MetaAhorro) -> Unit,
    onDeleteMeta: (com.finanzaspersonales.gt.data.local.entity.MetaAhorro) -> Unit,
    onNuevaEvaluacion: () -> Unit,
    showIngresoDialog: Boolean,
    onDismissIngresoDialog: () -> Unit,
    editingIngreso: Ingreso?,
    showGastoFijoDialog: Boolean,
    onDismissGastoFijoDialog: () -> Unit,
    editingGastoFijo: GastoFijo?,
    showGastoVariableDialog: Boolean,
    onDismissGastoVariableDialog: () -> Unit,
    editingGastoVariable: GastoVariable?,
    showDeudaDialog: Boolean,
    onDismissDeudaDialog: () -> Unit,
    editingDeuda: Deuda?,
    showMetaDialog: Boolean,
    onDismissMetaDialog: () -> Unit,
    editingMeta: com.finanzaspersonales.gt.data.local.entity.MetaAhorro?,
    showNuevaEvaluacionDialog: Boolean,
    onDismissNuevaEvaluacionDialog: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        IngresoSection(
            ingresos = uiState.ingresos,
            onAdd = onAddIngreso,
            onEdit = onEditIngreso,
            onDelete = onDeleteIngreso,
            formatter = finanzasViewModel::formatCurrency
        )

        GastoFijoSection(
            gastos = uiState.gastosFijos,
            onAdd = onAddGastoFijo,
            onEdit = onEditGastoFijo,
            onDelete = onDeleteGastoFijo,
            formatter = finanzasViewModel::formatCurrency
        )

        GastoVariableSection(
            gastos = uiState.gastosVariables,
            onAdd = onAddGastoVariable,
            onEdit = onEditGastoVariable,
            onDelete = onDeleteGastoVariable,
            formatter = finanzasViewModel::formatCurrency
        )

        DeudaSection(
            deudas = uiState.deudas,
            onAdd = onAddDeuda,
            onEdit = onEditDeuda,
            onDelete = onDeleteDeuda,
            formatter = finanzasViewModel::formatCurrency
        )

        MetaAhorroSection(
            metas = uiState.metas,
            onAdd = onAddMeta,
            onEdit = onEditMeta,
            onDelete = onDeleteMeta,
            formatter = finanzasViewModel::formatCurrency
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onNuevaEvaluacion,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
        ) {
            Text("NUEVA EVALUACIÓN")
        }
    }

    // Diálogos
    if (showIngresoDialog) {
        IngresoDialog(
            onDismiss = onDismissIngresoDialog,
            onConfirm = { name, amount, periodicity ->
                val ingreso = Ingreso(
                    id = editingIngreso?.id ?: 0,
                    userId = 0, // Se asignará en el ViewModel
                    nombre = name,
                    cantidad = amount,
                    periodicidad = periodicity
                )
                if (editingIngreso != null) {
                    finanzasViewModel.updateIngreso(ingreso) { saved -> if (saved) onDismissIngresoDialog() }
                } else {
                    finanzasViewModel.insertIngreso(ingreso) { saved -> if (saved) onDismissIngresoDialog() }
                }
            },
            initialName = editingIngreso?.nombre ?: "",
            initialAmount = editingIngreso?.cantidad?.toString() ?: "",
            initialPeriodicity = editingIngreso?.periodicidad ?: "Mensual",
            isEditing = editingIngreso != null
        )
    }

    if (showGastoFijoDialog) {
        GastoFijoDialog(
            onDismiss = onDismissGastoFijoDialog,
            onConfirm = { name, category, amount, periodicity ->
                val gasto = GastoFijo(
                    id = editingGastoFijo?.id ?: 0,
                    userId = 0,
                    nombre = name,
                    categoria = category,
                    cantidad = amount,
                    periodicidad = periodicity
                )
                if (editingGastoFijo != null) {
                    finanzasViewModel.updateGastoFijo(gasto) { saved -> if (saved) onDismissGastoFijoDialog() }
                } else {
                    finanzasViewModel.insertGastoFijo(gasto) { saved -> if (saved) onDismissGastoFijoDialog() }
                }
            },
            initialName = editingGastoFijo?.nombre ?: "",
            initialCategory = editingGastoFijo?.categoria ?: "Alimentación",
            existingCategories = (uiState.gastosFijos.map { it.categoria } + uiState.gastosVariables.map { it.categoria })
                .filterNot { it.equals(editingGastoFijo?.categoria, ignoreCase = true) },
            initialAmount = editingGastoFijo?.cantidad?.toString() ?: "",
            initialPeriodicity = editingGastoFijo?.periodicidad ?: "Mensual",
            isEditing = editingGastoFijo != null
        )
    }

    if (showGastoVariableDialog) {
        GastoVariableDialog(
            onDismiss = onDismissGastoVariableDialog,
            onConfirm = { name, category, amount, periodicity ->
                val gasto = GastoVariable(
                    id = editingGastoVariable?.id ?: 0,
                    userId = 0,
                    nombre = name,
                    categoria = category,
                    cantidad = amount,
                    periodicidad = periodicity
                )
                if (editingGastoVariable != null) {
                    finanzasViewModel.updateGastoVariable(gasto) { saved -> if (saved) onDismissGastoVariableDialog() }
                } else {
                    finanzasViewModel.insertGastoVariable(gasto) { saved -> if (saved) onDismissGastoVariableDialog() }
                }
            },
            initialName = editingGastoVariable?.nombre ?: "",
            initialCategory = editingGastoVariable?.categoria ?: "Entretenimiento",
            existingCategories = (uiState.gastosFijos.map { it.categoria } + uiState.gastosVariables.map { it.categoria })
                .filterNot { it.equals(editingGastoVariable?.categoria, ignoreCase = true) },
            initialAmount = editingGastoVariable?.cantidad?.toString() ?: "",
            initialPeriodicity = editingGastoVariable?.periodicidad ?: "Mensual",
            isEditing = editingGastoVariable != null
        )
    }

    if (showDeudaDialog) {
        DeudaDialog(
            onDismiss = onDismissDeudaDialog,
            onConfirm = { name, montoTotal, pagoPeriodico, periodicity ->
                val deuda = Deuda(
                    id = editingDeuda?.id ?: 0,
                    userId = 0,
                    nombre = name,
                    montoTotal = montoTotal,
                    pagoPeriodico = pagoPeriodico,
                    periodicidad = periodicity
                )
                if (editingDeuda != null) {
                    finanzasViewModel.updateDeuda(deuda) { saved -> if (saved) onDismissDeudaDialog() }
                } else {
                    finanzasViewModel.insertDeuda(deuda) { saved -> if (saved) onDismissDeudaDialog() }
                }
            },
            initialName = editingDeuda?.nombre ?: "",
            initialMontoTotal = editingDeuda?.montoTotal?.toString() ?: "",
            initialPagoPeriodico = editingDeuda?.pagoPeriodico?.toString() ?: "",
            initialPeriodicity = editingDeuda?.periodicidad ?: "Mensual",
            isEditing = editingDeuda != null
        )
    }

    if (showMetaDialog) {
        MetaAhorroDialog(
            onDismiss = onDismissMetaDialog,
            onConfirm = { name, objetivo, ahorrado, aporte ->
                val meta = com.finanzaspersonales.gt.data.local.entity.MetaAhorro(
                    id = editingMeta?.id ?: 0,
                    userId = 0,
                    nombre = name,
                    cantidadObjetivo = objetivo,
                    cantidadAhorrada = ahorrado,
                    aporteMensual = aporte
                )
                if (editingMeta != null) {
                    finanzasViewModel.updateMeta(meta) { saved -> if (saved) onDismissMetaDialog() }
                } else {
                    finanzasViewModel.insertMeta(meta) { saved -> if (saved) onDismissMetaDialog() }
                }
            },
            initialName = editingMeta?.nombre ?: "",
            initialObjetivo = editingMeta?.cantidadObjetivo?.toString() ?: "",
            initialAhorrado = editingMeta?.cantidadAhorrada?.toString() ?: "",
            initialAporte = editingMeta?.aporteMensual?.toString() ?: "",
            isEditing = editingMeta != null
        )
    }

    if (showNuevaEvaluacionDialog) {
        ConfirmDialog(
            onDismiss = onDismissNuevaEvaluacionDialog,
            onConfirm = { finanzasViewModel.nuevaEvaluacion() },
            title = "Nueva Evaluación",
            message = "¿Deseas comenzar una nueva evaluación? Se eliminarán todos tus datos financieros actuales.",
            confirmText = "Confirmar",
            confirmColor = MaterialTheme.colorScheme.error
        )
    }
}
