package com.finanzaspersonales.gt.utils

import com.finanzaspersonales.gt.data.local.entity.Ingreso
import com.finanzaspersonales.gt.data.local.entity.GastoFijo
import com.finanzaspersonales.gt.data.local.entity.GastoVariable
import com.finanzaspersonales.gt.data.local.entity.Deuda
import com.finanzaspersonales.gt.domain.calculator.BalanceCalculator

fun Ingreso.formatMonthly(): Double {
    return BalanceCalculator.normalizarAMensual(cantidad, periodicidad, fechaMillis)
}

fun GastoFijo.formatMonthly(): Double {
    return BalanceCalculator.normalizarAMensual(cantidad, periodicidad, fechaMillis)
}

fun GastoVariable.formatMonthly(): Double {
    return BalanceCalculator.normalizarAMensual(cantidad, periodicidad, fechaMillis)
}

fun Deuda.formatMonthly(): Double {
    return BalanceCalculator.normalizarAMensual(pagoPeriodico, periodicidad, fechaMillis)
}
