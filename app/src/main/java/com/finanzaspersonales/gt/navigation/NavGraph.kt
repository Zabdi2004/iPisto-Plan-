package com.finanzaspersonales.gt.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.key
import androidx.activity.compose.BackHandler
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
    const val Ingresos = "ingresos"
    const val Gastos = "gastos"
    const val Deudas = "deudas"
    const val NuevoRegistro = "registro/nuevo"
    const val Metas = "metas"
    const val MetaDetail = "metas/detalle/{metaId}"
    const val MetaNueva = "metas/nueva"
    const val MetaEditar = "metas/editar/{metaId}"
    const val MetaAporte = "metas/agregar-aporte/{metaId}"
    const val Perfil = "perfil"
    const val Configuracion = "configuracion"
    const val Seguridad = "seguridad"
    const val EducacionFinanciera = "educacion_financiera"
    const val RecuperarPassword = "recuperar_password"
    const val Ocio = "ocio"
}

@Composable
fun AppNavHost(authViewModel: AuthViewModel, navController: NavHostController = rememberNavController()) {
    val user by authViewModel.currentUser.collectAsState()
    val unlocked by authViewModel.isUnlocked.collectAsState()
    val authState by authViewModel.authState.collectAsState()
    var isRecoveringPassword by androidx.compose.runtime.saveable.rememberSaveable { mutableStateOf(false) }
    val context = LocalContext.current
    val app = context.applicationContext as FinanzasApp
    val financeViewModel = user?.id?.let { id ->
        viewModel<com.finanzaspersonales.gt.viewmodel.FinanzasViewModel>(
            key = "finanzas-$id", factory = app.finanzasViewModelFactory(id)
        )
    }

    LaunchedEffect(user?.id, unlocked) {
        if (user != null && unlocked) {
            navController.navigate(Screen.Inicio) {
                popUpTo(navController.graph.id) { inclusive = false }
                launchSingleTop = true
            }
        }
    }

    if (user != null && !unlocked) {
        if (isRecoveringPassword) {
            BackHandler(enabled = true) {
                isRecoveringPassword = false
                authViewModel.resetAuthState()
            }
            RecuperarPasswordScreen(
                initialUsername = user!!.nombreUsuario,
                authViewModel = authViewModel,
                onBack = {
                    isRecoveringPassword = false
                    authViewModel.resetAuthState()
                }
            )
        } else {
            BackHandler(enabled = true) { }
            key(user!!.id) {
                BloqueoScreen(
                    username = user!!.nombreUsuario,
                    loading = authState == com.finanzaspersonales.gt.viewmodel.AuthState.Loading,
                    error = (authState as? com.finanzaspersonales.gt.viewmodel.AuthState.Error)?.message,
                    onUnlock = authViewModel::unlock,
                    onLogout = {
                        authViewModel.logout()
                        navController.navigate(Screen.Welcome) {
                            popUpTo(navController.graph.id) { inclusive = true }
                        }
                    },
                    onForgotPassword = {
                        authViewModel.resetAuthState()
                        isRecoveringPassword = true
                    }
                )
            }
        }
        return
    }

    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route
    Scaffold(
        containerColor = androidx.compose.material3.MaterialTheme.colorScheme.background,
        topBar = {
            // La puerta de cerrar sesión vive en el lado superior derecho de todas las pantallas.
            // En Perfil no se muestra aquí porque esa pantalla incluye su propia barra (tuerca + puerta).
            if (user != null && unlocked && currentRoute != Screen.Perfil) {
                AppTopBar(onLogout = {
                    authViewModel.logout()
                    navController.navigate(Screen.Welcome) {
                        popUpTo(navController.graph.id) { inclusive = true }
                        launchSingleTop = true
                    }
                })
            }
        },
        bottomBar = {
            if (user != null && unlocked) IpistoBottomBar(currentRoute, navController)
        }
    ) { paddingValues ->
    NavHost(navController = navController, startDestination = Screen.Welcome, modifier = Modifier.padding(paddingValues)) {
        composable(Screen.Welcome) { WelcomeScreen(navController) }
        composable(Screen.Login) { LoginScreen(navController, authViewModel) }
        composable(Screen.Register) { RegisterScreen(navController, authViewModel) }
        composable(Screen.RecuperarPassword) {
            RecuperarPasswordScreen(
                initialUsername = "",
                authViewModel = authViewModel,
                onBack = {
                    authViewModel.resetAuthState()
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.Inicio) {
            PrivateRoute(user != null && financeViewModel != null, navController) { InicioScreen(navController, financeViewModel!!) }
        }
        composable(Screen.Graficas) {
            PrivateRoute(user != null && financeViewModel != null, navController) { GraficasScreen(financeViewModel!!) }
        }
        composable(Screen.Ingresos) {
            PrivateRoute(user != null && financeViewModel != null, navController) { IngresosScreen(navController, financeViewModel!!) }
        }
        composable(Screen.Gastos) {
            PrivateRoute(user != null && financeViewModel != null, navController) { GastosScreen(navController, financeViewModel!!) }
        }
        composable(Screen.Deudas) {
            PrivateRoute(user != null && financeViewModel != null, navController) { DeudasScreen(navController, financeViewModel!!) }
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
        composable(Screen.Seguridad) { PrivateRoute(user != null && financeViewModel != null, navController) { SeguridadScreen(navController, authViewModel, financeViewModel!!) } }
        composable(Screen.EducacionFinanciera) { PrivateRoute(user != null, navController) { EducacionFinancieraScreen() } }
        composable(Screen.Ocio) {
            PrivateRoute(user != null && financeViewModel != null, navController) { OcioScreen(navController, financeViewModel!!) }
        }
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
