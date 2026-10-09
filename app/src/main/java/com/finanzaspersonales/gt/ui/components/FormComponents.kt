package com.finanzaspersonales.gt.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

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
    supportingText: (@Composable () -> Unit)? = null
) {
    Column(modifier = modifier) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = { Text(label) },
            singleLine = singleLine,
            modifier = Modifier.fillMaxWidth(),
            trailingIcon = trailingIcon,
            supportingText = supportingText,
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
            val filtered = newValue.text.filter { it == '.' || it == ',' || it.isDigit() }
            val normalized = filtered.replace(',', '.')
            val parts = normalized.split('.')
            val result = if (parts.size > 2) {
                parts[0] + '.' + parts.drop(1).joinToString("")
            } else {
                normalized
            }
            onValueChange(TextFieldValue(result))
        },
        label = label,
        placeholder = "0.00",
        modifier = modifier,
        isError = isError,
        errorText = errorText
    )
}

@Composable
fun PeriodicityDropdown(
    selectedPeriodicity: String,
    onPeriodicityChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val periodicities = listOf("Semanal", "Quincenal", "Mensual")
    MenuDropdown(
        selectedValue = selectedPeriodicity,
        options = periodicities,
        onOptionClick = onPeriodicityChange,
        modifier = modifier
    )
}

@Composable
fun CategoryDropdown(
    selectedCategory: String,
    categories: List<String>,
    onCategoryChange: (String) -> Unit,
    allowCustom: Boolean = true,
    modifier: Modifier = Modifier
) {
    val options = if (allowCustom) {
        categories + "Otra..."
    } else {
        categories
    }
    MenuDropdown(
        selectedValue = selectedCategory,
        options = options,
        onOptionClick = { selected ->
            if (selected == "Otra...") {
                // TODO: Show dialog for custom category
            } else {
                onCategoryChange(selected)
            }
        },
        modifier = modifier
    )
}

@Composable
fun MenuDropdown(
    selectedValue: String,
    options: List<String>,
    onOptionClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    val textFieldValue = TextFieldValue(selectedValue)

    OutlinedTextField(
        value = textFieldValue,
        onValueChange = {},
        readOnly = true,
        label = { Text("Seleccionar") },
        modifier = modifier.fillMaxWidth(),
        trailingIcon = {
            IconButton(onClick = { expanded = !expanded }) {
                Icon(
                    imageVector = Icons.Default.ExpandMore,
                    contentDescription = if (expanded) "Contraer" else "Expandir"
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
                    text = { Text(option) },
                    onClick = {
                        onOptionClick(option)
                        expanded = false
                    }
                )
            }
        }
    }
}
