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
    private const val UNICO = "Único"
    private const val DIARIO = "Diario"
    private const val SEMANAL = "Semanal"
    private const val QUINCENAL = "Quincenal"
    private const val MENSUAL = "Mensual"

    private fun mismoMes(fechaMillis: Long?): Boolean {
        if (fechaMillis == null) return false // Legacy rows have no fabricated transaction history.
        val utc = java.util.TimeZone.getTimeZone("UTC")
        val date = java.util.Calendar.getInstance(utc).apply { timeInMillis = fechaMillis }
        val now = java.util.Calendar.getInstance(utc)
        return date.get(java.util.Calendar.YEAR) == now.get(java.util.Calendar.YEAR) &&
            date.get(java.util.Calendar.MONTH) == now.get(java.util.Calendar.MONTH)
    }

    /**
     * Estimated monthly equivalent for comparing recurring amounts with different frequencies.
     * Daily uses 365/12, weekly 52/12, every-15-days 365/15/12, and monthly 1.
     * A one-time movement is actual only in the month of its recorded date; it is never annualized.
     * This is not a historical-period total: recurring entries describe schedules, not dated occurrences.
     */
    fun equivalenteMensualEstimado(cantidad: Double, periodicidad: String, fechaMillis: Long? = null): Double {
        require(cantidad.isFinite() && cantidad >= 0.0) { "Las cantidades deben ser finitas y no negativas" }
        return when (periodicidad) {
            UNICO -> if (mismoMes(fechaMillis)) cantidad else 0.0
            DIARIO -> cantidad * 365 / 12
            SEMANAL -> cantidad * 52 / 12
            QUINCENAL -> cantidad * 365 / 15 / 12
            MENSUAL -> cantidad
            else -> throw IllegalArgumentException("Periodicidad no reconocida: $periodicidad")
        }
    }

    /** Compatibility alias for existing callers; new UI and domain code should use the explicit name. */
    fun normalizarAMensual(cantidad: Double, periodicidad: String, fechaMillis: Long? = null): Double =
        equivalenteMensualEstimado(cantidad, periodicidad, fechaMillis)

    private fun checkedSum(values: List<Double>): Double = values.sum().also {
        require(it.isFinite()) { "El total calculado excede el rango permitido" }
    }

    fun calcularIngresosMensuales(ingresos: List<Ingreso>): Double {
        return checkedSum(ingresos.map { equivalenteMensualEstimado(it.cantidad, it.periodicidad, it.fechaMillis) })
    }

    fun calcularGastosFijosMensuales(gastos: List<GastoFijo>): Double {
        return checkedSum(gastos.map { equivalenteMensualEstimado(it.cantidad, it.periodicidad, it.fechaMillis) })
    }

    fun calcularGastosVariablesMensuales(gastos: List<GastoVariable>): Double {
        return checkedSum(gastos.map { equivalenteMensualEstimado(it.cantidad, it.periodicidad, it.fechaMillis) })
    }

    fun calcularPagosDeudaMensuales(deudas: List<Deuda>): Double {
        return checkedSum(deudas.map { equivalenteMensualEstimado(it.pagoPeriodico, it.periodicidad, it.fechaMillis) })
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
