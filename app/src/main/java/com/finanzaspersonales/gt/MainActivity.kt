package com.finanzaspersonales.gt

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.finanzaspersonales.gt.navigation.AppNavHost
import androidx.activity.viewModels
import com.finanzaspersonales.gt.ui.screens.LoadingScreen
import com.finanzaspersonales.gt.utils.PreferencesHelper
import com.finanzaspersonales.gt.viewmodel.AuthState
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

        // Apply saved language on startup
        val savedLanguage = PreferencesHelper.getLanguage(this)
        PreferencesHelper.applyLanguage(this, savedLanguage)
        PreferencesHelper.applyCurrency(this, PreferencesHelper.getCurrency(this))

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

        // If the Activity is being recreated after a language change, restore the
        // previously unlocked session instead of forcing the lock screen.
        val languageChangeInProgress = PreferencesHelper.isLanguageChanging
        val sessionUserId = (application as FinanzasApp).sessionManager.getCurrentUserId()
        if (languageChangeInProgress && sessionUserId != null) {
            authViewModel.restoreUnlockedSession(sessionUserId)
        } else {
            sessionUserId?.let(authViewModel::checkExistingSession)
        }

        setContent {
            val languageChangeState = remember { languageChangeInProgress }
            val authState by authViewModel.authState.collectAsState()
            val savedTheme = PreferencesHelper.getTheme(this@MainActivity)
            FinanzasPersonalesGTTheme(themeMode = savedTheme) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    if (languageChangeState && authState is AuthState.Loading) {
                        LoadingScreen()
                    } else {
                        if (languageChangeState) {
                            SideEffect { PreferencesHelper.isLanguageChanging = false }
                        }
                        AppNavHost(authViewModel)
                    }
                }
            }
        }
    }
}