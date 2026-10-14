package com.finanzaspersonales.gt.data.local.dao

import androidx.room.*
import com.finanzaspersonales.gt.data.local.entity.MetaAhorro
import kotlinx.coroutines.flow.Flow

@Dao
interface MetaAhorroDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(metaAhorro: MetaAhorro): Long

    @Update
    suspend fun update(metaAhorro: MetaAhorro)

    @Delete
    suspend fun delete(metaAhorro: MetaAhorro)

    @Query("SELECT * FROM MetaAhorro WHERE userId = :userId ORDER BY id DESC")
    fun getAllByUser(userId: Long): Flow<List<MetaAhorro>>

    @Query("SELECT * FROM MetaAhorro WHERE userId = :userId ORDER BY id DESC")
    suspend fun getAllByUserOnce(userId: Long): List<MetaAhorro>

    @Query("SELECT * FROM MetaAhorro WHERE id = :id AND userId = :userId LIMIT 1")
    suspend fun findById(id: Long, userId: Long): MetaAhorro?

    @Query("DELETE FROM MetaAhorro WHERE userId = :userId")
    suspend fun deleteAllByUser(userId: Long)

    @Query("DELETE FROM MetaAhorro")
    suspend fun deleteAll()
}
