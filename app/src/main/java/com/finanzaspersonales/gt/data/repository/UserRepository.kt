package com.finanzaspersonales.gt.data.repository

import com.finanzaspersonales.gt.data.local.AppDatabase
import com.finanzaspersonales.gt.data.local.entity.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext
import kotlinx.coroutines.Dispatchers
import androidx.room.withTransaction
import com.finanzaspersonales.gt.utils.PasswordHasher

class UserRepository(
    private val database: AppDatabase,
    private val ioDispatcher: CoroutineContext = Dispatchers.IO
) {
    private val userTypeDao get() = database.userTypeDao()
    suspend fun registerUser(user: User): Result<Long> = withContext(ioDispatcher) {
        try {
            database.withTransaction {
                if (userTypeDao.findByUsername(user.nombreUsuario) != null) {
                    Result.failure(IllegalArgumentException("El nombre de usuario ya existe"))
                } else {
                    Result.success(userTypeDao.insert(user))
                }
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun authenticateUser(username: String, password: String): Result<User> = withContext(ioDispatcher) {
        try {
            val user = userTypeDao.findByUsername(username)
            if (user != null && PasswordHasher.verifyPassword(password, user.passwordHash)) {
                Result.success(user)
            } else {
                Result.failure(Exception("Credenciales incorrectas"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun findByUsername(username: String): User? = withContext(ioDispatcher) {
        userTypeDao.findByUsername(username)
    }

    suspend fun getUserById(id: Long): User? = withContext(ioDispatcher) {
        userTypeDao.findById(id)
    }

    suspend fun updateUser(user: User): Result<Unit> = withContext(ioDispatcher) {
        try {
            userTypeDao.updatePassword(user.id, user.passwordHash)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun renameUser(id: Long, username: String): Result<Unit> = withContext(ioDispatcher) {
        try {
            database.withTransaction {
                val existing = userTypeDao.findByUsername(username)
                when {
                    existing != null && existing.id != id -> Result.failure(IllegalArgumentException("Ese nombre de usuario ya está en uso"))
                    userTypeDao.findById(id) == null -> Result.failure(IllegalArgumentException("La cuenta ya no existe"))
                    else -> {
                        userTypeDao.updateUsername(id, username)
                        Result.success(Unit)
                    }
                }
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteUser(id: Long): Result<Unit> = withContext(ioDispatcher) {
        try {
            database.withTransaction {
                database.ingresoDao().deleteAllByUser(id)
                database.gastoFijoDao().deleteAllByUser(id)
                database.gastoVariableDao().deleteAllByUser(id)
                database.deudaDao().deleteAllByUser(id)
                database.metaAhorroDao().deleteAllByUser(id)
                userTypeDao.deleteById(id)
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun getAllUsers(): Flow<List<User>> = userTypeDao.getAllUsers()
}
