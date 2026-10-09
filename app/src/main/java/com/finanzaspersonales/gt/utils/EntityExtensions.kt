package com.finanzaspersonales.gt.utils

import com.finanzaspersonales.gt.data.local.entity.Ingreso
import com.finanzaspersonales.gt.data.local.entity.GastoFijo
import com.finanzaspersonales.gt.data.local.entity.GastoVariable
import com.finanzaspersonales.gt.data.local.entity.Deuda

fun Ingreso.formatMonthly(): Double {
    return when (periodicidad) {
        "Semanal" -> cantidad * 52 / 12
        "Quincenal" -> cantidad * 26 / 12
        else -> cantidad // Mensual
    }
}

fun GastoFijo.formatMonthly(): Double {
    return when (periodicidad) {
        "Semanal" -> cantidad * 52 / 12
        "Quincenal" -> cantidad * 26 / 12
        else -> cantidad
    }
}

fun GastoVariable.formatMonthly(): Double {
    return when (periodicidad) {
        "Semanal" -> cantidad * 52 / 12
        "Quincenal" -> cantidad * 26 / 12
        else -> cantidad
    }
}

fun Deuda.formatMonthly(): Double {
    return when (periodicidad) {
        "Semanal" -> pagoPeriodico * 52 / 12
        "Quincenal" -> pagoPeriodico * 26 / 12
        else -> pagoPeriodico
    }
}
