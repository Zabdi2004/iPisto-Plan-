package com.finanzaspersonales.gt.domain.calculator

import com.finanzaspersonales.gt.data.local.entity.Deuda
import com.finanzaspersonales.gt.data.local.entity.GastoFijo
import com.finanzaspersonales.gt.data.local.entity.GastoVariable
import com.finanzaspersonales.gt.data.local.entity.Ingreso
import com.finanzaspersonales.gt.data.local.entity.MetaAhorro
import com.finanzaspersonales.gt.data.local.entity.PresupuestoOcio
import com.finanzaspersonales.gt.data.local.entity.PeriodicidadPresupuesto
import java.math.BigDecimal
import java.math.RoundingMode

data class OcioPresupuestoResult(
    val presupuestoConfigurado: BigDecimal,
    val gastosOcioDelPeriodo: BigDecimal,
    val presupuestoRestante: BigDecimal,
    val porcentajeConsumido: BigDecimal,
    val estadoPresupuesto: EstadoPresupuesto,
    val remanenteAcumulado: BigDecimal
)

enum class EstadoPresupuesto {
    NORMAL,
    ADVERTENCIA_75,
    ADVERTENCIA_90,
    AGOTADO,
    SIN_PRESUPUESTO
}

data class DineroDisponibleOcioResult(
    val ingresosEstimados: BigDecimal,
    val gastosEsencialesEstimados: BigDecimal,
    val pagosDeudaEstimados: BigDecimal,
    val ahorroComprometido: BigDecimal,
    val dineroDisponible: BigDecimal,
    val saldoActual: BigDecimal,
    val ingresosPendientes: BigDecimal,
    val gastosEsencialesPendientes: BigDecimal,
    val pagosDeudaPendientes: BigDecimal
)

object OcioCalculator {

    private val CIEN = BigDecimal("100")
    private val UNO = BigDecimal.ONE
    private val DOS_DECIMALES = 2

    private fun Double.toBd() = BigDecimal.valueOf(this).setScale(DOS_DECIMALES, RoundingMode.HALF_UP)

    private fun BigDecimal.coerceNonNegative(): BigDecimal = if (compareTo(BigDecimal.ZERO) < 0) BigDecimal.ZERO else this

    fun calcularDineroDisponibleOcio(
        ingresos: List<Ingreso>,
        gastosFijos: List<GastoFijo>,
        gastosVariables: List<GastoVariable>,
        deudas: List<Deuda>,
        metas: List<MetaAhorro>
    ): DineroDisponibleOcioResult {
        val ingresosEstimados = BalanceCalculator.calcularIngresosMensuales(ingresos).toBd()
        val gastosFijosEstimados = BalanceCalculator.calcularGastosFijosMensuales(gastosFijos).toBd()
        val gastosVariablesEstimados = BalanceCalculator.calcularGastosVariablesMensuales(gastosVariables).toBd()
        val pagosDeudaEstimados = BalanceCalculator.calcularPagosDeudaMensuales(deudas).toBd()
        val ahorroComprometido = metas.sumOf { it.aporteMensual.toDouble() }.toDouble().toBd()

        val gastosEsencialesEstimados = gastosFijosEstimados + gastosVariablesEstimados

        val saldoActual = ingresosEstimados - gastosEsencialesEstimados - pagosDeudaEstimados - ahorroComprometido
        val saldoActualNoNegativo = saldoActual.coerceNonNegative()

        val dineroDisponible = saldoActualNoNegativo

        return DineroDisponibleOcioResult(
            ingresosEstimados = ingresosEstimados,
            gastosEsencialesEstimados = gastosEsencialesEstimados,
            pagosDeudaEstimados = pagosDeudaEstimados,
            ahorroComprometido = ahorroComprometido,
            dineroDisponible = dineroDisponible,
            saldoActual = saldoActual,
            ingresosPendientes = BigDecimal.ZERO,
            gastosEsencialesPendientes = BigDecimal.ZERO,
            pagosDeudaPendientes = BigDecimal.ZERO
        )
    }

    fun calcularPresupuestoOcio(
        presupuesto: PresupuestoOcio?,
        gastosVariables: List<GastoVariable>,
        gastosFijos: List<GastoFijo>,
        ahoraMillis: Long
    ): OcioPresupuestoResult {
        if (presupuesto == null) {
            return OcioPresupuestoResult(
                presupuestoConfigurado = BigDecimal.ZERO,
                gastosOcioDelPeriodo = BigDecimal.ZERO,
                presupuestoRestante = BigDecimal.ZERO,
                porcentajeConsumido = BigDecimal.ZERO,
                estadoPresupuesto = EstadoPresupuesto.SIN_PRESUPUESTO,
                remanenteAcumulado = BigDecimal.ZERO
            )
        }

        val fechaInicioPeriodo = presupuesto.fechaInicio
        val fechaFinPeriodo = calcularFechaFinPeriodo(fechaInicioPeriodo, presupuesto.periodicidad, ahoraMillis)

        val gastosVariablesOcio = gastosVariables.filter { gasto ->
            gasto.esOcio && gasto.fechaMillis != null &&
                gasto.fechaMillis!! in fechaInicioPeriodo..fechaFinPeriodo
        }
        val gastosFijosOcio = gastosFijos.filter { gasto ->
            gasto.esOcio && gasto.fechaMillis != null &&
                gasto.fechaMillis!! in fechaInicioPeriodo..fechaFinPeriodo
        }
        val gastosOcioDelPeriodo = (
            gastosVariablesOcio.sumOf { convertirAEquivalentePeriodo(it.cantidad, it.periodicidad, presupuesto.periodicidad) } +
            gastosFijosOcio.sumOf { convertirAEquivalentePeriodo(it.cantidad, it.periodicidad, presupuesto.periodicidad) }
        ).toBigDecimal().setScale(DOS_DECIMALES, RoundingMode.HALF_UP)

        val presupuestoBase = presupuesto.montoPresupuestado.toBd()
        val remanenteAcumulado = presupuesto.remanenteAcumulado.toBd()
        val presupuestoTotalDisponible = presupuestoBase + remanenteAcumulado

        val presupuestoRestante = (presupuestoTotalDisponible - gastosOcioDelPeriodo).coerceNonNegative()
        val porcentajeConsumido = if (presupuestoTotalDisponible.compareTo(BigDecimal.ZERO) > 0) {
            (gastosOcioDelPeriodo * CIEN / presupuestoTotalDisponible)
                .setScale(DOS_DECIMALES, RoundingMode.HALF_UP)
                .coerceAtMost(CIEN)
        } else {
            BigDecimal.ZERO
        }

        val estadoPresupuesto = determinarEstado(presupuestoRestante, presupuestoTotalDisponible, gastosOcioDelPeriodo)

        return OcioPresupuestoResult(
            presupuestoConfigurado = presupuestoBase,
            gastosOcioDelPeriodo = gastosOcioDelPeriodo,
            presupuestoRestante = presupuestoRestante,
            porcentajeConsumido = porcentajeConsumido,
            estadoPresupuesto = estadoPresupuesto,
            remanenteAcumulado = remanenteAcumulado
        )
    }

