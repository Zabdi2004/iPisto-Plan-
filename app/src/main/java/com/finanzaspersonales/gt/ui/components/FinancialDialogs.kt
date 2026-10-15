package com.finanzaspersonales.gt.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.finanzaspersonales.gt.R
import com.finanzaspersonales.gt.utils.MoneyInput

@Composable
fun IngresoDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, Double, String, Long) -> Unit,
    initialName: String = "",
    initialAmount: String = "",
    initialPeriodicity: String = "Mensual",
    isEditing: Boolean = false,
    initialDateMillis: Long = System.currentTimeMillis(),
    saveError: String? = null
) {
    var name by remember { mutableStateOf(TextFieldValue(initialName)) }
    var amount by remember { mutableStateOf(TextFieldValue(initialAmount)) }
    var periodicity by remember { mutableStateOf(initialPeriodicity) }
    var dateMillis by remember(initialDateMillis) { mutableStateOf(initialDateMillis) }
    var nameError by remember { mutableStateOf(false) }
    var amountError by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (isEditing) stringResource(R.string.edit_income_dialog_title) else stringResource(R.string.add_income_dialog_title), style = MaterialTheme.typography.headlineSmall) },
        text = {
            Column(
                modifier = Modifier.padding(24.dp).widthIn(max = 320.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CustomTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = stringResource(R.string.income_name_label),
                    placeholder = stringResource(R.string.income_name_placeholder),
                    isError = nameError,
                    errorText = if (nameError) stringResource(R.string.income_name_required) else null,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))

                AmountTextField(
                    value = amount,
                    onValueChange = { amount = it },
                    label = stringResource(R.string.income_amount_label),
                    isError = amountError,
                    errorText = if (amountError) stringResource(R.string.income_amount_invalid) else null,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))

                PeriodicityDropdown(
                    selectedPeriodicity = periodicity,
                    onPeriodicityChange = { periodicity = it },
                    modifier = Modifier.fillMaxWidth()
                )
                if (periodicity == "Único") TransactionDateField(dateMillis, { dateMillis = it })
                saveError?.let { Text(stringResource(R.string.save_error, it), color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall) }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val parsedAmount = MoneyInput.parseAmount(amount.text)
                    val amountValue = parsedAmount ?: 0.0
                    val valid = name.text.isNotBlank() && parsedAmount != null && amountValue > 0
                    nameError = name.text.isBlank()
                    amountError = parsedAmount == null || amountValue <= 0
                    if (valid) {
                        onConfirm(name.text, amountValue, periodicity, dateMillis)
                    }
                }
            ) { Text(stringResource(R.string.save_button)) }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text(stringResource(R.string.cancel_button)) } }
    )
}

@Composable
fun GastoFijoDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, String, Double, String, Long) -> Unit,
    initialName: String = "",
    initialCategory: String = "Alimentación",
    existingCategories: List<String> = emptyList(),
    initialAmount: String = "",
    initialPeriodicity: String = "Mensual",
    isEditing: Boolean = false,
    initialDateMillis: Long = System.currentTimeMillis(),
    saveError: String? = null
) {
    var name by remember { mutableStateOf(TextFieldValue(initialName)) }
    var category by remember { mutableStateOf(initialCategory) }
    var amount by remember { mutableStateOf(TextFieldValue(initialAmount)) }
    var periodicity by remember { mutableStateOf(initialPeriodicity) }
    var dateMillis by remember(initialDateMillis) { mutableStateOf(initialDateMillis) }
    var nameError by remember { mutableStateOf(false) }
    var amountError by remember { mutableStateOf(false) }

    val categories = (listOf("Electricidad", "Agua", "Alquiler", "Alimentación", "Transporte", "Internet", "Teléfono", "Seguros", "Otro") + existingCategories)
        .distinctBy { it.trim().lowercase() }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (isEditing) stringResource(R.string.edit_fixed_expense_dialog_title) else stringResource(R.string.add_fixed_expense_dialog_title), style = MaterialTheme.typography.headlineSmall) },
        text = {
            Column(
                modifier = Modifier.padding(24.dp).widthIn(max = 320.dp)
            ) {
                CustomTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = stringResource(R.string.fixed_expense_name_label),
                    placeholder = stringResource(R.string.fixed_expense_name_placeholder),
                    isError = nameError,
                    errorText = if (nameError) stringResource(R.string.fixed_expense_name_required) else null,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))

                CategoryDropdown(
                    selectedCategory = category,
                    categories = categories,
                    onCategoryChange = { category = it },
                    existingCategories = existingCategories,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))

                AmountTextField(
                    value = amount,
                    onValueChange = { amount = it },
                    label = stringResource(R.string.fixed_expense_amount_label),
                    isError = amountError,
                    errorText = if (amountError) stringResource(R.string.fixed_expense_amount_invalid) else null,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))

                PeriodicityDropdown(
                    selectedPeriodicity = periodicity,
                    onPeriodicityChange = { periodicity = it },
                    modifier = Modifier.fillMaxWidth()
                )
                if (periodicity == "Único") TransactionDateField(dateMillis, { dateMillis = it })
                saveError?.let { Text(stringResource(R.string.save_error, it), color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall) }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val parsedAmount = MoneyInput.parseAmount(amount.text)
                    val amountValue = parsedAmount ?: 0.0
                    val valid = name.text.isNotBlank() && parsedAmount != null && amountValue > 0
                    nameError = name.text.isBlank()
                    amountError = parsedAmount == null || amountValue <= 0
                    if (valid) {
                        onConfirm(name.text, category, amountValue, periodicity, dateMillis)
                    }
                }
            ) { Text(stringResource(R.string.save_button)) }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text(stringResource(R.string.cancel_button)) } }
    )
}

