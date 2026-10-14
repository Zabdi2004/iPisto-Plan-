package com.finanzaspersonales.gt.data.repository

import com.finanzaspersonales.gt.data.local.dao.MetaAhorroDao
import com.finanzaspersonales.gt.data.local.entity.MetaAhorro
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext
import kotlinx.coroutines.Dispatchers

class MetasRepository(
    private val metaAhorroDao: MetaAhorroDao,
    private val ownerId: Long,
    private val ioDispatcher: CoroutineContext = Dispatchers.IO
) {
    suspend fun insertMeta(metaAhorro: MetaAhorro): Result<Long> = withContext(ioDispatcher) {
        try { Result.success(metaAhorroDao.insert(metaAhorro.copy(userId = ownerId))) }
        catch (e: Exception) { Result.failure(e) }
    }

    suspend fun updateMeta(metaAhorro: MetaAhorro): Result<Unit> = withContext(ioDispatcher) {
        try {
            if (metaAhorroDao.findById(metaAhorro.id, ownerId) == null) return@withContext Result.failure(SecurityException("Meta no encontrada"))
            metaAhorroDao.update(metaAhorro.copy(userId = ownerId)); Result.success(Unit)
        }
        catch (e: Exception) { Result.failure(e) }
    }

    suspend fun deleteMeta(metaAhorro: MetaAhorro): Result<Unit> = withContext(ioDispatcher) {
        try {
            val owned = metaAhorroDao.findById(metaAhorro.id, ownerId) ?: return@withContext Result.failure(SecurityException("Meta no encontrada"))
            metaAhorroDao.delete(owned); Result.success(Unit)
        }
        catch (e: Exception) { Result.failure(e) }
    }

    fun getMetasByUser(): Flow<List<MetaAhorro>> = metaAhorroDao.getAllByUser(ownerId)
    suspend fun getMetasByUserOnce(): List<MetaAhorro> = metaAhorroDao.getAllByUserOnce(ownerId)

    suspend fun deleteAllMetasByUser(): Result<Unit> = withContext(ioDispatcher) {
        try { metaAhorroDao.deleteAllByUser(ownerId); Result.success(Unit) }
        catch (e: Exception) { Result.failure(e) }
    }
}
