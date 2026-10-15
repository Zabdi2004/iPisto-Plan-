package com.finanzaspersonales.gt.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.finanzaspersonales.gt.R
import com.finanzaspersonales.gt.navigation.Screen
import com.finanzaspersonales.gt.ui.components.QuetzalMascot
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import com.finanzaspersonales.gt.data.local.entity.MetaAhorro
import com.finanzaspersonales.gt.viewmodel.FinanzasViewModel
import com.finanzaspersonales.gt.ui.components.ConfirmDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MetasScreen(
    navController: NavHostController,
    finanzasViewModel: FinanzasViewModel = viewModel()
) {
    val uiState: androidx.compose.runtime.State<com.finanzaspersonales.gt.viewmodel.FinanzasUiState> = 
        finanzasViewModel.uiState.collectAsState()

    Box(Modifier.fillMaxSize()) {
      Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(start = 16.dp, top = 16.dp, end = 16.dp, bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
      ) {
        Text(stringResource(R.string.goals_title), style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.primary)

        if (uiState.value.metas.isEmpty()) {
            Box(modifier = Modifier.fillMaxWidth().padding(vertical = 64.dp), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    QuetzalMascot(size = 64.dp)
                    Text(stringResource(R.string.no_goals_registered_msg), style = MaterialTheme.typography.bodyLarge, textAlign = TextAlign.Center, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        } else {
            Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                uiState.value.metas.forEach { meta: MetaAhorro ->
                    MetaCard(meta = meta, formatter = finanzasViewModel::formatCurrency, onDelete = { finanzasViewModel.deleteMeta(meta) }, onEdit = { navController.navigate("${Screen.MetaEditar.substringBefore("{")}${meta.id}") })
                }
            }
        }
      }
      FloatingActionButton(
          onClick = { navController.navigate(Screen.MetaNueva) { launchSingleTop = true } },
          modifier = Modifier.align(Alignment.BottomEnd).padding(end = 16.dp, bottom = 12.dp),
          shape = androidx.compose.foundation.shape.CircleShape,
          containerColor = Color(0xFF8FA8F5),
          contentColor = Color(0xFF1B2133)
      ) {
          Icon(Icons.Default.Add, contentDescription = stringResource(R.string.add_savings_goal))
      }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MetaCard(
    meta: MetaAhorro,
    formatter: (Double) -> String,
    onDelete: () -> Unit,
    onEdit: () -> Unit
) {
    val ahorrada = meta.cantidadAhorrada.toDouble()
    val objetivo = meta.cantidadObjetivo.toDouble()
    val aporte = meta.aporteMensual.toDouble()
    val progress = if (objetivo > 0) (ahorrada / objetivo).coerceIn(0.0, 1.0) else 0.0
    val mesesRestantes = if (aporte > 0 && ahorrada < objetivo) {
        Math.ceil((objetivo - ahorrada) / aporte).toInt()
    } else 0

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(meta.nombre, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary, modifier = Modifier.weight(1f))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onEdit) { Icon(Icons.Default.Edit, contentDescription = stringResource(R.string.edit), tint = MaterialTheme.colorScheme.primary) }
                    IconButton(onClick = onDelete) { Icon(Icons.Default.Delete, contentDescription = stringResource(R.string.remove), tint = MaterialTheme.colorScheme.error) }
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column { Text(stringResource(R.string.progress_label), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant); Text(stringResource(R.string.progress_percent, (progress * 100).toInt()), style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.secondary) }
                Column(horizontalAlignment = Alignment.End) { Text(stringResource(R.string.months_remaining_label), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant); Text(mesesRestantes.toString(), style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.tertiary) }
            }
            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(progress = progress.toFloat(), modifier = Modifier.fillMaxWidth(), color = MaterialTheme.colorScheme.secondary, trackColor = MaterialTheme.colorScheme.secondaryContainer)
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) { Text(stringResource(R.string.saved_amount, formatter(ahorrada)), style = MaterialTheme.typography.bodySmall, modifier = Modifier.weight(1f)); Text(stringResource(R.string.objective_amount, formatter(objetivo)), style = MaterialTheme.typography.bodySmall) }
            Spacer(modifier = Modifier.height(4.dp))
            if (mesesRestantes > 0) {
                Text(stringResource(R.string.contributing_message, formatter(aporte), mesesRestantes), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            } else if (progress >= 1.0) {
                Text(stringResource(R.string.goal_achieved), style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.secondary)
            } else {
                Text(stringResource(R.string.set_monthly_contribution), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MetaDetailScreen(
    navController: NavHostController,
    metaId: Long,
    finanzasViewModel: FinanzasViewModel = viewModel()
) {
    val uiState: androidx.compose.runtime.State<com.finanzaspersonales.gt.viewmodel.FinanzasUiState> = 
        finanzasViewModel.uiState.collectAsState()
    
    val meta = remember(metaId) {
        derivedStateOf { uiState.value.metas.find { it.id == metaId } }
    }.value

    if (meta == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text(stringResource(R.string.goal_not_found), style = MaterialTheme.typography.bodyLarge) }
        return
    }

    val formatter: (Double) -> String = finanzasViewModel::formatCurrency
    val ahorrada = meta.cantidadAhorrada.toDouble()
    val objetivo = meta.cantidadObjetivo.toDouble()
    val aporte = meta.aporteMensual.toDouble()
    val progress = if (objetivo > 0) (ahorrada / objetivo).coerceIn(0.0, 1.0) else 0.0
    val montoFaltante = objetivo - ahorrada
    val mesesRestantes = if (aporte > 0 && montoFaltante > 0) {
        Math.ceil(montoFaltante / aporte).toInt()
    } else 0
val fechaEstimada = if (mesesRestantes > 0) {
        val fecha = java.util.Calendar.getInstance()
        fecha.add(java.util.Calendar.MONTH, mesesRestantes)
        java.text.SimpleDateFormat("MMMM yyyy", java.util.Locale("es", "GT")).format(fecha.time)
    } else "—"

    var showDeleteDialog by remember { mutableStateOf(false) }
    var isDeleting by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = { navController.popBackStack() }) { Icon(Icons.Default.ArrowBack, contentDescription = stringResource(R.string.back_button)) }
            Text(meta.nombre, style = MaterialTheme.typography.headlineSmall, color = MaterialTheme.colorScheme.primary, modifier = Modifier.weight(1f))
            Spacer(modifier = Modifier.width(48.dp))
        }

        Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text(stringResource(R.string.progress_title), style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(modifier = Modifier.height(8.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Column { Text(stringResource(R.string.progress_percent, (progress * 100).toInt()), style = MaterialTheme.typography.displaySmall, color = MaterialTheme.colorScheme.secondary); Text(stringResource(R.string.completed_label), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant) }
                    CircularProgressIndicator(progress = progress.toFloat(), modifier = Modifier.size(80.dp), strokeWidth = 8.dp, color = MaterialTheme.colorScheme.secondary, trackColor = MaterialTheme.colorScheme.secondaryContainer)
                }
                Spacer(modifier = Modifier.height(16.dp))
                LinearProgressIndicator(progress = progress.toFloat(), modifier = Modifier.fillMaxWidth(), color = MaterialTheme.colorScheme.secondary, trackColor = MaterialTheme.colorScheme.secondaryContainer)
            }
        }

        Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(stringResource(R.string.financial_detail_title), style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.height(16.dp))
                DetailRow(stringResource(R.string.objective_label), formatter(objetivo), isHighlight = true)
                DetailRow(stringResource(R.string.saved_label), formatter(ahorrada), color = MaterialTheme.colorScheme.primary)
                DetailRow(stringResource(R.string.missing_label), formatter(montoFaltante), color = MaterialTheme.colorScheme.tertiary)
                Divider(modifier = Modifier.padding(vertical = 8.dp))
                DetailRow(stringResource(R.string.monthly_contribution_detail), formatter(aporte))
                if (mesesRestantes > 0) {
                    DetailRow(stringResource(R.string.months_remaining_detail), mesesRestantes.toString(), color = MaterialTheme.colorScheme.secondary)
                    DetailRow(stringResource(R.string.estimated_date), fechaEstimada, color = MaterialTheme.colorScheme.secondary)
                } else if (progress >= 1.0) {
                    DetailRow(stringResource(R.string.status_label), stringResource(R.string.completed_status), color = MaterialTheme.colorScheme.secondary, isHighlight = true)
                } else {
                    DetailRow(stringResource(R.string.status_label), stringResource(R.string.no_monthly_contribution), color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }

        Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(stringResource(R.string.actions_title), style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = { navController.navigate("metas/agregar-aporte/${meta.id.toString()}") }, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)) { Text(stringResource(R.string.add_contribution_action)) }
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedButton(onClick = { navController.navigate("metas/editar/${meta.id.toString()}") }, modifier = Modifier.fillMaxWidth()) { Text(stringResource(R.string.edit_goal_action)) }
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedButton(
                    onClick = { showDeleteDialog = true },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) { Text(stringResource(R.string.delete_goal_action)) }
            }
        }

        if (showDeleteDialog) {
            ConfirmDialog(
                onDismiss = { showDeleteDialog = false },
            onConfirm = { isDeleting = true; finanzasViewModel.deleteMeta(meta) { deleted ->
                isDeleting = false
                if (deleted) { showDeleteDialog = false; navController.popBackStack() }
            } },
                title = stringResource(R.string.delete_goal_title),
                message = stringResource(R.string.delete_goal_message, meta.nombre) +
                    (uiState.value.error?.let { "\n\n" + stringResource(R.string.delete_error, it) } ?: ""),
                confirmText = if (isDeleting) stringResource(R.string.deleting) else stringResource(R.string.delete_button),
                confirmColor = MaterialTheme.colorScheme.error,
                dismissOnConfirm = false,
                confirmEnabled = !isDeleting
            )
        }

        if (aporte > 0 && progress < 1.0) {
            Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(stringResource(R.string.projection_title), style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onTertiaryContainer)
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Column(Modifier.weight(1f)) { Text(stringResource(R.string.current_month), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onTertiaryContainer); Text(formatter(ahorrada), style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onTertiaryContainer) }
                        Column(Modifier.weight(1f), horizontalAlignment = Alignment.End) { Text(stringResource(R.string.month_format, mesesRestantes, fechaEstimada), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onTertiaryContainer); Text(formatter(objetivo), style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onTertiaryContainer) }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(stringResource(R.string.projection_message, formatter(aporte), mesesRestantes), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onTertiaryContainer)
                    Text(stringResource(R.string.projection_disclaimer_short), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.7f))
                }
            }
        }
    }
}

@Composable
fun DetailRow(
    label: String,
    value: String,
    color: androidx.compose.ui.graphics.Color = androidx.compose.ui.graphics.Color.Unspecified,
    isHighlight: Boolean = false
) {
    val textColor = if (color != androidx.compose.ui.graphics.Color.Unspecified) color else MaterialTheme.colorScheme.onSurface
    val textStyle = if (isHighlight) MaterialTheme.typography.titleMedium else MaterialTheme.typography.bodyMedium
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.weight(1f))
        Text(value, style = textStyle, color = textColor, textAlign = TextAlign.End, modifier = Modifier.weight(1f))
    }
}
