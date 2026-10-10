package com.finanzaspersonales.gt.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "GastoVariable",
    indices = [Index(value = ["userId"], name = "index_gastovariable_userId")]
)
data class GastoVariable(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val userId: Long,
    val nombre: String,
    val categoria: String,
    val cantidad: Double,
    val periodicidad: String,
    val fechaMillis: Long? = null
)
