package com.finanzaspersonales.gt.data.local.dao

import androidx.room.*
import com.finanzaspersonales.gt.data.local.entity.PresupuestoOcio
import kotlinx.coroutines.flow.Flow

@Dao
interface PresupuestoOcioDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(presupuesto: PresupuestoOcio): Long

    @Update
    suspend fun update(presupuesto: PresupuestoOcio)

    @Delete
    suspend fun delete(presupuesto: PresupuestoOcio)

    @Query("SELECT * FROM PresupuestoOcio WHERE userId = :userId LIMIT 1")
    fun getByUser(userId: Long): Flow<List<PresupuestoOcio>>

    @Query("SELECT * FROM PresupuestoOcio WHERE userId = :userId LIMIT 1")
    suspend fun getByUserOnce(userId: Long): PresupuestoOcio?

    @Query("DELETE FROM PresupuestoOcio WHERE userId = :userId")
    suspend fun deleteByUser(userId: Long)
}
