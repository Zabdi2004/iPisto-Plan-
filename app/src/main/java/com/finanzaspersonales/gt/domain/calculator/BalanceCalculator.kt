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
    val porcentajeUtilizado: Double?,
    val estadoFinanciero: String
)

object BalanceCalculator {
    private const val SEMANAL = "Semanal"
    private const val QUINCENAL = "Quincenal"
    private const val MENSUAL = "Mensual"

    fun normalizarAMensual(cantidad: Double, periodicidad: String): Double {
        require(cantidad.isFinite() && cantidad >= 0.0) { "Las cantidades deben ser finitas y no negativas" }
        return when (periodicidad) {
            SEMANAL -> cantidad * 52 / 12
            QUINCENAL -> cantidad * 26 / 12
            MENSUAL -> cantidad
            else -> throw IllegalArgumentException("Periodicidad no reconocida: $periodicidad")
        }
    }

    private fun checkedSum(values: List<Double>): Double = values.sum().also {
        require(it.isFinite()) { "El total calculado excede el rango permitido" }
    }

    fun calcularIngresosMensuales(ingresos: List<Ingreso>): Double {
        return checkedSum(ingresos.map { normalizarAMensual(it.cantidad, it.periodicidad) })
    }

    fun calcularGastosFijosMensuales(gastos: List<GastoFijo>): Double {
        return checkedSum(gastos.map { normalizarAMensual(it.cantidad, it.periodicidad) })
    }

    fun calcularGastosVariablesMensuales(gastos: List<GastoVariable>): Double {
        return checkedSum(gastos.map { normalizarAMensual(it.cantidad, it.periodicidad) })
    }

    fun calcularPagosDeudaMensuales(deudas: List<Deuda>): Double {
        return checkedSum(deudas.map { normalizarAMensual(it.pagoPeriodico, it.periodicidad) })
    }

    fun calcularDeudaTotal(deudas: List<Deuda>): Double {
        return checkedSum(deudas.map { it.montoTotal.also { amount -> require(amount.isFinite() && amount >= 0.0) } })
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

        val gastosMensuales = checkedSum(listOf(gastosFijosMensuales, gastosVariablesMensuales, pagosDeudaMensuales))
        val dineroDisponible = (ingresosMensuales - gastosMensuales).also {
            require(it.isFinite()) { "El balance calculado excede el rango permitido" }
        }

        val porcentajeUtilizado = if (ingresosMensuales > 0) {
            (gastosMensuales / ingresosMensuales * 100).also {
                require(it.isFinite()) { "El porcentaje calculado excede el rango permitido" }
            }
        } else null

        val estadoFinanciero = when {
            porcentajeUtilizado == null -> "Sin ingresos"
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
