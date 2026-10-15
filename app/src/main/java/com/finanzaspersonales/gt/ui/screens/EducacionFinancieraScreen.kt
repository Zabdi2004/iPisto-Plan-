package com.finanzaspersonales.gt.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.finanzaspersonales.gt.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EducacionFinancieraScreen() {
    val tips = listOf(
        TipItem(stringResource(R.string.tip_50_30_20_title), stringResource(R.string.tip_50_30_20_category), Icons.Default.AccountBalanceWallet, stringResource(R.string.tip_50_30_20_content)),
        TipItem(stringResource(R.string.tip_emergency_fund_title), stringResource(R.string.tip_emergency_fund_category), Icons.Default.Security, stringResource(R.string.tip_emergency_fund_content)),
        TipItem(stringResource(R.string.tip_compound_interest_title), stringResource(R.string.tip_compound_interest_category), Icons.AutoMirrored.Filled.TrendingUp, stringResource(R.string.tip_compound_interest_content)),
        TipItem(stringResource(R.string.tip_diversification_title), stringResource(R.string.tip_diversification_category), Icons.Default.Hub, stringResource(R.string.tip_diversification_content)),
        TipItem(stringResource(R.string.tip_investment_horizon_title), stringResource(R.string.tip_investment_horizon_category), Icons.Default.CalendarMonth, stringResource(R.string.tip_investment_horizon_content)),
        TipItem(stringResource(R.string.tip_smart_debt_title), stringResource(R.string.tip_smart_debt_category), Icons.Default.CreditCard, stringResource(R.string.tip_smart_debt_content)),
        TipItem(stringResource(R.string.tip_inflation_title), stringResource(R.string.tip_inflation_category), Icons.Default.Speed, stringResource(R.string.tip_inflation_content)),
        TipItem(stringResource(R.string.tip_automation_title), stringResource(R.string.tip_automation_category), Icons.Default.AutoAwesome, stringResource(R.string.tip_automation_content)),
        TipItem(stringResource(R.string.tip_zero_budget_title), stringResource(R.string.tip_zero_budget_category), Icons.Default.Calculate, stringResource(R.string.tip_zero_budget_content)),
        TipItem(stringResource(R.string.tip_asset_protection_title), stringResource(R.string.tip_asset_protection_category), Icons.Default.Shield, stringResource(R.string.tip_asset_protection_content))
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(stringResource(R.string.education_title), style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.height(8.dp))
        Text(stringResource(R.string.education_subtitle), style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
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
                Icon(Icons.Default.Info, contentDescription = stringResource(R.string.cd_info), tint = MaterialTheme.colorScheme.onTertiaryContainer, modifier = Modifier.size(24.dp))
                Text(stringResource(R.string.education_disclaimer_full), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onTertiaryContainer, textAlign = TextAlign.Center)
                Text(stringResource(R.string.education_consult_advisor), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.7f), textAlign = TextAlign.Center)
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
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    Icon(tip.icon, contentDescription = tip.categoria, tint = if (expanded) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.primary, modifier = Modifier.size(24.dp).padding(end = 12.dp))
                    Column {
                        Text(tip.titulo, style = MaterialTheme.typography.titleMedium, color = if (expanded) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.primary)
                        Text(tip.categoria, style = MaterialTheme.typography.labelSmall, color = if (expanded) MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f) else MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
                IconButton(onClick = { onExpandChange(!expanded) }) {
                    Icon(if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore, contentDescription = if (expanded) stringResource(R.string.collapse_button) else stringResource(R.string.expand_button), tint = if (expanded) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            if (expanded) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(tip.contenido, style = MaterialTheme.typography.bodyMedium, color = if (expanded) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}
