package com.finanzaspersonales.gt.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.DateFormat
import java.util.Date
import com.finanzaspersonales.gt.R
import com.finanzaspersonales.gt.utils.MoneyInput
import com.finanzaspersonales.gt.utils.CategoryNameValidator

@Composable
fun CustomTextField(
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    label: String,
    placeholder: String = "",
    singleLine: Boolean = true,
    modifier: Modifier = Modifier,
    isError: Boolean = false,
    errorText: String? = null,
    trailingIcon: (@Composable () -> Unit)? = null,
    supportingText: (@Composable () -> Unit)? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default
) {
    Column(modifier = modifier) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = { Text(label) },
            placeholder = placeholder.takeIf { it.isNotEmpty() }?.let { text -> ({ Text(text) }) },
            singleLine = singleLine,
            modifier = Modifier.fillMaxWidth(),
            trailingIcon = trailingIcon,
            supportingText = supportingText,
            keyboardOptions = keyboardOptions,
            isError = isError,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = if (isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                errorContainerColor = MaterialTheme.colorScheme.errorContainer
            )
        )
        if (isError && errorText != null) {
            Text(
                text = errorText,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(top = 4.dp, start = 4.dp)
            )
        }
    }
}

@Composable
fun AmountTextField(
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    isError: Boolean = false,
    errorText: String? = null
) {
    CustomTextField(
        value = value,
        onValueChange = { newValue ->
            val normalized = MoneyInput.normalizeEditingValue(
                newValue.text,
                newValue.selection.start,
                newValue.selection.end
            )
            onValueChange(TextFieldValue(normalized.text, TextRange(normalized.selectionStart, normalized.selectionEnd)))
        },
        label = label,
        placeholder = "0.00",
        modifier = modifier,
        isError = isError,
        errorText = errorText,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
    )
}

@Composable
fun PeriodicityDropdown(
    selectedPeriodicity: String,
    onPeriodicityChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    // Los valores canónicos se conservan como dato; solo se localiza su presentación.
    val periodicities = listOf("Único", "Diario", "Semanal", "Quincenal", "Mensual")
    val labels = mapOf(
        "Único" to stringResource(R.string.periodicity_unique),
        "Diario" to stringResource(R.string.periodicity_daily),
        "Semanal" to stringResource(R.string.periodicity_weekly),
        "Quincenal" to stringResource(R.string.periodicity_biweekly),
        "Mensual" to stringResource(R.string.periodicity_monthly)
    )
    MenuDropdown(
        selectedValue = selectedPeriodicity,
        options = periodicities,
        onOptionClick = onPeriodicityChange,
        optionLabels = { labels[it] ?: it },
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionDateField(selectedDateMillis: Long, onDateSelected: (Long) -> Unit) {
    var showPicker by remember { mutableStateOf(false) }
    OutlinedButton(onClick = { showPicker = true }, modifier = Modifier.fillMaxWidth()) {
        val formatter = remember { DateFormat.getDateInstance().apply { timeZone = java.util.TimeZone.getTimeZone("UTC") } }
        Text(stringResource(R.string.date_field_label, formatter.format(Date(selectedDateMillis))))
    }
    if (showPicker) {
        val pickerState = rememberDatePickerState(initialSelectedDateMillis = selectedDateMillis)
        DatePickerDialog(
            onDismissRequest = { showPicker = false },
            confirmButton = {
                TextButton(onClick = {
                    pickerState.selectedDateMillis?.let(onDateSelected)
                    showPicker = false
                }) { Text(stringResource(R.string.choose_date_button)) }
            },
            dismissButton = { TextButton(onClick = { showPicker = false }) { Text(stringResource(R.string.cancel_button)) } }
        ) { DatePicker(state = pickerState) }
    }
}

@Composable
fun CategoryDropdown(
    selectedCategory: String,
    categories: List<String>,
    onCategoryChange: (String) -> Unit,
    allowCustom: Boolean = true,
    existingCategories: List<String> = emptyList(),
    modifier: Modifier = Modifier
) {
    var showCustomCategoryDialog by remember { mutableStateOf(false) }
    var customCategory by remember { mutableStateOf("") }
    var customCategoryError by remember { mutableStateOf(false) }
    var customCategoryErrorMessage by remember { mutableStateOf("") }
    val otherOption = stringResource(R.string.category_other_option)
    val emptyCategoryMessage = stringResource(R.string.category_name_empty)
    val duplicateCategoryMessage = stringResource(R.string.category_name_duplicate)
    val options = if (allowCustom) {
        (categories + existingCategories).distinctBy { it.trim().lowercase() } + otherOption
    } else {
        categories
    }
    MenuDropdown(
        selectedValue = selectedCategory,
        options = options,
        onOptionClick = { selected ->
            if (selected == otherOption) {
                customCategory = ""
                customCategoryError = false
                customCategoryErrorMessage = ""
                showCustomCategoryDialog = true
            } else {
                onCategoryChange(selected)
            }
        },
        modifier = modifier
    )

    if (showCustomCategoryDialog) {
        AlertDialog(
            onDismissRequest = { showCustomCategoryDialog = false },
            title = { Text(stringResource(R.string.new_category_dialog_title)) },
            text = {
                OutlinedTextField(
                    value = customCategory,
                    onValueChange = { customCategory = it; customCategoryError = false; customCategoryErrorMessage = "" },
                    label = { Text(stringResource(R.string.category_name_label)) },
                    singleLine = true,
                    isError = customCategoryError,
                    supportingText = if (customCategoryError) ({ Text(customCategoryErrorMessage) }) else null
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    val name = customCategory.trim()
                    when (CategoryNameValidator.validate(name, options.filterNot { it == otherOption })) {
                        CategoryNameValidator.Error.EMPTY -> {
                            customCategoryError = true
                            customCategoryErrorMessage = emptyCategoryMessage
                        }
                        CategoryNameValidator.Error.DUPLICATE -> {
                            customCategoryError = true
                            customCategoryErrorMessage = duplicateCategoryMessage
                        }
                        null -> {
                            onCategoryChange(name)
                            showCustomCategoryDialog = false
                        }
                    }
                }) { Text(stringResource(R.string.add_category_button)) }
            },
            dismissButton = {
                TextButton(onClick = { showCustomCategoryDialog = false }) { Text(stringResource(R.string.cancel_button)) }
            }
        )
    }
}

@Composable
fun MenuDropdown(
    selectedValue: String,
    options: List<String>,
    onOptionClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    optionLabels: (String) -> String = { it }
) {
    var expanded by remember { mutableStateOf(false) }
    val textFieldValue = TextFieldValue(optionLabels(selectedValue))

    OutlinedTextField(
        value = textFieldValue,
        onValueChange = {},
        readOnly = true,
        label = { Text(stringResource(R.string.select_category)) },
        modifier = modifier.fillMaxWidth(),
        trailingIcon = {
            IconButton(onClick = { expanded = !expanded }) {
                Icon(
                    imageVector = Icons.Default.ExpandMore,
                    contentDescription = if (expanded) stringResource(R.string.collapse_button) else stringResource(R.string.expand_button)
                )
            }
        },
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
        )
    )

    if (expanded) {
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.fillMaxWidth()
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(optionLabels(option)) },
                    onClick = {
                        onOptionClick(option)
                        expanded = false
                    }
                )
            }
        }
    }
}
