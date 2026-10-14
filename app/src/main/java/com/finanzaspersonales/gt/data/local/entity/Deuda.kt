package com.finanzaspersonales.gt.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "Deuda",
    indices = [Index(value = ["userId"], name = "index_deuda_userId")]
)
data class Deuda(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val userId: Long,
    val nombre: String,
    val montoTotal: Double,
    val pagoPeriodico: Double,
    val periodicidad: String,
    val fechaMillis: Long? = null
)
