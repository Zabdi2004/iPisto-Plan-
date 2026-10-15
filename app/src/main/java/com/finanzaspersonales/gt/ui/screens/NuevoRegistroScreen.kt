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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.finanzaspersonales.gt.R
import com.finanzaspersonales.gt.data.local.entity.Deuda
import com.finanzaspersonales.gt.data.local.entity.GastoFijo
import com.finanzaspersonales.gt.data.local.entity.GastoVariable
import com.finanzaspersonales.gt.data.local.entity.Ingreso
import com.finanzaspersonales.gt.ui.components.AmountTextField
import com.finanzaspersonales.gt.ui.components.CategoryDropdown
import com.finanzaspersonales.gt.ui.components.CustomTextField
import com.finanzaspersonales.gt.ui.components.QuetzalMascot
import com.finanzaspersonales.gt.ui.components.PeriodicityDropdown
import com.finanzaspersonales.gt.ui.components.TransactionDateField
import com.finanzaspersonales.gt.utils.MoneyInput
import com.finanzaspersonales.gt.viewmodel.FinanzasViewModel

@Composable
fun NuevoRegistroScreen(navController: NavHostController, viewModel: FinanzasViewModel) {
    val state by viewModel.uiState.collectAsState()
    var kind by remember { mutableStateOf(EntryKind.EXPENSE) }
    var expenseKind by remember { mutableStateOf(ExpenseKind.VARIABLE) }
    var name by remember { mutableStateOf(TextFieldValue("")) }
    var amount by remember { mutableStateOf(TextFieldValue("")) }
    var debtPayment by remember { mutableStateOf(TextFieldValue("")) }
    var category by remember { mutableStateOf("Comida") }
    var periodicity by remember { mutableStateOf("Mensual") }
    var occurrenceDate by remember { mutableStateOf(System.currentTimeMillis()) }
    var nameError by remember { mutableStateOf(false) }
    var amountError by remember { mutableStateOf(false) }
    var debtPaymentError by remember { mutableStateOf(false) }
    var isSaving by remember { mutableStateOf(false) }

    val savedExpenseCategories = (state.gastosFijos.map { it.categoria } + state.gastosVariables.map { it.categoria }).distinct()
    val expenseCategories = listOf("Comida", "Bebidas", "Transporte", "Hogar", "Social", "Teléfono", "Servicios", "Educación", "Salud", "Otros")

    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(Icons.Default.Close, contentDescription = stringResource(R.string.close_new_record))
            }
            Text(stringResource(R.string.new_record_title), style = MaterialTheme.typography.headlineSmall, modifier = Modifier.weight(1f))
            QuetzalMascot(size = 48.dp)
        }

        // Tipo de registro: Gasto, Ingreso o Deuda.
        Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterChip(selected = kind == EntryKind.EXPENSE, onClick = { kind = EntryKind.EXPENSE }, label = { Text(stringResource(R.string.expense_chip)) })
            FilterChip(selected = kind == EntryKind.INCOME, onClick = { kind = EntryKind.INCOME }, label = { Text(stringResource(R.string.income_chip)) })
            FilterChip(selected = kind == EntryKind.DEBT, onClick = { kind = EntryKind.DEBT }, label = { Text(stringResource(R.string.debt_chip)) })
        }

        if (kind == EntryKind.EXPENSE) {
            Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(
                    selected = expenseKind == ExpenseKind.FIXED,
                    onClick = { expenseKind = ExpenseKind.FIXED; category = "Hogar" },
                    label = { Text(stringResource(R.string.fixed_expense_chip)) }
                )
                FilterChip(
                    selected = expenseKind == ExpenseKind.VARIABLE,
                    onClick = { expenseKind = ExpenseKind.VARIABLE; category = "Comida" },
                    label = { Text(stringResource(R.string.variable_expense_chip)) }
                )
            }
        }

        Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
            Column(Modifier.fillMaxWidth().padding(18.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                Text(
                    when (kind) {
                        EntryKind.INCOME -> stringResource(R.string.income_section)
                        EntryKind.DEBT -> stringResource(R.string.debt_chip)
                        EntryKind.EXPENSE -> if (expenseKind == ExpenseKind.FIXED) stringResource(R.string.fixed_expense_section) else stringResource(R.string.variable_expense_section)
                    },
                    style = MaterialTheme.typography.titleMedium,
                    color = if (kind == EntryKind.INCOME) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.error
                )
                CustomTextField(
                    value = name,
                    onValueChange = { name = it; nameError = false },
                    label = if (kind == EntryKind.DEBT) stringResource(R.string.debt_description_label) else stringResource(R.string.description_label),
                    placeholder = when (kind) {
                        EntryKind.INCOME -> stringResource(R.string.salary_placeholder)
                        EntryKind.DEBT -> stringResource(R.string.debt_name_placeholder)
                        EntryKind.EXPENSE -> stringResource(R.string.weekly_purchase_placeholder)
                    },
                    isError = nameError,
                    errorText = if (nameError) stringResource(R.string.description_required) else null
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
                    label = if (kind == EntryKind.DEBT) stringResource(R.string.debt_total_amount_field_label) else stringResource(R.string.amount_label),
                    isError = amountError,
                    errorText = if (amountError) stringResource(R.string.amount_positive_limit) else null
                )
                if (kind == EntryKind.DEBT) {
                    AmountTextField(
                        value = debtPayment,
                        onValueChange = { debtPayment = it; debtPaymentError = false },
                        label = stringResource(R.string.debt_periodic_payment_field_label),
                        isError = debtPaymentError,
                        errorText = if (debtPaymentError) stringResource(R.string.payment_positive_limit) else null
                    )
                }
                PeriodicityDropdown(selectedPeriodicity = periodicity, onPeriodicityChange = { periodicity = it })
                if (periodicity == "Único") TransactionDateField(occurrenceDate, { occurrenceDate = it })
            }
        }

        if (state.error != null) {
            Text(stringResource(R.string.save_error, state.error!!), color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodyMedium)
        }
        Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
            Text(
                stringResource(R.string.unique_movements_note),
                modifier = Modifier.padding(16.dp),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Spacer(Modifier.height(4.dp))
        Button(
            enabled = !isSaving,
            onClick = {
                val parsed = MoneyInput.parseAmount(amount.text)
                val parsedPayment = if (kind == EntryKind.DEBT) MoneyInput.parseAmount(debtPayment.text) else null
                nameError = name.text.isBlank()
                amountError = parsed == null || parsed <= 0.0
                debtPaymentError = kind == EntryKind.DEBT && (parsedPayment == null || parsedPayment <= 0.0)
                if (!nameError && !amountError && !debtPaymentError && parsed != null) {
                    isSaving = true
                    val complete: (Boolean) -> Unit = { saved ->
                        isSaving = false
                        if (saved) navController.popBackStack()
                    }
                    when (kind) {
                        EntryKind.INCOME -> viewModel.insertIngreso(Ingreso(userId = 0, nombre = name.text.trim(), cantidad = parsed, periodicidad = periodicity, fechaMillis = occurrenceDate), complete)
                        EntryKind.DEBT -> viewModel.insertDeuda(
                            Deuda(
                                userId = 0,
                                nombre = name.text.trim(),
                                montoTotal = parsed,
                                pagoPeriodico = parsedPayment ?: 0.0,
                                periodicidad = periodicity,
                                fechaMillis = if (periodicity == "Único") occurrenceDate else null
                            ),
                            complete
                        )
                        EntryKind.EXPENSE -> if (expenseKind == ExpenseKind.FIXED) {
                            viewModel.insertGastoFijo(GastoFijo(userId = 0, nombre = name.text.trim(), categoria = category, cantidad = parsed, periodicidad = periodicity, fechaMillis = if (periodicity == "Único") occurrenceDate else null), complete)
                        } else {
                            viewModel.insertGastoVariable(GastoVariable(userId = 0, nombre = name.text.trim(), categoria = category, cantidad = parsed, periodicidad = periodicity, fechaMillis = if (periodicity == "Único") occurrenceDate else null), complete)
                        }
                    }
                }
            },
            modifier = Modifier.fillMaxWidth().height(54.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Icon(Icons.Default.Check, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text(if (isSaving) stringResource(R.string.saving_ellipsis) else stringResource(R.string.save_record))
        }
    }
}

private enum class EntryKind { EXPENSE, INCOME, DEBT }
private enum class ExpenseKind { FIXED, VARIABLE }
