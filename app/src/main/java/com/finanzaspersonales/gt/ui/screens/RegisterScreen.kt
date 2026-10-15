package com.finanzaspersonales.gt.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowDropDown
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.finanzaspersonales.gt.R
import com.finanzaspersonales.gt.viewmodel.AuthViewModel
import com.finanzaspersonales.gt.viewmodel.AuthState
import com.finanzaspersonales.gt.ui.components.IpistoLogo
import com.finanzaspersonales.gt.ui.components.ProgressBar
import com.finanzaspersonales.gt.utils.PreferencesHelper

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    navController: NavHostController,
    authViewModel: AuthViewModel
) {
    val predefinedQuestions = listOf(
        stringResource(R.string.recovery_question_pet),
        stringResource(R.string.recovery_question_city),
        stringResource(R.string.recovery_question_food),
        stringResource(R.string.recovery_question_school),
        stringResource(R.string.recovery_question_custom)
    )

    var username by remember { mutableStateOf(TextFieldValue("")) }
    var password by remember { mutableStateOf(TextFieldValue("")) }
    var confirmPassword by remember { mutableStateOf(TextFieldValue("")) }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    var selectedQuestion by remember { mutableStateOf(predefinedQuestions[0]) }
    var customQuestion by remember { mutableStateOf(TextFieldValue("")) }
    var securityAnswer by remember { mutableStateOf(TextFieldValue("")) }
    var dropdownExpanded by remember { mutableStateOf(false) }

    val authState by authViewModel.authState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .imePadding()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        IpistoLogo(
            size = 72,
            tintColor = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = stringResource(R.string.auth_register),
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text(stringResource(R.string.auth_username)) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            supportingText = {
                Text(stringResource(R.string.msg_username_format))
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
            )
        )
        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text(stringResource(R.string.auth_password)) },
            singleLine = true,
            visualTransformation = if (passwordVisible) androidx.compose.ui.text.input.VisualTransformation.None else PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            supportingText = { Text(stringResource(R.string.msg_password_min_length)) },
            trailingIcon = {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        imageVector = if (passwordVisible) Icons.Outlined.VisibilityOff else Icons.Outlined.Visibility,
                        contentDescription = if (passwordVisible) stringResource(R.string.auth_hide_password) else stringResource(R.string.auth_show_password)
                    )
                }
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
            )
        )
        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = { Text(stringResource(R.string.auth_confirm_password)) },
            singleLine = true,
            visualTransformation = if (confirmPasswordVisible) androidx.compose.ui.text.input.VisualTransformation.None else PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            trailingIcon = {
                IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                    Icon(
                        imageVector = if (confirmPasswordVisible) Icons.Outlined.VisibilityOff else Icons.Outlined.Visibility,
                        contentDescription = if (confirmPasswordVisible) stringResource(R.string.auth_hide_password) else stringResource(R.string.auth_show_password)
                    )
                }
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
            )
        )
        Spacer(modifier = Modifier.height(20.dp))

        HorizontalDivider(modifier = Modifier.fillMaxWidth(), color = MaterialTheme.colorScheme.outlineVariant)
        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = stringResource(R.string.recovery_configure_question),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        ExposedDropdownMenuBox(
            expanded = dropdownExpanded,
            onExpandedChange = { dropdownExpanded = !dropdownExpanded },
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                value = selectedQuestion,
                onValueChange = {},
                readOnly = true,
                label = { Text(stringResource(R.string.recovery_security_question)) },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = dropdownExpanded)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
            )
            ExposedDropdownMenu(
                expanded = dropdownExpanded,
                onDismissRequest = { dropdownExpanded = false }
            ) {
                predefinedQuestions.forEach { question ->
                    DropdownMenuItem(
                        text = { Text(question) },
                        onClick = {
                            selectedQuestion = question
                            dropdownExpanded = false
                        }
                    )
                }
            }
        }

        if (selectedQuestion == stringResource(R.string.recovery_question_custom)) {
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedTextField(
                value = customQuestion,
                onValueChange = { customQuestion = it },
                label = { Text(stringResource(R.string.recovery_write_question)) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = securityAnswer,
            onValueChange = { securityAnswer = it },
            label = { Text(stringResource(R.string.recovery_secret_answer)) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            supportingText = { Text(stringResource(R.string.msg_security_question_usage)) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
            )
        )

        if (authState is AuthState.Error) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = (authState as AuthState.Error).message,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        val customQuestionStr = stringResource(R.string.recovery_question_custom)
        val registerStr = stringResource(R.string.auth_register)
        val hasAccountLoginStr = stringResource(R.string.auth_has_account_login)

        Button(
            onClick = {
                val finalQuestion = if (selectedQuestion == customQuestionStr) customQuestion.text.trim() else selectedQuestion
                authViewModel.register(
                    username.text,
                    password.text,
                    confirmPassword.text,
                    finalQuestion,
                    securityAnswer.text
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            enabled = authState !is AuthState.Loading,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            )
        ) {
            if (authState is AuthState.Loading) {
                ProgressBar()
            } else {
                Text(registerStr, style = MaterialTheme.typography.labelLarge)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextButton(
            onClick = {
                if (!navController.popBackStack()) {
                    navController.navigate("welcome")
                }
            }
        ) {
            Text(
                text = hasAccountLoginStr,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}
