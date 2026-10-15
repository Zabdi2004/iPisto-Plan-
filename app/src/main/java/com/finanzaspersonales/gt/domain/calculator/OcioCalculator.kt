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

    /** Límite de períodos que se pueden cerrar de golpe al normalizar un presupuesto antiguo. */
    private const val MAX_PERIODOS_ROLLOVER = 1200

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

        val gastosOcioDelPeriodo = calcularGastosOcioEnPeriodo(
            gastosVariables = gastosVariables,
            gastosFijos = gastosFijos,
            fechaInicioPeriodo = fechaInicioPeriodo,
            fechaFinPeriodo = fechaFinPeriodo,
            periodicidadPresupuesto = presupuesto.periodicidad
        )

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

    /**
     * Total de gastos marcados como ocio atribuibles al período indicado, expresado en la
     * periodicidad del presupuesto.
     */
    private fun calcularGastosOcioEnPeriodo(
        gastosVariables: List<GastoVariable>,
        gastosFijos: List<GastoFijo>,
        fechaInicioPeriodo: Long,
        fechaFinPeriodo: Long,
        periodicidadPresupuesto: String
    ): BigDecimal {
        val gastosVariablesOcio = gastosVariables.filter { gasto ->
            gasto.esOcio && perteneceAlPeriodo(gasto.fechaMillis, fechaInicioPeriodo, fechaFinPeriodo)
        }
        val gastosFijosOcio = gastosFijos.filter { gasto ->
            gasto.esOcio && perteneceAlPeriodo(gasto.fechaMillis, fechaInicioPeriodo, fechaFinPeriodo)
        }
        return (
            gastosVariablesOcio.sumOf { convertirAEquivalentePeriodo(it.cantidad, it.periodicidad, periodicidadPresupuesto) } +
                gastosFijosOcio.sumOf { convertirAEquivalentePeriodo(it.cantidad, it.periodicidad, periodicidadPresupuesto) }
            ).toBigDecimal().setScale(DOS_DECIMALES, RoundingMode.HALF_UP)
    }

    /**
     * Un gasto de ocio pertenece al período cuando no tiene fecha (los movimientos recurrentes
     * describen una agenda vigente, no una ocurrencia fechada) o cuando su fecha cae dentro del
     * período evaluado (movimientos únicos).
     */
    private fun perteneceAlPeriodo(fechaMillis: Long?, inicio: Long, fin: Long): Boolean =
        fechaMillis == null || fechaMillis in inicio..fin

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

    private fun sumarPeriodos(fechaBase: Long, periodicidad: String, periodos: Int): Long {
        val calendar = java.util.Calendar.getInstance().apply { timeInMillis = fechaBase }
        return when (periodicidad) {
            PeriodicidadPresupuesto.SEMANAL.name.lowercase() -> {
                calendar.add(java.util.Calendar.WEEK_OF_YEAR, periodos)
                calendar.timeInMillis
            }
            PeriodicidadPresupuesto.MENSUAL.name.lowercase() -> {
                calendar.add(java.util.Calendar.MONTH, periodos)
                calendar.timeInMillis
            }
            else -> Long.MAX_VALUE
        }
    }

    /**
     * Lleva el presupuesto al período vigente cuando [ahoraMillis] ya superó el cierre del período
     * reservado. Si el rollover está activo, el saldo no gastado de cada período cerrado se acumula
     * en [PresupuestoOcio.remanenteAcumulado]; si está desactivado, el presupuesto se reinicia y el
     * remanente vuelve a cero.
     *
     * Devuelve `null` cuando el presupuesto ya está en el período actual y no requiere cambios.
     */
    fun normalizarPeriodoActual(
        presupuesto: PresupuestoOcio?,
        gastosVariables: List<GastoVariable>,
        gastosFijos: List<GastoFijo>,
        ahoraMillis: Long
    ): PresupuestoOcio? {
        if (presupuesto == null) return null

        val fechaBase = presupuesto.fechaInicio
        val periodicidad = presupuesto.periodicidad
        val presupuestoBase = presupuesto.montoPresupuestado.toBd()

        var remanente = presupuesto.remanenteAcumulado.toBd()
        var inicioPeriodo = fechaBase
        var finPeriodo = sumarPeriodos(fechaBase, periodicidad, 1)
        var periodosCerrados = 0

        while (ahoraMillis > finPeriodo && periodosCerrados < MAX_PERIODOS_ROLLOVER && finPeriodo != Long.MAX_VALUE) {
            remanente = if (presupuesto.rolloverEnabled) {
                val gastado = calcularGastosOcioEnPeriodo(
                    gastosVariables = gastosVariables,
                    gastosFijos = gastosFijos,
                    fechaInicioPeriodo = inicioPeriodo,
                    fechaFinPeriodo = finPeriodo,
                    periodicidadPresupuesto = periodicidad
                )
                (presupuestoBase + remanente - gastado).coerceNonNegative()
            } else {
                BigDecimal.ZERO
            }
            periodosCerrados++
            inicioPeriodo = finPeriodo
            finPeriodo = sumarPeriodos(fechaBase, periodicidad, periodosCerrados + 1)
        }

        if (periodosCerrados == 0) return null

        return presupuesto.copy(
            fechaInicio = inicioPeriodo,
            remanenteAcumulado = remanente.toDouble()
        )
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
        val presupuestoTotalDisponible = presupuestoResult.presupuestoConfigurado + presupuestoResult.remanenteAcumulado
        val nuevoPorcentaje = if (presupuestoTotalDisponible.compareTo(BigDecimal.ZERO) > 0) {
            val nuevoGastado = presupuestoResult.gastosOcioDelPeriodo + simulacionAmount
            (nuevoGastado * CIEN / presupuestoTotalDisponible)
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
