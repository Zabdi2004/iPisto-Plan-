package com.finanzaspersonales.gt.data.local.dao

import androidx.room.*
import com.finanzaspersonales.gt.data.local.entity.Deuda
import kotlinx.coroutines.flow.Flow

@Dao
interface DeudaDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(deuda: Deuda): Long

    @Update
    suspend fun update(deuda: Deuda)

    @Delete
    suspend fun delete(deuda: Deuda)

    @Query("SELECT * FROM Deuda WHERE userId = :userId ORDER BY id DESC")
    fun getAllByUser(userId: Long): Flow<List<Deuda>>

    @Query("SELECT * FROM Deuda WHERE userId = :userId ORDER BY id DESC")
    suspend fun getAllByUserOnce(userId: Long): List<Deuda>

    @Query("SELECT * FROM Deuda WHERE id = :id AND userId = :userId LIMIT 1")
    suspend fun findById(id: Long, userId: Long): Deuda?

    @Query("DELETE FROM Deuda WHERE userId = :userId")
    suspend fun deleteAllByUser(userId: Long)

    @Query("DELETE FROM Deuda")
    suspend fun deleteAll()
}
