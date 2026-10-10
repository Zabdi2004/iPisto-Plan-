package com.finanzaspersonales.gt.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Savings
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController

@Composable
fun IpistoBottomBar(currentRoute: String?, navController: NavHostController) {
    val isOnGoals = currentRoute?.startsWith("metas") == true
    Box(Modifier.fillMaxWidth()) {
        NavigationBar(
            modifier = Modifier.align(Alignment.BottomCenter),
            containerColor = MaterialTheme.colorScheme.surface,
            tonalElevation = 10.dp
        ) {
            val colors = NavigationBarItemDefaults.colors(
                selectedIconColor = MaterialTheme.colorScheme.primary,
                selectedTextColor = MaterialTheme.colorScheme.primary,
                indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                disabledIconColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = .45f),
                disabledTextColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = .55f)
            )
            NavigationBarItem(
                selected = currentRoute == Screen.Inicio,
                onClick = { navigateTopLevel(navController, Screen.Inicio) },
                icon = { Icon(if (currentRoute == Screen.Inicio) Icons.Filled.Home else Icons.Outlined.Home, contentDescription = "Inicio") },
                label = { Text("Inicio") },
                colors = colors
            )
            NavigationBarItem(
                selected = currentRoute == Screen.Graficas,
                onClick = { navigateTopLevel(navController, Screen.Graficas) },
                icon = { Icon(if (currentRoute == Screen.Graficas) Icons.Filled.BarChart else Icons.Outlined.BarChart, contentDescription = "Informes") },
                label = { Text("Informes") },
                colors = colors
            )
            NavigationBarItem(
                selected = false,
                onClick = {},
                enabled = false,
                icon = { Spacer(Modifier.size(24.dp)) },
                label = null,
                colors = colors
            )
            NavigationBarItem(
                selected = isOnGoals,
                onClick = { navigateTopLevel(navController, Screen.Metas) },
                icon = { Icon(if (isOnGoals) Icons.Filled.Savings else Icons.Outlined.Savings, contentDescription = "Metas de ahorro") },
                label = { Text("Metas", maxLines = 1) },
                colors = colors
            )
            NavigationBarItem(
                selected = currentRoute == Screen.Perfil,
                onClick = { navigateTopLevel(navController, Screen.Perfil) },
                icon = { Icon(if (currentRoute == Screen.Perfil) Icons.Filled.Person else Icons.Outlined.Person, contentDescription = "Perfil") },
                label = { Text("Perfil") },
                colors = colors
            )
        }
        FloatingActionButton(
            onClick = { navController.navigate(Screen.NuevoRegistro) { launchSingleTop = true } },
            modifier = Modifier.align(Alignment.TopCenter).offset(y = (-22).dp),
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        ) {
            Icon(Icons.Default.Add, contentDescription = "Nuevo registro")
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
