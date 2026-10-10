package com.finanzaspersonales.gt.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text("METAS DE AHORRO", style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.primary)
            Button(onClick = { navController.navigate("metas/nueva") }) {
                Icon(Icons.Default.Add, contentDescription = "Agregar meta")
                Text("Nueva Meta")
            }
        }

        if (uiState.value.metas.isEmpty()) {
            Box(modifier = Modifier.fillMaxWidth().padding(vertical = 64.dp), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Icon(Icons.Default.Savings, contentDescription = "Metas", tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f), modifier = Modifier.size(64.dp))
                    Text("No hay metas de ahorro registradas.", style = MaterialTheme.typography.bodyLarge, textAlign = TextAlign.Center, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Button(onClick = { navController.navigate("metas/nueva") }) { Text("Crear Primera Meta") }
                }
            }
        } else {
            Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                uiState.value.metas.forEach { meta: MetaAhorro ->
                    MetaCard(meta = meta, formatter = finanzasViewModel::formatCurrency, onDelete = { finanzasViewModel.deleteMeta(meta) }, onEdit = { navController.navigate("metas/editar/${meta.id.toString()}") })
                }
            }
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
                Text(meta.nombre, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onEdit) { Icon(Icons.Default.Edit, contentDescription = "Editar", tint = MaterialTheme.colorScheme.primary) }
                    IconButton(onClick = onDelete) { Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = MaterialTheme.colorScheme.error) }
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column { Text("Progreso", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant); Text("${(progress * 100).toInt()}%", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.secondary) }
                Column(horizontalAlignment = Alignment.End) { Text("Meses restantes", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant); Text(mesesRestantes.toString(), style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.tertiary) }
            }
            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(progress = progress.toFloat(), modifier = Modifier.fillMaxWidth(), color = MaterialTheme.colorScheme.secondary, trackColor = MaterialTheme.colorScheme.secondaryContainer)
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) { Text("Ahorrado: ${formatter(ahorrada)}", style = MaterialTheme.typography.bodySmall); Text("Objetivo: ${formatter(objetivo)}", style = MaterialTheme.typography.bodySmall) }
            Spacer(modifier = Modifier.height(4.dp))
            if (mesesRestantes > 0) {
                Text("Aportando ${formatter(aporte)}/mes → Llegarás en $mesesRestantes meses", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            } else if (progress >= 1.0) {
                Text("¡Meta alcanzada! 🎉", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.secondary)
            } else {
                Text("Define un aporte mensual para ver la proyección", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
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
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("Meta no encontrada", style = MaterialTheme.typography.bodyLarge) }
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

    Column(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = { navController.popBackStack() }) { Icon(Icons.Default.ArrowBack, contentDescription = "Volver") }
            Text(meta.nombre, style = MaterialTheme.typography.headlineSmall, color = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.width(48.dp))
        }

        Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text("PROGRESO", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(modifier = Modifier.height(8.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Column { Text("${(progress * 100).toInt()}%", style = MaterialTheme.typography.displaySmall, color = MaterialTheme.colorScheme.secondary); Text("Completado", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant) }
                    CircularProgressIndicator(progress = progress.toFloat(), modifier = Modifier.size(80.dp), strokeWidth = 8.dp, color = MaterialTheme.colorScheme.secondary, trackColor = MaterialTheme.colorScheme.secondaryContainer)
                }
                Spacer(modifier = Modifier.height(16.dp))
                LinearProgressIndicator(progress = progress.toFloat(), modifier = Modifier.fillMaxWidth(), color = MaterialTheme.colorScheme.secondary, trackColor = MaterialTheme.colorScheme.secondaryContainer)
            }
        }

        Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("DETALLE FINANCIERO", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.height(16.dp))
                DetailRow("Objetivo", formatter(objetivo), isHighlight = true)
                DetailRow("Ahorrado", formatter(ahorrada), color = MaterialTheme.colorScheme.primary)
                DetailRow("Faltante", formatter(montoFaltante), color = MaterialTheme.colorScheme.tertiary)
                Divider(modifier = Modifier.padding(vertical = 8.dp))
                DetailRow("Aporte mensual", formatter(aporte))
                if (mesesRestantes > 0) {
                    DetailRow("Meses restantes", mesesRestantes.toString(), color = MaterialTheme.colorScheme.secondary)
                    DetailRow("Fecha estimada", fechaEstimada, color = MaterialTheme.colorScheme.secondary)
                } else if (progress >= 1.0) {
                    DetailRow("Estado", "¡COMPLETADA! 🎉", color = MaterialTheme.colorScheme.secondary, isHighlight = true)
                } else {
                    DetailRow("Estado", "Sin aporte mensual", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }

        Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("ACCIONES", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = { navController.navigate("metas/agregar-aporte/${meta.id.toString()}") }, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)) { Text("AGREGAR APORTE") }
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedButton(onClick = { navController.navigate("metas/editar/${meta.id.toString()}") }, modifier = Modifier.fillMaxWidth()) { Text("EDITAR META") }
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedButton(
                    onClick = { showDeleteDialog = true },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) { Text("ELIMINAR META") }
            }
        }

        if (showDeleteDialog) {
            ConfirmDialog(
                onDismiss = { showDeleteDialog = false },
                onConfirm = { finanzasViewModel.deleteMeta(meta); navController.popBackStack(); showDeleteDialog = false },
                title = "Eliminar Meta",
                message = "¿Estás seguro de eliminar \"${meta.nombre}\"? Esta acción no se puede deshacer.",
                confirmText = "Eliminar",
                confirmColor = MaterialTheme.colorScheme.error
            )
        }

        if (aporte > 0 && progress < 1.0) {
            Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("PROYECCIÓN", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onTertiaryContainer)
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Column { Text("Mes actual", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onTertiaryContainer); Text(formatter(ahorrada), style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onTertiaryContainer) }
                        Column(horizontalAlignment = Alignment.End) { Text("Mes $mesesRestantes ($fechaEstimada)", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onTertiaryContainer); Text(formatter(objetivo), style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onTertiaryContainer) }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Con un aporte constante de ${formatter(aporte)}/mes, alcanzarás tu meta en $mesesRestantes meses.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onTertiaryContainer)
                    Text("Nota: Esta es una proyección estimada. Los resultados reales pueden variar.", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.7f))
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
        Text(label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, style = textStyle, color = textColor)
    }
}
