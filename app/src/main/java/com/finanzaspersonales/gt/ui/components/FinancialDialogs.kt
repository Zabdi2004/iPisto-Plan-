package com.finanzaspersonales.gt.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.finanzaspersonales.gt.utils.MoneyInput

@Composable
fun IngresoDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, Double, String) -> Unit,
    initialName: String = "",
    initialAmount: String = "",
    initialPeriodicity: String = "Mensual",
    isEditing: Boolean = false
) {
    var name by remember { mutableStateOf(TextFieldValue(initialName)) }
    var amount by remember { mutableStateOf(TextFieldValue(initialAmount)) }
    var periodicity by remember { mutableStateOf(initialPeriodicity) }
    var nameError by remember { mutableStateOf(false) }
    var amountError by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (isEditing) "Editar Ingreso" else "Agregar Ingreso", style = MaterialTheme.typography.headlineSmall) },
        text = {
            Column(
                modifier = Modifier.padding(24.dp).width(320.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CustomTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = "Nombre",
                    placeholder = "Ej: Salario",
                    isError = nameError,
                    errorText = if (nameError) "El nombre es obligatorio" else null,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))

                AmountTextField(
                    value = amount,
                    onValueChange = { amount = it },
                    label = "Cantidad (Q)",
                    isError = amountError,
                    errorText = if (amountError) "Ingrese una cantidad válida mayor a 0" else null,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))

                PeriodicityDropdown(
                    selectedPeriodicity = periodicity,
                    onPeriodicityChange = { periodicity = it },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    val amountValue = MoneyInput.parseAmount(amount.text) ?: 0.0
                    val valid = name.text.isNotBlank() && amountValue.isFinite() && amountValue > 0
                    nameError = name.text.isBlank()
                    amountError = !amountValue.isFinite() || amountValue <= 0
                    if (valid) {
                        onConfirm(name.text, amountValue, periodicity)
                        onDismiss()
                    }
                }
            ) { Text("Guardar") }
        }
    )
}

@Composable
fun GastoFijoDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, String, Double, String) -> Unit,
    initialName: String = "",
    initialCategory: String = "Alimentación",
    existingCategories: List<String> = emptyList(),
    initialAmount: String = "",
    initialPeriodicity: String = "Mensual",
    isEditing: Boolean = false
) {
    var name by remember { mutableStateOf(TextFieldValue(initialName)) }
    var category by remember { mutableStateOf(initialCategory) }
    var amount by remember { mutableStateOf(TextFieldValue(initialAmount)) }
    var periodicity by remember { mutableStateOf(initialPeriodicity) }
    var nameError by remember { mutableStateOf(false) }
    var amountError by remember { mutableStateOf(false) }

    val categories = (listOf("Electricidad", "Agua", "Alquiler", "Alimentación", "Transporte", "Internet", "Teléfono", "Seguros", "Otro") + existingCategories)
        .distinctBy { it.trim().lowercase() }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (isEditing) "Editar Gasto Fijo" else "Agregar Gasto Fijo", style = MaterialTheme.typography.headlineSmall) },
        text = {
            Column(
                modifier = Modifier.padding(24.dp).width(320.dp)
            ) {
                CustomTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = "Nombre",
                    placeholder = "Ej: Alquiler apartamento",
                    isError = nameError,
                    errorText = if (nameError) "El nombre es obligatorio" else null,
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
                    label = "Cantidad (Q)",
                    isError = amountError,
                    errorText = if (amountError) "Ingrese una cantidad válida mayor a 0" else null,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))

                PeriodicityDropdown(
                    selectedPeriodicity = periodicity,
                    onPeriodicityChange = { periodicity = it },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    val amountValue = MoneyInput.parseAmount(amount.text) ?: 0.0
                    val valid = name.text.isNotBlank() && amountValue.isFinite() && amountValue > 0
                    nameError = name.text.isBlank()
                    amountError = !amountValue.isFinite() || amountValue <= 0
                    if (valid) {
                        onConfirm(name.text, category, amountValue, periodicity)
                        onDismiss()
                    }
                }
            ) { Text("Guardar") }
        }
    )
}

@Composable
fun GastoVariableDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, String, Double, String) -> Unit,
    initialName: String = "",
    initialCategory: String = "Entretenimiento",
    existingCategories: List<String> = emptyList(),
    initialAmount: String = "",
    initialPeriodicity: String = "Mensual",
    isEditing: Boolean = false
) {
    var name by remember { mutableStateOf(TextFieldValue(initialName)) }
    var category by remember { mutableStateOf(initialCategory) }
    var amount by remember { mutableStateOf(TextFieldValue(initialAmount)) }
    var periodicity by remember { mutableStateOf(initialPeriodicity) }
    var nameError by remember { mutableStateOf(false) }
    var amountError by remember { mutableStateOf(false) }

    val categories = (listOf("Entretenimiento", "Transporte", "Higiene", "Salud", "Ropa", "Educación", "Regalos", "Otro") + existingCategories)
        .distinctBy { it.trim().lowercase() }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (isEditing) "Editar Gasto Variable" else "Agregar Gasto Variable", style = MaterialTheme.typography.headlineSmall) },
        text = {
            Column(
                modifier = Modifier.padding(24.dp).width(320.dp)
            ) {
                CustomTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = "Nombre",
                    placeholder = "Ej: Cine",
                    isError = nameError,
                    errorText = if (nameError) "El nombre es obligatorio" else null,
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
                    label = "Cantidad (Q)",
                    isError = amountError,
                    errorText = if (amountError) "Ingrese una cantidad válida mayor a 0" else null,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))

                PeriodicityDropdown(
                    selectedPeriodicity = periodicity,
                    onPeriodicityChange = { periodicity = it },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    val amountValue = MoneyInput.parseAmount(amount.text) ?: 0.0
                    val valid = name.text.isNotBlank() && amountValue.isFinite() && amountValue > 0
                    nameError = name.text.isBlank()
                    amountError = !amountValue.isFinite() || amountValue <= 0
                    if (valid) {
                        onConfirm(name.text, category, amountValue, periodicity)
                        onDismiss()
                    }
                }
            ) { Text("Guardar") }
        }
    )
}

