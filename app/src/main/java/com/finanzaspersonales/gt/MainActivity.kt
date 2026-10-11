package com.finanzaspersonales.gt

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.finanzaspersonales.gt.navigation.AppNavHost
import androidx.activity.viewModels
import com.finanzaspersonales.gt.viewmodel.AuthViewModel
import com.finanzaspersonales.gt.viewmodel.FinanzasViewModel
import com.finanzaspersonales.gt.ui.theme.FinanzasPersonalesGTTheme
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner

class MainActivity : ComponentActivity() {
    private val authViewModel: AuthViewModel by viewModels {
        (application as FinanzasApp).authViewModelFactory
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE)
        val lockObserver = object : DefaultLifecycleObserver {
            override fun onStop(owner: LifecycleOwner) { authViewModel.lock() }
        }
        ProcessLifecycleOwner.get().lifecycle.addObserver(lockObserver)
        lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onDestroy(owner: LifecycleOwner) {
                ProcessLifecycleOwner.get().lifecycle.removeObserver(lockObserver)
                lifecycle.removeObserver(this)
            }
        })
        (application as FinanzasApp).sessionManager.getCurrentUserId()?.let(authViewModel::checkExistingSession)
        setContent {
            FinanzasPersonalesGTTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavHost(authViewModel)
                }
            }
        }
    }
}
