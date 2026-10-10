package com.finanzaspersonales.gt.data.repository

import com.finanzaspersonales.gt.data.local.dao.IngresoDao
import com.finanzaspersonales.gt.data.local.dao.GastoFijoDao
import com.finanzaspersonales.gt.data.local.dao.GastoVariableDao
import com.finanzaspersonales.gt.data.local.dao.DeudaDao
import com.finanzaspersonales.gt.data.local.entity.Ingreso
import com.finanzaspersonales.gt.data.local.entity.GastoFijo
import com.finanzaspersonales.gt.data.local.entity.GastoVariable
import com.finanzaspersonales.gt.data.local.entity.Deuda
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext
import kotlinx.coroutines.Dispatchers

class FinanzasRepository(
    private val ingresoDao: IngresoDao,
    private val gastoFijoDao: GastoFijoDao,
    private val gastoVariableDao: GastoVariableDao,
    private val deudaDao: DeudaDao,
    private val ownerId: Long,
    private val ioDispatcher: CoroutineContext = Dispatchers.IO
) {
    // Ingresos
    suspend fun insertIngreso(ingreso: Ingreso): Result<Long> = withContext(ioDispatcher) {
        try { Result.success(ingresoDao.insert(ingreso.copy(userId = ownerId))) }
        catch (e: Exception) { Result.failure(e) }
    }

    suspend fun updateIngreso(ingreso: Ingreso): Result<Unit> = withContext(ioDispatcher) {
        try {
            if (ingresoDao.findById(ingreso.id, ownerId) == null) return@withContext Result.failure(SecurityException("Registro no encontrado"))
            ingresoDao.update(ingreso.copy(userId = ownerId)); Result.success(Unit)
        }
        catch (e: Exception) { Result.failure(e) }
    }

    suspend fun deleteIngreso(ingreso: Ingreso): Result<Unit> = withContext(ioDispatcher) {
        try {
            val owned = ingresoDao.findById(ingreso.id, ownerId) ?: return@withContext Result.failure(SecurityException("Registro no encontrado"))
            ingresoDao.delete(owned); Result.success(Unit)
        }
        catch (e: Exception) { Result.failure(e) }
    }

    fun getIngresosByUser(): Flow<List<Ingreso>> = ingresoDao.getAllByUser(ownerId)
    suspend fun getIngresosByUserOnce(): List<Ingreso> = ingresoDao.getAllByUserOnce(ownerId)

    // Gastos Fijos
    suspend fun insertGastoFijo(gastoFijo: GastoFijo): Result<Long> = withContext(ioDispatcher) {
        try { Result.success(gastoFijoDao.insert(gastoFijo.copy(userId = ownerId))) }
        catch (e: Exception) { Result.failure(e) }
    }

    suspend fun updateGastoFijo(gastoFijo: GastoFijo): Result<Unit> = withContext(ioDispatcher) {
        try {
            if (gastoFijoDao.findById(gastoFijo.id, ownerId) == null) return@withContext Result.failure(SecurityException("Registro no encontrado"))
            gastoFijoDao.update(gastoFijo.copy(userId = ownerId)); Result.success(Unit)
        }
        catch (e: Exception) { Result.failure(e) }
    }

    suspend fun deleteGastoFijo(gastoFijo: GastoFijo): Result<Unit> = withContext(ioDispatcher) {
        try {
            val owned = gastoFijoDao.findById(gastoFijo.id, ownerId) ?: return@withContext Result.failure(SecurityException("Registro no encontrado"))
            gastoFijoDao.delete(owned); Result.success(Unit)
        }
        catch (e: Exception) { Result.failure(e) }
    }

    fun getGastosFijosByUser(): Flow<List<GastoFijo>> = gastoFijoDao.getAllByUser(ownerId)
    suspend fun getGastosFijosByUserOnce(): List<GastoFijo> = gastoFijoDao.getAllByUserOnce(ownerId)

    // Gastos Variables
    suspend fun insertGastoVariable(gastoVariable: GastoVariable): Result<Long> = withContext(ioDispatcher) {
        try { Result.success(gastoVariableDao.insert(gastoVariable.copy(userId = ownerId))) }
        catch (e: Exception) { Result.failure(e) }
    }

    suspend fun updateGastoVariable(gastoVariable: GastoVariable): Result<Unit> = withContext(ioDispatcher) {
        try {
            if (gastoVariableDao.findById(gastoVariable.id, ownerId) == null) return@withContext Result.failure(SecurityException("Registro no encontrado"))
            gastoVariableDao.update(gastoVariable.copy(userId = ownerId)); Result.success(Unit)
        }
        catch (e: Exception) { Result.failure(e) }
    }

    suspend fun deleteGastoVariable(gastoVariable: GastoVariable): Result<Unit> = withContext(ioDispatcher) {
        try {
            val owned = gastoVariableDao.findById(gastoVariable.id, ownerId) ?: return@withContext Result.failure(SecurityException("Registro no encontrado"))
            gastoVariableDao.delete(owned); Result.success(Unit)
        }
        catch (e: Exception) { Result.failure(e) }
    }

    fun getGastosVariablesByUser(): Flow<List<GastoVariable>> = gastoVariableDao.getAllByUser(ownerId)
    suspend fun getGastosVariablesByUserOnce(): List<GastoVariable> = gastoVariableDao.getAllByUserOnce(ownerId)

    // Deudas
    suspend fun insertDeuda(deuda: Deuda): Result<Long> = withContext(ioDispatcher) {
        try { Result.success(deudaDao.insert(deuda.copy(userId = ownerId))) }
        catch (e: Exception) { Result.failure(e) }
    }

    suspend fun updateDeuda(deuda: Deuda): Result<Unit> = withContext(ioDispatcher) {
        try {
            if (deudaDao.findById(deuda.id, ownerId) == null) return@withContext Result.failure(SecurityException("Registro no encontrado"))
            deudaDao.update(deuda.copy(userId = ownerId)); Result.success(Unit)
        }
        catch (e: Exception) { Result.failure(e) }
    }

    suspend fun deleteDeuda(deuda: Deuda): Result<Unit> = withContext(ioDispatcher) {
        try {
            val owned = deudaDao.findById(deuda.id, ownerId) ?: return@withContext Result.failure(SecurityException("Registro no encontrado"))
            deudaDao.delete(owned); Result.success(Unit)
        }
        catch (e: Exception) { Result.failure(e) }
    }

    fun getDeudasByUser(): Flow<List<Deuda>> = deudaDao.getAllByUser(ownerId)
    suspend fun getDeudasByUserOnce(): List<Deuda> = deudaDao.getAllByUserOnce(ownerId)

    // Nueva Evaluación
    suspend fun deleteAllFinancialData(): Result<Unit> = withContext(ioDispatcher) {
        try {
            ingresoDao.deleteAllByUser(ownerId)
            gastoFijoDao.deleteAllByUser(ownerId)
            gastoVariableDao.deleteAllByUser(ownerId)
            deudaDao.deleteAllByUser(ownerId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