@Composable
fun DeudaDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, Double, Double, String) -> Unit,
    initialName: String = "",
    initialMontoTotal: String = "",
    initialPagoPeriodico: String = "",
    initialPeriodicity: String = "Mensual",
    isEditing: Boolean = false
) {
    var name by remember { mutableStateOf(TextFieldValue(initialName)) }
    var montoTotal by remember { mutableStateOf(TextFieldValue(initialMontoTotal)) }
    var pagoPeriodico by remember { mutableStateOf(TextFieldValue(initialPagoPeriodico)) }
    var periodicity by remember { mutableStateOf(initialPeriodicity) }
    var nameError by remember { mutableStateOf(false) }
    var montoError by remember { mutableStateOf(false) }
    var pagoError by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (isEditing) "Editar Deuda" else "Agregar Deuda", style = MaterialTheme.typography.headlineSmall) },
        text = {
            Column(
                modifier = Modifier.padding(24.dp).width(320.dp)
            ) {
                CustomTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = "Nombre",
                    placeholder = "Ej: Tarjeta de crédito",
                    isError = nameError,
                    errorText = if (nameError) "El nombre es obligatorio" else null,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))

                AmountTextField(
                    value = montoTotal,
                    onValueChange = { montoTotal = it },
                    label = "Monto total (Q)",
                    isError = montoError,
                    errorText = if (montoError) "Ingrese un monto válido mayor a 0" else null,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))

                AmountTextField(
                    value = pagoPeriodico,
                    onValueChange = { pagoPeriodico = it },
                    label = "Pago periódico (Q)",
                    isError = pagoError,
                    errorText = if (pagoError) "Ingrese un pago válido mayor a 0" else null,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))

                PeriodicityDropdown(
                    selectedPeriodicity = periodicity,
                    onPeriodicityChange = { periodicity = it },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    val monto = MoneyInput.parseAmount(montoTotal.text) ?: 0.0
                    val pago = MoneyInput.parseAmount(pagoPeriodico.text) ?: 0.0
                    val valid = name.text.isNotBlank() && monto.isFinite() && monto > 0 && pago.isFinite() && pago > 0
                    nameError = name.text.isBlank()
                    montoError = !monto.isFinite() || monto <= 0
                    pagoError = !pago.isFinite() || pago <= 0
                    if (valid) {
                        onConfirm(name.text, monto, pago, periodicity)
                        onDismiss()
                    }
                }
            ) { Text("Guardar") }
        }
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
    isEditing: Boolean = false
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
        title = { Text(if (isEditing) "Editar Meta" else "Agregar Meta de Ahorro", style = MaterialTheme.typography.headlineSmall) },
        text = {
            Column(
                modifier = Modifier.padding(24.dp).width(320.dp)
            ) {
                CustomTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = "Nombre de la meta",
                    placeholder = "Ej: Comprar computadora",
                    isError = nameError,
                    errorText = if (nameError) "El nombre es obligatorio" else null,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))

                AmountTextField(
                    value = objetivo,
                    onValueChange = { objetivo = it },
                    label = "Cantidad objetivo (Q)",
                    isError = objetivoError,
                    errorText = if (objetivoError) "Ingrese una cantidad mayor a 0" else null,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))

                AmountTextField(
                    value = ahorrado,
                    onValueChange = { ahorrado = it },
                    label = "Ya ahorrado (Q)",
                    isError = ahorradoError,
                    errorText = if (ahorradoError) "No puede ser negativo" else null,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))

                AmountTextField(
                    value = aporte,
                    onValueChange = { aporte = it },
                    label = "Aporte mensual (Q)",
                    isError = aporteError,
                    errorText = if (aporteError) "El aporte debe ser finito y no negativo" else null,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    val obj = MoneyInput.parseAmount(objetivo.text) ?: 0.0
                    val ahr = MoneyInput.parseAmount(ahorrado.text) ?: 0.0
                    val apr = MoneyInput.parseAmount(aporte.text) ?: 0.0
                    val valid = name.text.isNotBlank() && obj.isFinite() && obj > 0 && ahr.isFinite() && ahr >= 0 && apr.isFinite() && apr >= 0
                    nameError = name.text.isBlank()
                    objetivoError = !obj.isFinite() || obj <= 0
                    ahorradoError = !ahr.isFinite() || ahr < 0
                    aporteError = !apr.isFinite() || apr < 0
                    if (valid) {
                        onConfirm(name.text, obj, ahr, apr)
                        onDismiss()
                    }
                }
            ) { Text("Guardar") }
        }
    )
}


