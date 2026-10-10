package com.finanzaspersonales.gt.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navArgument
import androidx.compose.ui.platform.LocalContext
import com.finanzaspersonales.gt.FinanzasApp
import com.finanzaspersonales.gt.ui.screens.*
import com.finanzaspersonales.gt.viewmodel.AuthViewModel

object Screen {
    const val Welcome = "welcome"
    const val Login = "login"
    const val Register = "register"
    const val Inicio = "inicio"
    const val Graficas = "graficas"
    const val NuevoRegistro = "registro/nuevo"
    const val Metas = "metas"
    const val MetaDetail = "metas/detalle/{metaId}"
    const val MetaNueva = "metas/nueva"
    const val MetaEditar = "metas/editar/{metaId}"
    const val MetaAporte = "metas/agregar-aporte/{metaId}"
    const val Perfil = "perfil"
    const val Configuracion = "configuracion"
    const val EducacionFinanciera = "educacion_financiera"
}

@Composable
fun AppNavHost(authViewModel: AuthViewModel, navController: NavHostController = rememberNavController()) {
    val user by authViewModel.currentUser.collectAsState()
    val context = LocalContext.current
    val app = context.applicationContext as FinanzasApp
    val financeViewModel = user?.id?.let { id ->
        viewModel<com.finanzaspersonales.gt.viewmodel.FinanzasViewModel>(
            key = "finanzas-$id", factory = app.finanzasViewModelFactory(id)
        )
    }

    LaunchedEffect(user?.id) {
        if (user != null) {
            navController.navigate(Screen.Inicio) {
                popUpTo(Screen.Welcome) { inclusive = true }
                launchSingleTop = true
            }
        }
    }

    val backStackEntry by navController.currentBackStackEntryAsState()
    Scaffold(
        containerColor = androidx.compose.material3.MaterialTheme.colorScheme.background,
        bottomBar = {
            if (user != null) IpistoBottomBar(backStackEntry?.destination?.route, navController)
        }
    ) { paddingValues ->
    NavHost(navController = navController, startDestination = Screen.Welcome, modifier = Modifier.padding(paddingValues)) {
        composable(Screen.Welcome) { WelcomeScreen(navController) }
        composable(Screen.Login) { LoginScreen(navController, authViewModel) }
        composable(Screen.Register) { RegisterScreen(navController, authViewModel) }

        composable(Screen.Inicio) {
            PrivateRoute(user != null && financeViewModel != null, navController) { InicioScreen(navController, financeViewModel!!) }
        }
        composable(Screen.Graficas) {
            PrivateRoute(user != null && financeViewModel != null, navController) { GraficasScreen(financeViewModel!!) }
        }
        composable(Screen.NuevoRegistro) {
            PrivateRoute(user != null && financeViewModel != null, navController) { NuevoRegistroScreen(navController, financeViewModel!!) }
        }
        composable(Screen.Metas) {
            PrivateRoute(user != null && financeViewModel != null, navController) { MetasScreen(navController, financeViewModel!!) }
        }
        composable(Screen.MetaDetail, arguments = listOf(navArgument("metaId") { type = NavType.LongType })) { entry ->
            PrivateRoute(user != null && financeViewModel != null, navController) {
                MetaDetailScreen(navController, entry.arguments?.getLong("metaId") ?: 0L, financeViewModel!!)
            }
        }
        composable(Screen.MetaNueva) {
            PrivateRoute(user != null && financeViewModel != null, navController) { MetaNuevaScreen(navController, financeViewModel!!) }
        }
        composable(Screen.MetaEditar, arguments = listOf(navArgument("metaId") { type = NavType.LongType })) { entry ->
            PrivateRoute(user != null && financeViewModel != null, navController) {
                MetaEditarScreen(navController, entry.arguments?.getLong("metaId") ?: 0L, financeViewModel!!)
            }
        }
        composable(Screen.MetaAporte, arguments = listOf(navArgument("metaId") { type = NavType.LongType })) { entry ->
            PrivateRoute(user != null && financeViewModel != null, navController) {
                MetaAgregarAporteScreen(navController, entry.arguments?.getLong("metaId") ?: 0L, financeViewModel!!)
            }
        }
        composable(Screen.Perfil) { PrivateRoute(user != null && financeViewModel != null, navController) { PerfilScreen(navController, authViewModel, financeViewModel!!) } }
        composable(Screen.Configuracion) { PrivateRoute(user != null, navController) { ConfiguracionScreen(navController) } }
        composable(Screen.EducacionFinanciera) { PrivateRoute(user != null, navController) { EducacionFinancieraScreen() } }
    }
    }
}

@Composable
private fun PrivateRoute(isAuthenticated: Boolean, navController: NavHostController, content: @Composable () -> Unit) {
    if (isAuthenticated) {
        content()
    } else {
        LaunchedEffect(Unit) {
            navController.navigate(Screen.Welcome) {
                popUpTo(navController.graph.id) { inclusive = true }
                launchSingleTop = true
            }
        }
    }
}
