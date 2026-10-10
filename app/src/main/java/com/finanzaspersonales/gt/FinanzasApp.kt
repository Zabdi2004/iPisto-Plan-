package com.finanzaspersonales.gt

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.finanzaspersonales.gt.data.local.AppDatabase
import com.finanzaspersonales.gt.data.repository.FinanzasRepository
import com.finanzaspersonales.gt.data.repository.MetasRepository
import com.finanzaspersonales.gt.data.repository.UserRepository
import com.finanzaspersonales.gt.utils.SessionManager
import com.finanzaspersonales.gt.viewmodel.AuthViewModel
import com.finanzaspersonales.gt.viewmodel.FinanzasViewModel

class FinanzasApp : Application() {
    val database by lazy { AppDatabase.getDatabase(this) }
    val sessionManager by lazy { SessionManager(this) }
    val userRepository by lazy { UserRepository(database) }
    fun finanzasRepository(ownerId: Long) = FinanzasRepository(
        database.ingresoDao(), database.gastoFijoDao(), database.gastoVariableDao(), database.deudaDao(), ownerId
    )
    fun metasRepository(ownerId: Long) = MetasRepository(database.metaAhorroDao(), ownerId)

    val authViewModelFactory: ViewModelProvider.Factory by lazy {
        factory { AuthViewModel(userRepository, sessionManager) }
    }

    fun finanzasViewModelFactory(userId: Long): ViewModelProvider.Factory =
        factory { FinanzasViewModel(finanzasRepository(userId), metasRepository(userId), userId) }

    private fun <T : ViewModel> factory(create: () -> T) = object : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <VM : ViewModel> create(modelClass: Class<VM>): VM = create() as VM
    }
}
