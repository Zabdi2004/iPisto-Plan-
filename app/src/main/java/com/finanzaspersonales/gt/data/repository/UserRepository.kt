package com.finanzaspersonales.gt.data.repository

import com.finanzaspersonales.gt.data.local.dao.UserTypeDao
import com.finanzaspersonales.gt.data.local.entity.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext
import kotlinx.coroutines.Dispatchers

class UserRepository(
    private val userTypeDao: UserTypeDao,
    private val ioDispatcher: CoroutineContext = Dispatchers.IO
) {
    suspend fun registerUser(user: User): Result<Long> = withContext(ioDispatcher) {
        try {
            val existing = userTypeDao.findByUsername(user.nombreUsuario)
            if (existing != null) {
                Result.failure(Exception("El nombre de usuario ya existe"))
            } else {
                val id = userTypeDao.insert(user)
                Result.success(id)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun authenticateUser(username: String, passwordHash: String): Result<User> = withContext(ioDispatcher) {
        try {
            val user = userTypeDao.findByUsername(username)
            if (user != null && user.passwordHash == passwordHash) {
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

    suspend fun deleteUser(id: Long): Result<Unit> = withContext(ioDispatcher) {
        try {
            userTypeDao.deleteById(id.toInt())
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun getAllUsers(): Flow<List<User>> = userTypeDao.getAllUsers()
}
