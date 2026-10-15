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
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Savings
import androidx.compose.material.icons.outlined.School
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
import androidx.compose.ui.res.stringResource
import com.finanzaspersonales.gt.R
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
                icon = { Icon(if (currentRoute == Screen.Inicio) Icons.Filled.Home else Icons.Outlined.Home, contentDescription = stringResource(R.string.navigation_home)) },
                label = { Text(stringResource(R.string.navigation_home)) },
                colors = colors
            )
            NavigationBarItem(
                selected = currentRoute == Screen.Graficas,
                onClick = { navigateTopLevel(navController, Screen.Graficas) },
                icon = { Icon(if (currentRoute == Screen.Graficas) Icons.Filled.BarChart else Icons.Outlined.BarChart, contentDescription = stringResource(R.string.navigation_reports)) },
                label = { Text(stringResource(R.string.navigation_reports)) },
                colors = colors
            )
            NavigationBarItem(
                selected = currentRoute == Screen.Ocio,
                onClick = { navigateTopLevel(navController, Screen.Ocio) },
                icon = { Icon(if (currentRoute == Screen.Ocio) Icons.Filled.Favorite else Icons.Outlined.Favorite, contentDescription = stringResource(R.string.navigation_ocio)) },
                label = { Text(stringResource(R.string.navigation_ocio)) },
                colors = colors
            )
            NavigationBarItem(
                selected = isOnGoals,
                onClick = { navigateTopLevel(navController, Screen.Metas) },
                icon = { Icon(if (isOnGoals) Icons.Filled.Savings else Icons.Outlined.Savings, contentDescription = stringResource(R.string.navigation_goals)) },
                label = { Text(stringResource(R.string.navigation_goals), maxLines = 1) },
                colors = colors
            )
            NavigationBarItem(
                selected = currentRoute == Screen.EducacionFinanciera,
                onClick = { navController.navigate(Screen.EducacionFinanciera) { launchSingleTop = true } },
                icon = { Icon(if (currentRoute == Screen.EducacionFinanciera) Icons.Filled.School else Icons.Outlined.School, contentDescription = "Educación financiera") },
                label = { Text("Educación", maxLines = 1) },
                colors = colors
            )
            NavigationBarItem(
                selected = currentRoute == Screen.Perfil,
                onClick = { navigateTopLevel(navController, Screen.Perfil) },
                icon = { Icon(if (currentRoute == Screen.Perfil) Icons.Filled.Person else Icons.Outlined.Person, contentDescription = stringResource(R.string.navigation_profile)) },
                label = { Text(stringResource(R.string.navigation_profile)) },
                colors = colors
            )
        }
        FloatingActionButton(
            onClick = { navController.navigate(Screen.NuevoRegistro) { launchSingleTop = true } },
            modifier = Modifier.align(Alignment.BottomEnd).offset(x = (-16).dp, y = (-80).dp),
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        ) {
            Icon(Icons.Default.Add, contentDescription = stringResource(R.string.navigation_add_record))
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
