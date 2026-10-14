package com.finanzaspersonales.gt.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "PresupuestoOcio",
    indices = [Index(value = ["userId"], name = "index_presupuestocio_userId")]
)
data class PresupuestoOcio(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val userId: Long,
    val montoPresupuestado: Double,
    val periodicidad: String,
    val rolloverEnabled: Boolean,
    val fechaInicio: Long,
    val remanenteAcumulado: Double = 0.0
)

enum class PeriodicidadPresupuesto(val esMensual: Boolean) {
    SEMANAL(false),
    MENSUAL(true)
}
