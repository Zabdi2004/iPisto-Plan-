package com.finanzaspersonales.gt.data.local.dao

import androidx.room.*
import com.finanzaspersonales.gt.data.local.entity.GastoVariable
import kotlinx.coroutines.flow.Flow

@Dao
interface GastoVariableDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(gastoVariable: GastoVariable): Long

    @Update
    suspend fun update(gastoVariable: GastoVariable)

    @Delete
    suspend fun delete(gastoVariable: GastoVariable)

    @Query("SELECT * FROM GastoVariable WHERE userId = :userId ORDER BY id DESC")
    fun getAllByUser(userId: Long): Flow<List<GastoVariable>>

    @Query("SELECT * FROM GastoVariable WHERE userId = :userId ORDER BY id DESC")
    suspend fun getAllByUserOnce(userId: Long): List<GastoVariable>

    @Query("SELECT * FROM GastoVariable WHERE id = :id AND userId = :userId LIMIT 1")
    suspend fun findById(id: Long, userId: Long): GastoVariable?

    @Query("DELETE FROM GastoVariable WHERE userId = :userId")
    suspend fun deleteAllByUser(userId: Long)

    @Query("DELETE FROM GastoVariable")
    suspend fun deleteAll()
}