    private fun convertirAEquivalentePeriodo(cantidad: Double, periodicidadGasto: String, periodicidadPresupuesto: String): Double {
        val mensual = BalanceCalculator.equivalenteMensualEstimado(cantidad, periodicidadGasto, null)
        return when (periodicidadPresupuesto) {
            PeriodicidadPresupuesto.SEMANAL.name.lowercase() -> mensual * 12 / 52
            else -> mensual
        }
    }

    private fun Double.toBigDecimal(): BigDecimal = BigDecimal.valueOf(this)

    private fun calcularFechaFinPeriodo(fechaInicio: Long, periodicidad: String, ahoraMillis: Long): Long {
        val calendar = java.util.Calendar.getInstance().apply { timeInMillis = fechaInicio }
        return when {
            periodicidad == PeriodicidadPresupuesto.SEMANAL.name.lowercase() -> {
                calendar.add(java.util.Calendar.WEEK_OF_YEAR, 1)
                calendar.timeInMillis
            }
            periodicidad == PeriodicidadPresupuesto.MENSUAL.name.lowercase() -> {
                calendar.add(java.util.Calendar.MONTH, 1)
                calendar.timeInMillis
            }
            else -> ahoraMillis
        }
    }

    private fun determinarEstado(
        restante: BigDecimal,
        presupuestoTotal: BigDecimal,
        gastado: BigDecimal
    ): EstadoPresupuesto {
        if (presupuestoTotal.compareTo(BigDecimal.ZERO) <= 0) {
            return EstadoPresupuesto.SIN_PRESUPUESTO
        }
        if (gastado >= presupuestoTotal) {
            return EstadoPresupuesto.AGOTADO
        }
        val porcentaje = gastado * CIEN / presupuestoTotal
        return when {
            porcentaje >= BigDecimal.valueOf(90) -> EstadoPresupuesto.ADVERTENCIA_90
            porcentaje >= BigDecimal.valueOf(75) -> EstadoPresupuesto.ADVERTENCIA_75
            else -> EstadoPresupuesto.NORMAL
        }
    }

    fun simularCompra(
        simulacionAmount: BigDecimal,
        presupuestoResult: OcioPresupuestoResult
    ): SimulacionCompraResult {
        val nuevoRestante = (presupuestoResult.presupuestoRestante - simulacionAmount).coerceNonNegative()
        val nuevoPorcentaje = if (presupuestoResult.presupuestoConfigurado.compareTo(BigDecimal.ZERO) > 0) {
            val nuevoGastado = presupuestoResult.gastosOcioDelPeriodo + simulacionAmount
            (nuevoGastado * CIEN / presupuestoResult.presupuestoConfigurado)
                .setScale(DOS_DECIMALES, RoundingMode.HALF_UP)
                .coerceAtMost(CIEN)
        } else {
            BigDecimal.ZERO
        }

        val recomendacion = when {
            simulacionAmount > presupuestoResult.presupuestoRestante ->
                "No hay presupuesto suficiente para esta compra."
            nuevoPorcentaje >= BigDecimal.valueOf(90) ->
                "Esta compra consumiría mucho de tu presupuesto de ocio."
            nuevoPorcentaje >= BigDecimal.valueOf(75) ->
                "Esta compra superaría el umbral de advertencia."
            else ->
                "Puedes permitirte esta compra con margen."
        }

        return SimulacionCompraResult(
            precioCompra = simulacionAmount,
            disponibleAntes = presupuestoResult.presupuestoRestante,
            disponibleDespues = nuevoRestante,
            porcentajeConsumido = nuevoPorcentaje,
            recomendacion = recomendacion
        )
    }

    fun calcularRemanenteAcumulable(
        presupuesto: PresupuestoOcio?,
        gastosVariables: List<GastoVariable>,
        gastosFijos: List<GastoFijo>,
        ahoraMillis: Long
    ): BigDecimal {
        val result = calcularPresupuestoOcio(presupuesto, gastosVariables, gastosFijos, ahoraMillis)
        return result.presupuestoRestante
    }
}

data class SimulacionCompraResult(
    val precioCompra: BigDecimal,
    val disponibleAntes: BigDecimal,
    val disponibleDespues: BigDecimal,
    val porcentajeConsumido: BigDecimal,
    val recomendacion: String
)
