package com.finanzaspersonales.gt.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Celebration
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.finanzaspersonales.gt.R
import com.finanzaspersonales.gt.domain.calculator.BalanceFinanciero
import com.finanzaspersonales.gt.ui.components.ProgressBar
import com.finanzaspersonales.gt.viewmodel.FinanzasViewModel
import com.finanzaspersonales.gt.navigation.Screen
import com.finanzaspersonales.gt.ui.theme.IpistoPalette

/**
 * Pantalla de inicio: resumen financiero a pantalla completa.
 * Muestra el dinero disponible, el ingreso mensual y los gastos totales, el acceso a las
 * gráficas y tres accesos centrales para administrar ingresos, gastos y deudas.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InicioScreen(
    navController: NavHostController,
    finanzasViewModel: FinanzasViewModel = viewModel()
) {
    val uiState by finanzasViewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = stringResource(R.string.home_title),
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary
        )
        uiState.error?.let { message ->
            Text(
                text = stringResource(R.string.operation_error, message),
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium
            )
        }

        val balance = uiState.balance
        when {
            uiState.isLoading -> {
                Box(
                    modifier = Modifier.fillMaxWidth().weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    ProgressBar()
                }
            }

            balance == null -> {
                Box(
                    modifier = Modifier.fillMaxWidth().weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(R.string.no_data_start),
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            else -> {
                Spacer(modifier = Modifier.height(10.dp))
                BalanceSummary(balance = balance, finanzasViewModel = finanzasViewModel)
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedButton(
                    onClick = { navController.navigate(Screen.Graficas) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(stringResource(R.string.view_charts))
                }

                // Zona central: accesos a Ingresos, Gastos, Deudas y Ocio.
                Box(
                    modifier = Modifier.fillMaxWidth().weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        DashboardActionCard(
                            title = stringResource(R.string.income_label),
                            subtitle = finanzasViewModel.formatCurrency(balance.ingresosMensuales),
                            icon = Icons.AutoMirrored.Filled.TrendingUp,
                            accent = IpistoPalette.Income,
                            onClick = { navController.navigate(Screen.Ingresos) }
                        )
                        DashboardActionCard(
                            title = stringResource(R.string.expenses_label),
                            subtitle = finanzasViewModel.formatCurrency(
                                balance.gastosFijosMensuales + balance.gastosVariablesMensuales
                            ),
                            icon = Icons.AutoMirrored.Filled.TrendingDown,
                            accent = IpistoPalette.Expense,
                            onClick = { navController.navigate(Screen.Gastos) }
                        )
                        DashboardActionCard(
                            title = stringResource(R.string.debts_label),
                            subtitle = finanzasViewModel.formatCurrency(balance.deudaTotal),
                            icon = Icons.Filled.CreditCard,
                            accent = MaterialTheme.colorScheme.tertiary,
                            onClick = { navController.navigate(Screen.Deudas) }
                        )
                        DashboardActionCard(
                            title = stringResource(R.string.navigation_ocio),
                            subtitle = uiState.dineroDisponibleOcio?.let {
                                finanzasViewModel.formatCurrencyBd(it.dineroDisponible)
                            } ?: finanzasViewModel.formatCurrency(0.0),
                            icon = Icons.Filled.Celebration,
                            accent = IpistoPalette.Balance,
                            onClick = { navController.navigate(Screen.Ocio) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun BalanceSummary(
    balance: BalanceFinanciero,
    finanzasViewModel: FinanzasViewModel
) {
    val gastosTotales = balance.gastosFijosMensuales +
        balance.gastosVariablesMensuales +
        balance.pagosDeudaMensuales

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            shape = MaterialTheme.shapes.large
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = stringResource(R.string.available_money),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = finanzasViewModel.formatCurrency(balance.dineroDisponible),
                    style = MaterialTheme.typography.headlineLarge,
                    color = if (balance.dineroDisponible >= 0) IpistoPalette.Balance else IpistoPalette.Expense
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            SummaryStatCard(
                label = stringResource(R.string.monthly_income_label),
                amount = finanzasViewModel.formatCurrency(balance.ingresosMensuales),
                amountColor = IpistoPalette.Income,
                modifier = Modifier.weight(1f)
            )
            SummaryStatCard(
                label = stringResource(R.string.total_expenses),
                amount = finanzasViewModel.formatCurrency(gastosTotales),
                amountColor = IpistoPalette.Expense,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun SummaryStatCard(
    label: String,
    amount: String,
    amountColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = amount,
                style = MaterialTheme.typography.titleMedium,
                color = amountColor,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DashboardActionCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    accent: Color,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        shape = MaterialTheme.shapes.large
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = accent,
                modifier = Modifier.size(30.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = accent
                )
            }
        }
    }
}
