package com.finanzaspersonales.gt.data.local.dao

import androidx.room.*
import com.finanzaspersonales.gt.data.local.entity.GastoFijo
import kotlinx.coroutines.flow.Flow

@Dao
interface GastoFijoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(gastoFijo: GastoFijo): Long

    @Update
    suspend fun update(gastoFijo: GastoFijo)

    @Delete
    suspend fun delete(gastoFijo: GastoFijo)

    @Query("SELECT * FROM GastoFijo WHERE userId = :userId ORDER BY id DESC")
    fun getAllByUser(userId: Long): Flow<List<GastoFijo>>

    @Query("SELECT * FROM GastoFijo WHERE userId = :userId ORDER BY id DESC")
    suspend fun getAllByUserOnce(userId: Long): List<GastoFijo>

    @Query("SELECT * FROM GastoFijo WHERE id = :id AND userId = :userId LIMIT 1")
    suspend fun findById(id: Long, userId: Long): GastoFijo?

    @Query("DELETE FROM GastoFijo WHERE userId = :userId")
    suspend fun deleteAllByUser(userId: Long)

    @Query("DELETE FROM GastoFijo")
    suspend fun deleteAll()
}
