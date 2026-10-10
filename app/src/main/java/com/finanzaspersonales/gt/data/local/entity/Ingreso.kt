package com.finanzaspersonales.gt.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "Ingreso",
    indices = [Index(value = ["userId"], name = "index_ingreso_userId")]
)
data class Ingreso(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val userId: Long,
    val nombre: String,
    val cantidad: Double,
    val periodicidad: String,
    /** Nullable for legacy records whose transaction date was never stored. Unix time in millis. */
    val fechaMillis: Long? = null
)
