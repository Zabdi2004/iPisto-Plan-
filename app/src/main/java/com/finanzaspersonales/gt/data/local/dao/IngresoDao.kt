package com.finanzaspersonales.gt.data.local.dao

import androidx.room.*
import com.finanzaspersonales.gt.data.local.entity.Ingreso
import kotlinx.coroutines.flow.Flow

@Dao
interface IngresoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(ingreso: Ingreso): Long

    @Update
    suspend fun update(ingreso: Ingreso)

    @Delete
    suspend fun delete(ingreso: Ingreso)

    @Query("SELECT * FROM Ingreso WHERE userId = :userId ORDER BY id DESC")
    fun getAllByUser(userId: Long): Flow<List<Ingreso>>

    @Query("SELECT * FROM Ingreso WHERE userId = :userId ORDER BY id DESC")
    suspend fun getAllByUserOnce(userId: Long): List<Ingreso>

    @Query("SELECT * FROM Ingreso WHERE id = :id AND userId = :userId LIMIT 1")
    suspend fun findById(id: Long, userId: Long): Ingreso?

    @Query("DELETE FROM Ingreso WHERE userId = :userId")
    suspend fun deleteAllByUser(userId: Long)

    @Query("DELETE FROM Ingreso")
    suspend fun deleteAll()
}