@Composable
fun GastoVariableDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, String, Double, String, Long) -> Unit,
    initialName: String = "",
    initialCategory: String = "Entretenimiento",
    existingCategories: List<String> = emptyList(),
    initialAmount: String = "",
    initialPeriodicity: String = "Mensual",
    isEditing: Boolean = false,
    initialDateMillis: Long = System.currentTimeMillis(),
    saveError: String? = null
) {
    var name by remember { mutableStateOf(TextFieldValue(initialName)) }
    var category by remember { mutableStateOf(initialCategory) }
    var amount by remember { mutableStateOf(TextFieldValue(initialAmount)) }
    var periodicity by remember { mutableStateOf(initialPeriodicity) }
    var dateMillis by remember(initialDateMillis) { mutableStateOf(initialDateMillis) }
    var nameError by remember { mutableStateOf(false) }
    var amountError by remember { mutableStateOf(false) }

    val categories = (listOf("Entretenimiento", "Transporte", "Higiene", "Salud", "Ropa", "Educación", "Regalos", "Otro") + existingCategories)
        .distinctBy { it.trim().lowercase() }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (isEditing) stringResource(R.string.edit_variable_expense_dialog_title) else stringResource(R.string.add_variable_expense_dialog_title), style = MaterialTheme.typography.headlineSmall) },
        text = {
            Column(
                modifier = Modifier.padding(24.dp).widthIn(max = 320.dp)
            ) {
                CustomTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = stringResource(R.string.variable_expense_name_label),
                    placeholder = stringResource(R.string.variable_expense_name_placeholder),
                    isError = nameError,
                    errorText = if (nameError) stringResource(R.string.variable_expense_name_required) else null,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))

                CategoryDropdown(
                    selectedCategory = category,
                    categories = categories,
                    onCategoryChange = { category = it },
                    existingCategories = existingCategories,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))

                AmountTextField(
                    value = amount,
                    onValueChange = { amount = it },
                    label = stringResource(R.string.variable_expense_amount_label),
                    isError = amountError,
                    errorText = if (amountError) stringResource(R.string.variable_expense_amount_invalid) else null,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))

                PeriodicityDropdown(
                    selectedPeriodicity = periodicity,
                    onPeriodicityChange = { periodicity = it },
                    modifier = Modifier.fillMaxWidth()
                )
                if (periodicity == "Único") TransactionDateField(dateMillis, { dateMillis = it })
                saveError?.let { Text(stringResource(R.string.save_error, it), color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall) }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val parsedAmount = MoneyInput.parseAmount(amount.text)
                    val amountValue = parsedAmount ?: 0.0
                    val valid = name.text.isNotBlank() && parsedAmount != null && amountValue > 0
                    nameError = name.text.isBlank()
                    amountError = parsedAmount == null || amountValue <= 0
                    if (valid) {
                        onConfirm(name.text, category, amountValue, periodicity, dateMillis)
                    }
                }
            ) { Text(stringResource(R.string.save_button)) }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text(stringResource(R.string.cancel_button)) } }
    )
}

