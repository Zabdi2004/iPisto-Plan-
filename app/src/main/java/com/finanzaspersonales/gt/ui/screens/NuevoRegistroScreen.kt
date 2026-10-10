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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.finanzaspersonales.gt.data.local.entity.GastoFijo
import com.finanzaspersonales.gt.data.local.entity.GastoVariable
import com.finanzaspersonales.gt.data.local.entity.Ingreso
import com.finanzaspersonales.gt.ui.components.AmountTextField
import com.finanzaspersonales.gt.ui.components.CategoryDropdown
import com.finanzaspersonales.gt.ui.components.CustomTextField
import com.finanzaspersonales.gt.ui.components.FoxMascot
import com.finanzaspersonales.gt.ui.components.PeriodicityDropdown
import com.finanzaspersonales.gt.utils.MoneyInput
import com.finanzaspersonales.gt.viewmodel.FinanzasViewModel

@Composable
fun NuevoRegistroScreen(navController: NavHostController, viewModel: FinanzasViewModel) {
    val state by viewModel.uiState.collectAsState()
    var kind by remember { mutableStateOf(EntryKind.EXPENSE) }
    var expenseKind by remember { mutableStateOf(ExpenseKind.VARIABLE) }
    var name by remember { mutableStateOf(TextFieldValue("")) }
    var amount by remember { mutableStateOf(TextFieldValue("")) }
    var category by remember { mutableStateOf("Comida") }
    var periodicity by remember { mutableStateOf("Mensual") }
    var nameError by remember { mutableStateOf(false) }
    var amountError by remember { mutableStateOf(false) }
    var saveSucceeded by remember { mutableStateOf(false) }

    val savedExpenseCategories = (state.gastosFijos.map { it.categoria } + state.gastosVariables.map { it.categoria }).distinct()
    val expenseCategories = listOf("Comida", "Bebidas", "Transporte", "Hogar", "Social", "Teléfono", "Servicios", "Educación", "Salud", "Otros")

    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(Icons.Default.Close, contentDescription = "Cerrar nuevo registro")
            }
            Text("Nuevo registro", style = MaterialTheme.typography.headlineSmall, modifier = Modifier.weight(1f))
            FoxMascot(size = 48.dp)
        }

        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterChip(selected = kind == EntryKind.EXPENSE, onClick = { kind = EntryKind.EXPENSE }, label = { Text("Gasto") })
            FilterChip(selected = kind == EntryKind.INCOME, onClick = { kind = EntryKind.INCOME }, label = { Text("Ingreso") })
            FilterChip(selected = false, onClick = {}, enabled = false, label = { Text("Transferencia") })
        }

        if (kind == EntryKind.EXPENSE) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(
                    selected = expenseKind == ExpenseKind.FIXED,
                    onClick = { expenseKind = ExpenseKind.FIXED; category = "Hogar" },
                    label = { Text("Gasto fijo") }
                )
                FilterChip(
                    selected = expenseKind == ExpenseKind.VARIABLE,
                    onClick = { expenseKind = ExpenseKind.VARIABLE; category = "Comida" },
                    label = { Text("Gasto variable") }
                )
            }
        }

        Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
            Column(Modifier.fillMaxWidth().padding(18.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                Text(
                    if (kind == EntryKind.INCOME) "Ingreso" else if (expenseKind == ExpenseKind.FIXED) "Gasto fijo" else "Gasto variable",
                    style = MaterialTheme.typography.titleMedium,
                    color = if (kind == EntryKind.INCOME) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.error
                )
                CustomTextField(
                    value = name,
                    onValueChange = { name = it; nameError = false },
                    label = "Descripción",
                    placeholder = if (kind == EntryKind.INCOME) "Ej. Salario" else "Ej. Compra semanal",
                    isError = nameError,
                    errorText = if (nameError) "Escribe una descripción." else null
                )
                if (kind == EntryKind.EXPENSE) {
                    CategoryDropdown(
                        selectedCategory = category,
                        categories = expenseCategories,
                        existingCategories = savedExpenseCategories,
                        onCategoryChange = { category = it }
                    )
                }
                AmountTextField(
                    value = amount,
                    onValueChange = { amount = it; amountError = false },
                    label = "Cantidad en quetzales (Q)",
                    isError = amountError,
                    errorText = if (amountError) "Ingresa un monto positivo hasta Q1,000,000,000,000." else null
                )
                PeriodicityDropdown(selectedPeriodicity = periodicity, onPeriodicityChange = { periodicity = it })
            }
        }

        if (state.error != null) {
            Text("No se pudo guardar: ${state.error}", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodyMedium)
        }
        Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
            Text(
                "Los registros actuales no almacenan fecha ni hora. La periodicidad se usa para calcular equivalentes mensuales.",
                modifier = Modifier.padding(16.dp),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Spacer(Modifier.height(4.dp))
        Button(
            onClick = {
                val parsed = MoneyInput.parseAmount(amount.text)
                nameError = name.text.isBlank()
                amountError = parsed == null || parsed <= 0.0
                if (!nameError && !amountError && parsed != null) {
                    val complete: (Boolean) -> Unit = { saved -> if (saved) saveSucceeded = true }
                    when (kind) {
                        EntryKind.INCOME -> viewModel.insertIngreso(Ingreso(userId = 0, nombre = name.text.trim(), cantidad = parsed, periodicidad = periodicity), complete)
                        EntryKind.EXPENSE -> if (expenseKind == ExpenseKind.FIXED) {
                            viewModel.insertGastoFijo(GastoFijo(userId = 0, nombre = name.text.trim(), categoria = category, cantidad = parsed, periodicidad = periodicity), complete)
                        } else {
                            viewModel.insertGastoVariable(GastoVariable(userId = 0, nombre = name.text.trim(), categoria = category, cantidad = parsed, periodicidad = periodicity), complete)
                        }
                    }
                }
            },
            modifier = Modifier.fillMaxWidth().height(54.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Icon(Icons.Default.Check, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text("Guardar registro")
        }
    }

    if (saveSucceeded) {
        AlertDialog(
            onDismissRequest = { navController.popBackStack() },
            title = { Text("Registro guardado") },
            text = { Text("Se guardó y los totales se actualizarán con tus datos.") },
            confirmButton = { TextButton(onClick = { navController.popBackStack() }) { Text("Listo") } }
        )
    }
}

private enum class EntryKind { EXPENSE, INCOME }
private enum class ExpenseKind { FIXED, VARIABLE }
