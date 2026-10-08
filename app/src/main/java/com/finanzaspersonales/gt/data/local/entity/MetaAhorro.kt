package com.finanzaspersonales.gt.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "MetaAhorro",
    indices = [Index(value = ["userId"], name = "index_metaahorro_userId")]
)
data class MetaAhorro(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val userId: Long,
    val nombre: String,
    val cantidadObjetivo: Double,
    val cantidadAhorrada: Double,
    val aporteMensual: Double
)
