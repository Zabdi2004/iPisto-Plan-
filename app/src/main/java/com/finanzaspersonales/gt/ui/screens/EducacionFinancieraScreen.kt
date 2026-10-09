package com.finanzaspersonales.gt.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EducacionFinancieraScreen(
    navController: NavHostController
) {
    val tips = remember {
        listOf(
            TipItem("Regla del 50/30/20", "Presupuesto", Icons.Default.AccountBalanceWallet, "Divide tus ingresos: 50% necesidades (vivienda, comida, transporte), 30% deseos (entretenimiento, compras), 20% ahorro e inversiones. Ajusta según tu realidad guatemalteca."),
            TipItem("Fondo de Emergencia", "Seguridad", Icons.Default.Security, "Mantén 3-6 meses de gastos básicos en una cuenta separada y accesible. En Guatemala, considera Q15,000-Q30,000 como base para imprevistos médicos, reparaciones o pérdida de empleo."),
            TipItem("Interés Compuesto", "Inversión", Icons.Default.TrendingUp, "El interés compuesto hace que tus ganancias generen más ganancias. Empezar temprano es clave: Q500/mes al 8% anual = Q294,000 en 20 años vs Q73,000 en 10 años. El tiempo es tu mejor aliado."),
            TipItem("Diversificación", "Riesgo", Icons.Default.Hub, "No pongas todos los huevos en la misma canasta. Diversifica: efectivo, certificados de depósito, bonos, acciones, bienes raíces. En Guatemala: bancas, cooperativas, fondos de inversión, propiedades."),
            TipItem("Horizonte de Inversión", "Planificación", Icons.Default.CalendarMonth, "Tu horizonte temporal determina el riesgo: Corto plazo (0-3 años) = conservador (depósitos, bonos). Mediano (3-7 años) = mixto. Largo plazo (7+ años) = puede asumir más riesgo (acciones, fondos)."),
            TipItem("Deuda Inteligente", "Deuda", Icons.Default.CreditCard, "Diferencia deuda buena (genera valor: educación, vivienda, negocio) de deuda mala (consumo: tarjetas, préstamos personales). Prioriza pagar deudas con interés alto (>20%) antes de invertir."),
            TipItem("Inflación en Guatemala", "Economía", Icons.Default.Speed, "La inflación erosiona el poder adquisitivo (~4-6% anual en GT). Tu dinero debe crecer por encima de la inflación. Mantener efectivo bajo el colchón pierde valor. Invierte para preservar y crecer."),
            TipItem("Automatización", "Hábitos", Icons.Default.AutoAwesome, "Automatiza tu ahorro: configura transferencias automáticas al recibir tu salario. \"Págate a ti primero\". Usa débitos automáticos para metas, inversiones y pagos de deuda. Elimina la decisión emocional."),
            TipItem("Presupuesto Base Cero", "Presupuesto", Icons.Default.Calculate, "Asigna cada quetzal a una categoría antes de que empiece el mes: Ingresos - Gastos - Ahorro = 0. Si sobra, destina a metas. Si falta, ajusta gastos. Revisa semanalmente y corrige desviaciones."),
            TipItem("Protección Patrimonial", "Seguros", Icons.Default.Shield, "Protege lo construido: seguro médico (gastos catastróficos), seguro de vida (si tienes dependientes), seguro de vivienda/auto. En Guatemala, evalúa seguros de cooperativas y bancas vs aseguradoras tradicionales.")
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("EDUCACIÓN FINANCIERA", style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.height(8.dp))
        Text("Consejos prácticos para tu salud financiera en Guatemala", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(modifier = Modifier.height(16.dp))

        Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            tips.forEach { tip ->
                var expanded by remember { mutableStateOf(false) }
                TipCard(tip = tip, expanded = expanded, onExpandChange = { expanded = it })
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
        Card(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Icon(Icons.Default.Info, contentDescription = "Info", tint = MaterialTheme.colorScheme.onTertiaryContainer, modifier = Modifier.size(24.dp))
                Text("Las proyecciones y consejos son únicamente educativos y no representan una garantía de rendimiento financiero.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onTertiaryContainer, textAlign = TextAlign.Center)
                Text("Consulta a un asesor financiero certificado para decisiones importantes.", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.7f), textAlign = TextAlign.Center)
            }
        }
    }
}

data class TipItem(
    val titulo: String,
    val categoria: String,
    val icon: ImageVector,
    val contenido: String
)

@Composable
fun TipCard(
    tip: TipItem,
    expanded: Boolean,
    onExpandChange: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = if (expanded) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(tip.icon, contentDescription = tip.categoria, tint = if (expanded) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.primary, modifier = Modifier.size(24.dp).padding(end = 12.dp))
                    Column {
                        Text(tip.titulo, style = MaterialTheme.typography.titleMedium, color = if (expanded) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.primary)
                        Text(tip.categoria, style = MaterialTheme.typography.labelSmall, color = if (expanded) MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f) else MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
                IconButton(onClick = { onExpandChange(!expanded) }) {
                    Icon(if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore, contentDescription = if (expanded) "Contraer" else "Expandir", tint = if (expanded) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            if (expanded) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(tip.contenido, style = MaterialTheme.typography.bodyMedium, color = if (expanded) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}
