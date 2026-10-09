package com.finanzaspersonales.gt.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.finanzaspersonales.gt.ui.screens.LoginScreen
import com.finanzaspersonales.gt.ui.screens.RegisterScreen
import com.finanzaspersonales.gt.ui.screens.WelcomeScreen
import com.finanzaspersonales.gt.ui.screens.InicioScreen
import com.finanzaspersonales.gt.ui.screens.GraficasScreen
import com.finanzaspersonales.gt.ui.screens.MetasScreen
import com.finanzaspersonales.gt.ui.screens.MetaDetailScreen
import com.finanzaspersonales.gt.ui.screens.MetaNuevaScreen
import com.finanzaspersonales.gt.ui.screens.MetaEditarScreen
import com.finanzaspersonales.gt.ui.screens.MetaAgregarAporteScreen
import com.finanzaspersonales.gt.ui.screens.PerfilScreen
import com.finanzaspersonales.gt.ui.screens.ConfiguracionScreen
import com.finanzaspersonales.gt.ui.screens.EducacionFinancieraScreen

sealed class Screen(val route: String) {
    object Welcome : Screen("welcome")
    object Login : Screen("login")
    object Register : Screen("register")
    object Inicio : Screen("inicio")
    object Graficas : Screen("graficas")
    object Metas : Screen("metas")
    object Perfil : Screen("perfil")
    object Configuracion : Screen("configuracion")
    object EducacionFinanciera : Screen("educacion_financiera")
}

@Composable
fun AppNavHost(
    navController: NavHostController,
    startDestination: String = Screen.Welcome.route
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Screen.Welcome.route) {
            WelcomeScreen(navController)
        }
        composable(Screen.Login.route) {
            LoginScreen(navController, androidx.lifecycle.viewmodel.compose.viewModel())
        }
        composable(Screen.Register.route) {
            RegisterScreen(navController, androidx.lifecycle.viewmodel.compose.viewModel())
        }
        composable(Screen.Inicio.route) {
            InicioScreen(navController, androidx.lifecycle.viewmodel.compose.viewModel())
        }
        composable(Screen.Graficas.route) {
            GraficasScreen(navController, androidx.lifecycle.viewmodel.compose.viewModel())
        }
        composable(Screen.Metas.route) {
            MetasScreen(navController, androidx.lifecycle.viewmodel.compose.viewModel())
        }
        composable(Screen.Perfil.route) {
            PerfilScreen(navController, androidx.lifecycle.viewmodel.compose.viewModel())
        }
        composable(Screen.Configuracion.route) {
            ConfiguracionScreen(navController)
        }
        composable(Screen.EducacionFinanciera.route) {
            EducacionFinancieraScreen(navController)
        }
    }
}
