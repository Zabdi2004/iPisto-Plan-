package com.finanzaspersonales.gt.data.repository

import com.finanzaspersonales.gt.data.local.dao.PresupuestoOcioDao
import com.finanzaspersonales.gt.data.local.entity.PresupuestoOcio
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext
import kotlinx.coroutines.Dispatchers

class PresupuestoOcioRepository(
    private val dao: PresupuestoOcioDao,
    private val ownerId: Long,
    private val ioDispatcher: CoroutineContext = Dispatchers.IO
) {
    suspend fun saveOrUpdate(presupuesto: PresupuestoOcio): Result<Long> = withContext(ioDispatcher) {
        try {
            val existing = dao.getByUserOnce(ownerId)
            if (existing != null) {
                dao.update(presupuesto.copy(id = existing.id, userId = ownerId))
                Result.success(existing.id)
            } else {
                Result.success(dao.insert(presupuesto.copy(userId = ownerId)))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun update(presupuesto: PresupuestoOcio): Result<Unit> = withContext(ioDispatcher) {
        try {
            if (dao.getByUserOnce(ownerId) == null) {
                Result.failure(SecurityException("Presupuesto no encontrado"))
            } else {
                dao.update(presupuesto.copy(userId = ownerId))
                Result.success(Unit)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun getByUser(): Flow<List<PresupuestoOcio>> = dao.getByUser(ownerId)
    suspend fun getByUserOnce(): PresupuestoOcio? = dao.getByUserOnce(ownerId)

    suspend fun deleteByUser(): Result<Unit> = withContext(ioDispatcher) {
        try { dao.deleteByUser(ownerId); Result.success(Unit) }
        catch (e: Exception) { Result.failure(e) }
    }
}
