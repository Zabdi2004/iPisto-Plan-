package com.finanzaspersonales.gt.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.filled.Autorenew
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.finanzaspersonales.gt.R
import com.finanzaspersonales.gt.ui.components.ActionRow
import com.finanzaspersonales.gt.ui.components.ConfirmDialog
import com.finanzaspersonales.gt.viewmodel.AuthState
import com.finanzaspersonales.gt.viewmodel.AuthViewModel
import com.finanzaspersonales.gt.viewmodel.FinanzasViewModel

private val SecurityMaxWidth = 390.dp

/**
 * Ventana propia de "Seguridad y privacidad", con la misma estructura que Ajustes.
 * Agrupa cambiar contraseña, pregunta de seguridad, nueva evaluación y eliminar cuenta.
 */
@Composable
fun SeguridadScreen(
    navController: NavHostController,
    authViewModel: AuthViewModel,
    financeViewModel: FinanzasViewModel? = null
) {
    val authState by authViewModel.authState.collectAsState()
    val currentUser by authViewModel.currentUser.collectAsState()

    var showPasswordDialog by remember { mutableStateOf(false) }
    var showSecurityQuestionDialog by remember { mutableStateOf(false) }
    var questionInput by remember { mutableStateOf("") }
    var answerInput by remember { mutableStateOf("") }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var deleteRequested by remember { mutableStateOf(false) }
    var showNewEvaluationDialog by remember { mutableStateOf(false) }
    var oldPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    LaunchedEffect(authState, currentUser, showPasswordDialog, showSecurityQuestionDialog, deleteRequested) {
        if (authState == AuthState.Success && showPasswordDialog) {
            showPasswordDialog = false
            oldPassword = ""
            newPassword = ""
            confirmPassword = ""
            authViewModel.resetAuthState()
        }
        if (authState == AuthState.Success && showSecurityQuestionDialog) {
            showSecurityQuestionDialog = false
            questionInput = ""
            answerInput = ""
            authViewModel.resetAuthState()
        }
        if (deleteRequested && authState == AuthState.Idle && currentUser == null) {
            deleteRequested = false
            showDeleteDialog = false
            navController.navigate("welcome") { popUpTo(navController.graph.id) { inclusive = true }; launchSingleTop = true }
        }
        if (deleteRequested && authState is AuthState.Error) {
            deleteRequested = false
        }
    }

    val goBack: () -> Unit = {
        if (!navController.popBackStack()) {
            navController.navigate("perfil") { launchSingleTop = true }
        }
    }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
        Column(
            modifier = Modifier
                .widthIn(max = SecurityMaxWidth)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = goBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.action_back_arrow), tint = MaterialTheme.colorScheme.primary)
                }
                Spacer(Modifier.width(4.dp))
                Text(stringResource(R.string.security_privacy_title), fontSize = 20.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.primary)
            }
            Text(
                stringResource(R.string.security_privacy_subtitle),
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            ActionRow(Icons.Filled.Lock, stringResource(R.string.change_password), {
                oldPassword = ""
                newPassword = ""
                confirmPassword = ""
                authViewModel.resetAuthState()
                showPasswordDialog = true
            })
            ActionRow(Icons.AutoMirrored.Filled.HelpOutline, stringResource(R.string.security_question_button), {
                questionInput = currentUser?.preguntaSeguridad ?: ""
                answerInput = ""
                authViewModel.resetAuthState()
                showSecurityQuestionDialog = true
            })
            ActionRow(Icons.Filled.Autorenew, stringResource(R.string.new_evaluation_action), { showNewEvaluationDialog = true })
            ActionRow(Icons.Filled.Delete, stringResource(R.string.delete_account_button), { showDeleteDialog = true }, destructive = true)
        }
    }

    if (showPasswordDialog) AlertDialog(
        onDismissRequest = { if (authState != AuthState.Loading) showPasswordDialog = false },
        title = { Text(stringResource(R.string.change_password_dialog_title)) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(oldPassword, { oldPassword = it }, label = { Text(stringResource(R.string.current_password_label)) }, singleLine = true, visualTransformation = PasswordVisualTransformation())
                OutlinedTextField(newPassword, { newPassword = it }, label = { Text(stringResource(R.string.new_password_label)) }, singleLine = true, visualTransformation = PasswordVisualTransformation())
                OutlinedTextField(confirmPassword, { confirmPassword = it }, label = { Text(stringResource(R.string.confirm_password_label)) }, singleLine = true, visualTransformation = PasswordVisualTransformation())
                (authState as? AuthState.Error)?.let { Text(it.message, color = MaterialTheme.colorScheme.error) }
            }
        },
        confirmButton = {
            TextButton(enabled = authState != AuthState.Loading, onClick = { authViewModel.changePassword(oldPassword, newPassword, confirmPassword) }) {
                Text(if (authState == AuthState.Loading) stringResource(R.string.saving_ellipsis) else stringResource(R.string.save_button))
            }
        },
        dismissButton = { TextButton(onClick = { showPasswordDialog = false }) { Text(stringResource(R.string.cancel_button)) } }
    )

    if (showSecurityQuestionDialog) AlertDialog(
        onDismissRequest = { if (authState != AuthState.Loading) { showSecurityQuestionDialog = false; authViewModel.resetAuthState() } },
        title = { Text(stringResource(R.string.security_question_dialog_title)) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = stringResource(R.string.security_question_usage),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                OutlinedTextField(
                    value = questionInput,
                    onValueChange = { questionInput = it },
                    label = { Text(stringResource(R.string.security_question_label)) },
                    singleLine = true
                )
                OutlinedTextField(
                    value = answerInput,
                    onValueChange = { answerInput = it },
                    label = { Text(stringResource(R.string.secret_answer_label)) },
                    singleLine = true
                )
                (authState as? AuthState.Error)?.let { Text(it.message, color = MaterialTheme.colorScheme.error) }
            }
        },
        confirmButton = {
            TextButton(
                enabled = authState != AuthState.Loading && questionInput.isNotBlank() && answerInput.isNotBlank(),
                onClick = { authViewModel.setSecurityQuestion(questionInput, answerInput) }
            ) {
                Text(if (authState == AuthState.Loading) stringResource(R.string.saving_ellipsis) else stringResource(R.string.save_button))
            }
        },
        dismissButton = { TextButton(onClick = { showSecurityQuestionDialog = false; authViewModel.resetAuthState() }) { Text(stringResource(R.string.cancel_button)) } }
    )

    if (showDeleteDialog) AlertDialog(
        onDismissRequest = { showDeleteDialog = false },
        title = { Text(stringResource(R.string.delete_account_dialog_title)) },
        text = {
            Column {
                Text(stringResource(R.string.delete_account_dialog_message))
                (authState as? AuthState.Error)?.let { Text(it.message, color = MaterialTheme.colorScheme.error) }
            }
        },
        confirmButton = {
            TextButton(enabled = authState != AuthState.Loading, onClick = {
                currentUser?.let { deleteRequested = true; authViewModel.deleteUser(it.id) }
            }) { Text(if (authState == AuthState.Loading) stringResource(R.string.deleting) else stringResource(R.string.delete_button), color = MaterialTheme.colorScheme.error) }
        },
        dismissButton = { TextButton(onClick = { showDeleteDialog = false }) { Text(stringResource(R.string.cancel_button)) } }
    )

    if (showNewEvaluationDialog) ConfirmDialog(
        onDismiss = { showNewEvaluationDialog = false },
        onConfirm = { financeViewModel?.nuevaEvaluacion() },
        title = stringResource(R.string.new_evaluation_title),
        message = stringResource(R.string.new_evaluation_message),
        confirmText = stringResource(R.string.confirm_button),
        confirmColor = MaterialTheme.colorScheme.error
    )
}
