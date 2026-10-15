package com.finanzaspersonales.gt

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import com.finanzaspersonales.gt.navigation.AppNavHost
import com.finanzaspersonales.gt.ui.screens.LoadingScreen
import com.finanzaspersonales.gt.ui.theme.FinanzasPersonalesGTTheme
import com.finanzaspersonales.gt.utils.PreferencesHelper
import com.finanzaspersonales.gt.viewmodel.AuthState
import com.finanzaspersonales.gt.viewmodel.AuthViewModel

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

        val logoutObserver = object : DefaultLifecycleObserver {
            override fun onStop(owner: LifecycleOwner) {
                if (!PreferencesHelper.isLanguageChanging) {
                    authViewModel.logout()
                }
            }
        }
        ProcessLifecycleOwner.get().lifecycle.addObserver(logoutObserver)
        lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onDestroy(owner: LifecycleOwner) {
                ProcessLifecycleOwner.get().lifecycle.removeObserver(logoutObserver)
                lifecycle.removeObserver(this)
            }
        })

        // If the Activity is being recreated after a language or currency change,
        // restore the session. On a cold start, log out so the user always has to
        // log in again (the session dies when the app is closed). A plain
        // configuration change (for example, rotating the device) keeps the session.
        val languageChangeInProgress = PreferencesHelper.isLanguageChanging
        val sessionUserId = (application as FinanzasApp).sessionManager.getCurrentUserId()
        if (languageChangeInProgress && sessionUserId != null) {
            authViewModel.restoreSession(sessionUserId)
        } else if (savedInstanceState == null) {
            authViewModel.logout()
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
