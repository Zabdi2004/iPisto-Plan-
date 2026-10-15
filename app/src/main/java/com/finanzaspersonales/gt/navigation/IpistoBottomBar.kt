package com.finanzaspersonales.gt.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Savings
import androidx.compose.material.icons.outlined.School
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.finanzaspersonales.gt.R

/**
 * Barra de navegación inferior de iPisto.
 *
 * Cinco elementos distribuidos uniformemente: Inicio, Metas, Crear (botón circular central),
 * Educación y Perfil. El elemento activo se resalta con un fondo circular, sin etiquetas de texto.
 */
@Composable
fun IpistoBottomBar(currentRoute: String?, navController: NavHostController) {
    val isOnGoals = currentRoute?.startsWith("metas") == true
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(28.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 3.dp,
            shadowElevation = 10.dp
        ) {
            Column {
                HorizontalDivider(
                    thickness = 1.dp,
                    color = MaterialTheme.colorScheme.outlineVariant
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    BarIconItem(
                        selected = currentRoute == Screen.Inicio,
                        icon = if (currentRoute == Screen.Inicio) Icons.Filled.Home else Icons.Outlined.Home,
                        contentDescription = stringResource(R.string.navigation_home),
                        onClick = { navigateTopLevel(navController, Screen.Inicio) },
                        modifier = Modifier.weight(1f)
                    )
                    BarIconItem(
                        selected = isOnGoals,
                        icon = if (isOnGoals) Icons.Filled.Savings else Icons.Outlined.Savings,
                        contentDescription = stringResource(R.string.navigation_goals),
                        onClick = { navigateTopLevel(navController, Screen.Metas) },
                        modifier = Modifier.weight(1f)
                    )
                    BarCreateButton(
                        onClick = {
                            navController.navigate(Screen.NuevoRegistro) { launchSingleTop = true }
                        },
                        modifier = Modifier.weight(1f)
                    )
                    BarIconItem(
                        selected = currentRoute == Screen.EducacionFinanciera,
                        icon = if (currentRoute == Screen.EducacionFinanciera) Icons.Filled.School else Icons.Outlined.School,
                        contentDescription = "Educación",
                        onClick = { navController.navigate(Screen.EducacionFinanciera) { launchSingleTop = true } },
                        modifier = Modifier.weight(1f)
                    )
                    BarIconItem(
                        selected = currentRoute == Screen.Perfil,
                        icon = if (currentRoute == Screen.Perfil) Icons.Filled.Person else Icons.Outlined.Person,
                        contentDescription = stringResource(R.string.navigation_profile),
                        onClick = { navigateTopLevel(navController, Screen.Perfil) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun BarIconItem(
    selected: Boolean,
    icon: ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val tint = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
    val background = if (selected) MaterialTheme.colorScheme.primaryContainer else Color.Transparent
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(background)
                .clickable(onClick = onClick),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = contentDescription,
                tint = tint,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
private fun BarCreateButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary)
                .clickable(onClick = onClick),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Filled.Add,
                contentDescription = stringResource(R.string.navigation_add_record),
                tint = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.size(28.dp)
            )
        }
    }
}

private fun navigateTopLevel(navController: NavHostController, route: String) {
    navController.navigate(route) {
        popUpTo(navController.graph.startDestinationId) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}
