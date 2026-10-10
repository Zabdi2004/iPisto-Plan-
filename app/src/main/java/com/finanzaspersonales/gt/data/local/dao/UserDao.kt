package com.finanzaspersonales.gt.data.local.dao

import androidx.room.*
import com.finanzaspersonales.gt.data.local.entity.User
import kotlinx.coroutines.flow.Flow

@Dao
interface UserTypeDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(user: User): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertIfNotExists(user: User): Long

    @Query("SELECT * FROM User WHERE id = :id LIMIT 1")
    suspend fun findById(id: Long): User?

    @Query("SELECT * FROM User WHERE nombreUsuario = :username LIMIT 1")
    suspend fun findByUsername(username: String): User?

    @Query("SELECT COUNT(*) FROM User WHERE nombreUsuario = :username")
    suspend fun countByUsername(username: String): Int

    @Query("SELECT * FROM User")
    fun getAllUsers(): Flow<List<User>>

    @Query("DELETE FROM User WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("UPDATE User SET passwordHash = :passwordHash WHERE id = :id")
    suspend fun updatePassword(id: Long, passwordHash: String)

    @Query("DELETE FROM User")
    suspend fun deleteAll()
}
