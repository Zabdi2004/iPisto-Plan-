package com.finanzaspersonales.gt

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import androidx.lifecycle.viewmodel.compose.viewModel
import com.finanzaspersonales.gt.navigation.AppNavHost
import com.finanzaspersonales.gt.navigation.Screen
import com.finanzaspersonales.gt.utils.SessionManager
import com.finanzaspersonales.gt.viewmodel.AuthViewModel
import com.finanzaspersonales.gt.viewmodel.FinanzasViewModel
import com.finanzaspersonales.gt.ui.theme.FinanzasPersonalesGTTheme

class MainActivity : ComponentActivity() {

    private val sessionManager: SessionManager by lazy {
        SessionManager(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FinanzasPersonalesGTTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    val authViewModel: AuthViewModel = viewModel()
                    
                    // Verificar sesión existente
                    if (sessionManager.isLoggedIn()) {
                        val userId = sessionManager.getCurrentUserId() ?: 0L
                        if (userId > 0) {
                            authViewModel.checkExistingSession(userId)
                        }
                    }

                    AppNavHost(navController)
                }
            }
        }
    }
}