@Composable
fun DeudaDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, Double, Double, String, Long) -> Unit,
    initialName: String = "",
    initialMontoTotal: String = "",
    initialPagoPeriodico: String = "",
    initialPeriodicity: String = "Mensual",
    isEditing: Boolean = false,
    initialDateMillis: Long = System.currentTimeMillis(),
    saveError: String? = null
) {
    var name by remember { mutableStateOf(TextFieldValue(initialName)) }
    var montoTotal by remember { mutableStateOf(TextFieldValue(initialMontoTotal)) }
    var pagoPeriodico by remember { mutableStateOf(TextFieldValue(initialPagoPeriodico)) }
    var periodicity by remember { mutableStateOf(initialPeriodicity) }
    var dateMillis by remember(initialDateMillis) { mutableStateOf(initialDateMillis) }
    var nameError by remember { mutableStateOf(false) }
    var montoError by remember { mutableStateOf(false) }
    var pagoError by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (isEditing) stringResource(R.string.edit_debt_dialog_title) else stringResource(R.string.add_debt_dialog_title), style = MaterialTheme.typography.headlineSmall) },
        text = {
            Column(
                modifier = Modifier.padding(24.dp).widthIn(max = 320.dp)
            ) {
                CustomTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = stringResource(R.string.debt_name_label),
                    placeholder = stringResource(R.string.debt_name_placeholder),
                    isError = nameError,
                    errorText = if (nameError) stringResource(R.string.debt_name_required) else null,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))

                AmountTextField(
                    value = montoTotal,
                    onValueChange = { montoTotal = it },
                    label = stringResource(R.string.debt_total_amount_label),
                    isError = montoError,
                    errorText = if (montoError) stringResource(R.string.debt_total_amount_invalid) else null,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))

                AmountTextField(
                    value = pagoPeriodico,
                    onValueChange = { pagoPeriodico = it },
                    label = stringResource(R.string.debt_periodic_payment_label),
                    isError = pagoError,
                    errorText = if (pagoError) stringResource(R.string.debt_periodic_payment_invalid) else null,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))

                PeriodicityDropdown(
                    selectedPeriodicity = periodicity,
                    onPeriodicityChange = { periodicity = it },
                    modifier = Modifier.fillMaxWidth()
                )
                if (periodicity == "Único") TransactionDateField(dateMillis, { dateMillis = it })
                saveError?.let { Text(stringResource(R.string.save_error, it), color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall) }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val parsedMonto = MoneyInput.parseAmount(montoTotal.text)
                    val parsedPago = MoneyInput.parseAmount(pagoPeriodico.text)
                    val monto = parsedMonto ?: 0.0
                    val pago = parsedPago ?: 0.0
                    val valid = name.text.isNotBlank() && parsedMonto != null && monto > 0 && parsedPago != null && pago > 0
                    nameError = name.text.isBlank()
                    montoError = parsedMonto == null || monto <= 0
                    pagoError = parsedPago == null || pago <= 0
                    if (valid) {
                        onConfirm(name.text, monto, pago, periodicity, dateMillis)
                    }
                }
            ) { Text(stringResource(R.string.save_button)) }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text(stringResource(R.string.cancel_button)) } }
    )
}

@Composable
fun MetaAhorroDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, Double, Double, Double) -> Unit,
    initialName: String = "",
    initialObjetivo: String = "",
    initialAhorrado: String = "",
    initialAporte: String = "",
    isEditing: Boolean = false,
    saveError: String? = null
) {
    var name by remember { mutableStateOf(TextFieldValue(initialName)) }
    var objetivo by remember { mutableStateOf(TextFieldValue(initialObjetivo)) }
    var ahorrado by remember { mutableStateOf(TextFieldValue(initialAhorrado)) }
    var aporte by remember { mutableStateOf(TextFieldValue(initialAporte)) }
    var nameError by remember { mutableStateOf(false) }
    var objetivoError by remember { mutableStateOf(false) }
    var ahorradoError by remember { mutableStateOf(false) }
    var aporteError by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (isEditing) stringResource(R.string.edit_goal_dialog_title) else stringResource(R.string.add_goal_dialog_title), style = MaterialTheme.typography.headlineSmall) },
        text = {
            Column(
                modifier = Modifier.padding(24.dp).widthIn(max = 320.dp)
            ) {
                CustomTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = stringResource(R.string.goal_name_label),
                    placeholder = stringResource(R.string.goal_name_placeholder),
                    isError = nameError,
                    errorText = if (nameError) stringResource(R.string.goal_name_required) else null,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))

                AmountTextField(
                    value = objetivo,
                    onValueChange = { objetivo = it },
                    label = stringResource(R.string.goal_objective_label),
                    isError = objetivoError,
                    errorText = if (objetivoError) stringResource(R.string.goal_objective_invalid) else null,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))

                AmountTextField(
                    value = ahorrado,
                    onValueChange = { ahorrado = it },
                    label = stringResource(R.string.goal_saved_label),
                    isError = ahorradoError,
                    errorText = if (ahorradoError) stringResource(R.string.goal_saved_invalid) else null,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))

                AmountTextField(
                    value = aporte,
                    onValueChange = { aporte = it },
                    label = stringResource(R.string.goal_contribution_label),
                    isError = aporteError,
                    errorText = if (aporteError) stringResource(R.string.goal_contribution_invalid) else null,
                    modifier = Modifier.fillMaxWidth()
                )
                saveError?.let { Text(stringResource(R.string.save_error, it), color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall) }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val parsedObj = MoneyInput.parseAmount(objetivo.text)
                    val parsedAhr = MoneyInput.parseAmount(ahorrado.text)
                    val parsedApr = MoneyInput.parseAmount(aporte.text)
                    val obj = parsedObj ?: 0.0
                    val ahr = parsedAhr ?: 0.0
                    val apr = parsedApr ?: 0.0
                    val valid = name.text.isNotBlank() && parsedObj != null && obj > 0 && parsedAhr != null && ahr >= 0 && parsedApr != null && apr >= 0
                    nameError = name.text.isBlank()
                    objetivoError = parsedObj == null || obj <= 0
                    ahorradoError = parsedAhr == null || ahr < 0
                    aporteError = parsedApr == null || apr < 0
                    if (valid) {
                        onConfirm(name.text, obj, ahr, apr)
                    }
                }
            ) { Text(stringResource(R.string.save_button)) }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text(stringResource(R.string.cancel_button)) } }
    )
}


