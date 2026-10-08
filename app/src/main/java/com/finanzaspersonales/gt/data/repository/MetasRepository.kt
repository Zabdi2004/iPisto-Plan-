package com.finanzaspersonales.gt.data.repository

import com.finanzaspersonales.gt.data.local.dao.MetaAhorroDao
import com.finanzaspersonales.gt.data.local.entity.MetaAhorro
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext
import kotlinx.coroutines.Dispatchers

class MetasRepository(
    private val metaAhorroDao: MetaAhorroDao,
    private val ioDispatcher: CoroutineContext = Dispatchers.IO
) {
    suspend fun insertMeta(metaAhorro: MetaAhorro): Result<Long> = withContext(ioDispatcher) {
        try { Result.success(metaAhorroDao.insert(metaAhorro)) }
        catch (e: Exception) { Result.failure(e) }
    }

    suspend fun updateMeta(metaAhorro: MetaAhorro): Result<Unit> = withContext(ioDispatcher) {
        try { metaAhorroDao.update(metaAhorro); Result.success(Unit) }
        catch (e: Exception) { Result.failure(e) }
    }

    suspend fun deleteMeta(metaAhorro: MetaAhorro): Result<Unit> = withContext(ioDispatcher) {
        try { metaAhorroDao.delete(metaAhorro); Result.success(Unit) }
        catch (e: Exception) { Result.failure(e) }
    }

    fun getMetasByUser(userId: Long): Flow<List<MetaAhorro>> = metaAhorroDao.getAllByUser(userId)
    suspend fun getMetasByUserOnce(userId: Long): List<MetaAhorro> = metaAhorroDao.getAllByUserOnce(userId)

    suspend fun deleteAllMetasByUser(userId: Long): Result<Unit> = withContext(ioDispatcher) {
        try { metaAhorroDao.deleteAllByUser(userId); Result.success(Unit) }
        catch (e: Exception) { Result.failure(e) }
    }
}
