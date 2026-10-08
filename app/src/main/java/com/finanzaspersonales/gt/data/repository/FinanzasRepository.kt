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
    private val ioDispatcher: CoroutineContext = Dispatchers.IO
) {
    // Ingresos
    suspend fun insertIngreso(ingreso: Ingreso): Result<Long> = withContext(ioDispatcher) {
        try { Result.success(ingresoDao.insert(ingreso)) }
        catch (e: Exception) { Result.failure(e) }
    }

    suspend fun updateIngreso(ingreso: Ingreso): Result<Unit> = withContext(ioDispatcher) {
        try { ingresoDao.update(ingreso); Result.success(Unit) }
        catch (e: Exception) { Result.failure(e) }
    }

    suspend fun deleteIngreso(ingreso: Ingreso): Result<Unit> = withContext(ioDispatcher) {
        try { ingresoDao.delete(ingreso); Result.success(Unit) }
        catch (e: Exception) { Result.failure(e) }
    }

    fun getIngresosByUser(userId: Long): Flow<List<Ingreso>> = ingresoDao.getAllByUser(userId)
    suspend fun getIngresosByUserOnce(userId: Long): List<Ingreso> = ingresoDao.getAllByUserOnce(userId)

    // Gastos Fijos
    suspend fun insertGastoFijo(gastoFijo: GastoFijo): Result<Long> = withContext(ioDispatcher) {
        try { Result.success(gastoFijoDao.insert(gastoFijo)) }
        catch (e: Exception) { Result.failure(e) }
    }

    suspend fun updateGastoFijo(gastoFijo: GastoFijo): Result<Unit> = withContext(ioDispatcher) {
        try { gastoFijoDao.update(gastoFijo); Result.success(Unit) }
        catch (e: Exception) { Result.failure(e) }
    }

    suspend fun deleteGastoFijo(gastoFijo: GastoFijo): Result<Unit> = withContext(ioDispatcher) {
        try { gastoFijoDao.delete(gastoFijo); Result.success(Unit) }
        catch (e: Exception) { Result.failure(e) }
    }

    fun getGastosFijosByUser(userId: Long): Flow<List<GastoFijo>> = gastoFijoDao.getAllByUser(userId)
    suspend fun getGastosFijosByUserOnce(userId: Long): List<GastoFijo> = gastoFijoDao.getAllByUserOnce(userId)

    // Gastos Variables
    suspend fun insertGastoVariable(gastoVariable: GastoVariable): Result<Long> = withContext(ioDispatcher) {
        try { Result.success(gastoVariableDao.insert(gastoVariable)) }
        catch (e: Exception) { Result.failure(e) }
    }

    suspend fun updateGastoVariable(gastoVariable: GastoVariable): Result<Unit> = withContext(ioDispatcher) {
        try { gastoVariableDao.update(gastoVariable); Result.success(Unit) }
        catch (e: Exception) { Result.failure(e) }
    }

    suspend fun deleteGastoVariable(gastoVariable: GastoVariable): Result<Unit> = withContext(ioDispatcher) {
        try { gastoVariableDao.delete(gastoVariable); Result.success(Unit) }
        catch (e: Exception) { Result.failure(e) }
    }

    fun getGastosVariablesByUser(userId: Long): Flow<List<GastoVariable>> = gastoVariableDao.getAllByUser(userId)
    suspend fun getGastosVariablesByUserOnce(userId: Long): List<GastoVariable> = gastoVariableDao.getAllByUserOnce(userId)

    // Deudas
    suspend fun insertDeuda(deuda: Deuda): Result<Long> = withContext(ioDispatcher) {
        try { Result.success(deudaDao.insert(deuda)) }
        catch (e: Exception) { Result.failure(e) }
    }

    suspend fun updateDeuda(deuda: Deuda): Result<Unit> = withContext(ioDispatcher) {
        try { deudaDao.update(deuda); Result.success(Unit) }
        catch (e: Exception) { Result.failure(e) }
    }

    suspend fun deleteDeuda(deuda: Deuda): Result<Unit> = withContext(ioDispatcher) {
        try { deudaDao.delete(deuda); Result.success(Unit) }
        catch (e: Exception) { Result.failure(e) }
    }

    fun getDeudasByUser(userId: Long): Flow<List<Deuda>> = deudaDao.getAllByUser(userId)
    suspend fun getDeudasByUserOnce(userId: Long): List<Deuda> = deudaDao.getAllByUserOnce(userId)

    // Nueva Evaluación
    suspend fun deleteAllFinancialData(userId: Long): Result<Unit> = withContext(ioDispatcher) {
        try {
            ingresoDao.deleteAllByUser(userId)
            gastoFijoDao.deleteAllByUser(userId)
            gastoVariableDao.deleteAllByUser(userId)
            deudaDao.deleteAllByUser(userId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
