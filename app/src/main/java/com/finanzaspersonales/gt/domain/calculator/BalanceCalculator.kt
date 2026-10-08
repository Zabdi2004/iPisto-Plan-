package com.finanzaspersonales.gt.domain.calculator

import com.finanzaspersonales.gt.data.local.entity.Ingreso
import com.finanzaspersonales.gt.data.local.entity.GastoFijo
import com.finanzaspersonales.gt.data.local.entity.GastoVariable
import com.finanzaspersonales.gt.data.local.entity.Deuda

data class BalanceFinanciero(
    val ingresosMensuales: Double,
    val gastosFijosMensuales: Double,
    val gastosVariablesMensuales: Double,
    val pagosDeudaMensuales: Double,
    val deudaTotal: Double,
    val dineroDisponible: Double,
    val porcentajeUtilizado: Double,
    val estadoFinanciero: String
)

object BalanceCalculator {
    private const val SEMANAL = "Semanal"
    private const val QUINCENAL = "Quincenal"
    private const val MENSUAL = "Mensual"

    private fun normalizarAMensual(cantidad: Double, periodicidad: String): Double {
        return when (periodicity) {
            SEMANAL -> cantidad * 52 / 12
            QUINCENAL -> cantidad * 26 / 12
            MENSUAL -> cantidad
            else -> cantidad
        }
    }

    fun calcularIngresosMensuales(ingresos: List<Ingreso>): Double {
        return ingresos.sumOf { normalizarAMensual(it.cantidad, it.periodicidad) }
    }

    fun calcularGastosFijosMensuales(gastos: List<GastoFijo>): Double {
        return gastos.sumOf { normalizarAMensual(it.cantidad, it.periodicidad) }
    }

    fun calcularGastosVariablesMensuales(gastos: List<GastoVariable>): Double {
        return gastos.sumOf { normalizarAMensual(it.cantidad, it.periodicidad) }
    }

    fun calcularPagosDeudaMensuales(deudas: List<Deuda>): Double {
        return deudas.sumOf { normalizarAMensual(it.pagoPeriodico, it.periodicidad) }
    }

    fun calcularDeudaTotal(deudas: List<Deuda>): Double {
        return deudas.sumOf { it.montoTotal }
    }

    fun calcularBalance(
        ingresos: List<Ingreso>,
        gastosFijos: List<GastoFijo>,
        gastosVariables: List<GastoVariable>,
        deudas: List<Deuda>
    ): BalanceFinanciero {
        val ingresosMensuales = calcularIngresosMensuales(ingresos)
        val gastosFijosMensuales = calcularGastosFijosMensuales(gastosFijos)
        val gastosVariablesMensuales = calcularGastosVariablesMensuales(gastosVariables)
        val pagosDeudaMensuales = calcularPagosDeudaMensuales(deudas)
        val deudaTotal = calcularDeudaTotal(deudas)

        val dineroDisponible = ingresosMensuales - gastosFijosMensuales - gastosVariablesMensuales - pagosDeudaMensuales

        val porcentajeUtilizado = if (ingresosMensuales > 0) {
            ((gastosFijosMensuales + gastosVariablesMensuales + pagosDeudaMensuales) / ingresosMensuales) * 100
        } else 0.0

        val estadoFinanciero = when {
            porcentajeUtilizado < 60 -> "Saludable"
            porcentajeUtilizado < 80 -> "Precaución"
            else -> "Crítico"
        }

        return BalanceFinanciero(
            ingresosMensuales = ingresosMensuales,
            gastosFijosMensuales = gastosFijosMensuales,
            gastosVariablesMensuales = gastosVariablesMensuales,
            pagosDeudaMensuales = pagosDeudaMensuales,
            deudaTotal = deudaTotal,
            dineroDisponible = dineroDisponible,
            porcentajeUtilizado = porcentajeUtilizado,
            estadoFinanciero = estadoFinanciero
        )
    }
}
