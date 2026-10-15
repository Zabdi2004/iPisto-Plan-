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
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class OcioCalculatorTest {

    private val BD_SCALE = 2
    private val ROUND = RoundingMode.HALF_UP

    private fun bd(value: Double): BigDecimal =
        BigDecimal.valueOf(value).setScale(BD_SCALE, ROUND)

    private fun assertBdEquals(expected: BigDecimal, actual: BigDecimal) {
        assertEquals(0, expected.compareTo(actual))
    }

    private val ahora = System.currentTimeMillis()

    private fun ingresoMensual(cantidad: Double) =
        Ingreso(userId = 1, nombre = "Ingreso", cantidad = cantidad, periodicidad = "Mensual")

    private fun gastoFijoMensual(cantidad: Double, esOcio: Boolean = false, fechaMillis: Long? = null) =
        GastoFijo(userId = 1, nombre = "Gasto Fijo", categoria = "Otro", cantidad = cantidad, periodicidad = "Mensual", fechaMillis = fechaMillis, esOcio = esOcio)

    private fun gastoVariableMensual(cantidad: Double, esOcio: Boolean = false, fechaMillis: Long? = null) =
        GastoVariable(userId = 1, nombre = "Gasto Variable", categoria = "Otro", cantidad = cantidad, periodicidad = "Mensual", fechaMillis = fechaMillis, esOcio = esOcio)

    private fun gastoVarConFecha(cantidad: Double, esOcio: Boolean = true, fechaMilis: Long? = null): GastoVariable =
        GastoVariable(userId = 1, nombre = "Gasto Var", categoria = "Ocio", cantidad = cantidad, periodicidad = "Mensual", fechaMillis = fechaMilis, esOcio = esOcio)

    private fun gastoFijoConFecha(cantidad: Double, esOcio: Boolean = true, fechaMilis: Long? = null): GastoFijo =
        GastoFijo(userId = 1, nombre = "Gasto Fijo", categoria = "Ocio", cantidad = cantidad, periodicidad = "Mensual", fechaMillis = fechaMilis, esOcio = esOcio)

    private fun deudaMensual(pago: Double) =
        Deuda(userId = 1, nombre = "Deuda", montoTotal = 1000.0, pagoPeriodico = pago, periodicidad = "Mensual")

    private fun metaAhorro(aporte: Double) =
        MetaAhorro(userId = 1, nombre = "Meta", cantidadObjetivo = 1000.0, cantidadAhorrada = 0.0, aporteMensual = aporte)

    private fun presupuestoOcio(
        montoPresupuestado: Double,
        periodicidad: String = PeriodicidadPresupuesto.MENSUAL.name.lowercase(),
        rolloverEnabled: Boolean = false,
        fechaInicio: Long = ahora,
        remanenteAcumulado: Double = 0.0
    ) = PresupuestoOcio(
        userId = 1,
        montoPresupuestado = montoPresupuestado,
        periodicidad = periodicidad,
        rolloverEnabled = rolloverEnabled,
        fechaInicio = fechaInicio,
        remanenteAcumulado = remanenteAcumulado
    )

    // ===== 1 & 2: Formula de dinero disponible - sin deduplicación y sin ingresos en disponible =====

    @Test
    fun dineroDisponible_noDuplicaDeduccionesDeGastosDeudaYAhorro() {
        val ingreso = ingresoMensual(1000.0)
        val gastoFijo = gastoFijoMensual(200.0)
        val gastoVariable = gastoVariableMensual(150.0)
        val deuda = deudaMensual(100.0)
        val meta = metaAhorro(50.0)

        val result = OcioCalculator.calcularDineroDisponibleOcio(
            listOf(ingreso),
            listOf(gastoFijo),
            listOf(gastoVariable),
            listOf(deuda),
            listOf(meta)
        )

        assertBdEquals(bd(1000.0), result.ingresosEstimados)
        assertBdEquals(bd(350.0), result.gastosEsencialesEstimados)
        assertBdEquals(bd(100.0), result.pagosDeudaEstimados)
        assertBdEquals(bd(50.0), result.ahorroComprometido)
        assertBdEquals(bd(500.0), result.dineroDisponible)
        assertBdEquals(bd(500.0), result.saldoActual)
    }

    @Test
    fun dineroDisponible_ingresosNoSeConsideranDisponibles() {
        val result = OcioCalculator.calcularDineroDisponibleOcio(
            listOf(ingresoMensual(500.0)),
            listOf(gastoFijoMensual(300.0)),
            emptyList(),
            emptyList(),
            emptyList()
        )
        assertBdEquals(bd(200.0), result.dineroDisponible)
        assertBdEquals(bd(500.0), result.ingresosEstimados)
        assertBdEquals(bd(200.0), result.saldoActual)
    }

    @Test
    fun dineroDisponible_saldoNegativoSeClampaACero() {
        val result = OcioCalculator.calcularDineroDisponibleOcio(
            listOf(ingresoMensual(100.0)),
            listOf(gastoFijoMensual(200.0)),
            listOf(gastoVariableMensual(100.0)),
            listOf(deudaMensual(50.0)),
            listOf(metaAhorro(30.0))
        )
        assertBdEquals(bd(0.0), result.dineroDisponible)
        assertBdEquals(bd(-280.0), result.saldoActual)
    }

    @Test
    fun dineroDisponible_listasVaciasDanCero() {
        val result = OcioCalculator.calcularDineroDisponibleOcio(
            emptyList(), emptyList(), emptyList(), emptyList(), emptyList()
        )
        assertBdEquals(bd(0.0), result.dineroDisponible)
        assertBdEquals(bd(0.0), result.saldoActual)
    }

    @Test
    fun dineroDisponible_gastosVariablesYEsencialesNoSeDuplican() {
        val result = OcioCalculator.calcularDineroDisponibleOcio(
            listOf(ingresoMensual(2000.0)),
            listOf(gastoFijoMensual(300.0)),
            listOf(gastoVariableMensual(200.0)),
            emptyList(),
            emptyList()
        )
        assertBdEquals(bd(500.0), result.gastosEsencialesEstimados)
        assertBdEquals(bd(1500.0), result.dineroDisponible)
    }

    // ===== 3: Gastos de ocio se contabilizan una sola vez =====

    @Test
    fun presupuestoOcio_gastosOcioContabilizadosUnaSolavanza() {
        val fechaInicio = ahora
        val presupuesto = presupuestoOcio(1000.0, fechaInicio = fechaInicio)
        val gastoOcio = gastoVarConFecha(300.0, esOcio = true, fechaMilis = fechaInicio)

        val result = OcioCalculator.calcularPresupuestoOcio(presupuesto, listOf(gastoOcio), emptyList(), ahora)

        assertBdEquals(bd(300.0), result.gastosOcioDelPeriodo)
        assertBdEquals(bd(700.0), result.presupuestoRestante)
        assertEquals(0, bd(30.0).compareTo(result.porcentajeConsumido))
    }

    @Test
    fun presupuestoOcio_gastosNoOcioNoContanParaPresupuestoOcio() {
        val fecha = ahora
        val presupuesto = presupuestoOcio(1000.0, fechaInicio = fecha)
        val gastoNormal = gastoVariableMensual(500.0, esOcio = false, fechaMillis = fecha)
        val gastoOcio = gastoVariableMensual(300.0, esOcio = true, fechaMillis = fecha)

        val result = OcioCalculator.calcularPresupuestoOcio(presupuesto, listOf(gastoNormal, gastoOcio), emptyList(), ahora)

        assertBdEquals(bd(300.0), result.gastosOcioDelPeriodo)
        assertBdEquals(bd(700.0), result.presupuestoRestante)
    }

    // ===== 4: Periodos semanales y mensuales delimitan gastos =====

    @Test
    fun presupuestoOcio_filtroPorPeriodoMensual_excluyeGastosFueraDePeriodo() {
        val fechaInicio = ahora
        val presupuesto = presupuestoOcio(1000.0, periodicidad = "mensual", fechaInicio = fechaInicio)

        val gastoDentro = gastoVarConFecha(100.0, esOcio = true, fechaMilis = fechaInicio)
        val gastoFuera = gastoVarConFecha(500.0, esOcio = true, fechaMilis = fechaInicio - 40L * 24 * 3600_000)

        val result = OcioCalculator.calcularPresupuestoOcio(presupuesto, listOf(gastoDentro, gastoFuera), emptyList(), ahora)

        assertBdEquals(bd(100.0), result.gastosOcioDelPeriodo)
        assertBdEquals(bd(900.0), result.presupuestoRestante)
    }

    @Test
    fun presupuestoOcio_filtroPorPeriodoSemanal_excluyeGastosFueraDePeriodo() {
        val fechaInicio = ahora
        val presupuesto = presupuestoOcio(1000.0, periodicidad = "semanal", fechaInicio = fechaInicio)

        val dentro = gastoVarConFecha(100.0, esOcio = true, fechaMilis = fechaInicio)
        val fuera = gastoVarConFecha(500.0, esOcio = true, fechaMilis = fechaInicio - 8L * 24 * 3600_000)

        val result = OcioCalculator.calcularPresupuestoOcio(presupuesto, listOf(dentro, fuera), emptyList(), ahora)

        val expectedGastos = BigDecimal.valueOf(100.0 * 12 / 52).setScale(2, ROUND)
        assertBdEquals(expectedGastos, result.gastosOcioDelPeriodo)
        assertTrue(result.presupuestoRestante.compareTo(BigDecimal.ZERO) > 0)
    }

    // ===== 5: Reinicio y acumulación - remanente no se duplica =====

    @Test
    fun presupuestoOcio_remanenteAcumulado_noSeDuplicaEnCalculo() {
        val fechaInicio = ahora
        val presupuesto = presupuestoOcio(
            montoPresupuestado = 1000.0,
            fechaInicio = fechaInicio,
            remanenteAcumulado = 50.0
        )

        val result = OcioCalculator.calcularPresupuestoOcio(presupuesto, emptyList(), emptyList(), ahora)

        assertBdEquals(bd(1000.0), result.presupuestoConfigurado)
        assertBdEquals(bd(50.0), result.remanenteAcumulado)
        assertBdEquals(bd(1050.0), result.presupuestoRestante)
    }

    @Test
    fun presupuestoOcio_remanenteAcumulado_conGastosAcumulados_noDuplicaRemanente() {
        val fechaInicio = ahora
        val presupuesto = presupuestoOcio(
            montoPresupuestado = 1000.0,
            fechaInicio = fechaInicio,
            remanenteAcumulado = 100.0
        )
        val gastoOcio = gastoVarConFecha(200.0, esOcio = true, fechaMilis = fechaInicio)

        val result = OcioCalculator.calcularPresupuestoOcio(presupuesto, listOf(gastoOcio), emptyList(), ahora)

        assertBdEquals(bd(200.0), result.gastosOcioDelPeriodo)
        assertBdEquals(bd(900.0), result.presupuestoRestante)
        assertBdEquals(bd(100.0), result.remanenteAcumulado)
    }

    // ===== Estado del presupuesto =====

    @Test
    fun presupuestoOcio_nulo_retornaSIN_PRESUPUESTO() {
        val result = OcioCalculator.calcularPresupuestoOcio(null, emptyList(), emptyList(), ahora)
        assertEquals(EstadoPresupuesto.SIN_PRESUPUESTO, result.estadoPresupuesto)
        assertEquals(BigDecimal.ZERO, result.presupuestoConfigurado)
    }

    @Test
    fun presupuestoOcio_sinGastos_estadoNormal() {
        val presupuesto = presupuestoOcio(1000.0, fechaInicio = ahora)
        val result = OcioCalculator.calcularPresupuestoOcio(presupuesto, emptyList(), emptyList(), ahora)
        assertEquals(EstadoPresupuesto.NORMAL, result.estadoPresupuesto)
        assertBdEquals(bd(0.0), result.gastosOcioDelPeriodo)
        assertBdEquals(bd(1000.0), result.presupuestoRestante)
    }

    @Test
    fun presupuestoOcio_75porciento_estadoADVERTENCIA_75() {
        val presupuesto = presupuestoOcio(1000.0, fechaInicio = ahora)
        val gasto = gastoVarConFecha(750.0, esOcio = true, fechaMilis = ahora)

        val result = OcioCalculator.calcularPresupuestoOcio(presupuesto, listOf(gasto), emptyList(), ahora)
        assertEquals(EstadoPresupuesto.ADVERTENCIA_75, result.estadoPresupuesto)
        assertEquals(0, bd(75.0).compareTo(result.porcentajeConsumido))
    }

    @Test
    fun presupuestoOcio_90porciento_estadoADVERTENCIA_90() {
        val presupuesto = presupuestoOcio(1000.0, fechaInicio = ahora)
        val gasto = gastoVarConFecha(900.0, esOcio = true, fechaMilis = ahora)

        val result = OcioCalculator.calcularPresupuestoOcio(presupuesto, listOf(gasto), emptyList(), ahora)
        assertEquals(EstadoPresupuesto.ADVERTENCIA_90, result.estadoPresupuesto)
        assertEquals(0, bd(90.0).compareTo(result.porcentajeConsumido))
    }

    @Test
    fun presupuestoOcio_100porciento_estadoAGOTADO() {
        val presupuesto = presupuestoOcio(1000.0, fechaInicio = ahora)
        val gasto = gastoVarConFecha(1000.0, esOcio = true, fechaMilis = ahora)

        val result = OcioCalculator.calcularPresupuestoOcio(presupuesto, listOf(gasto), emptyList(), ahora)
        assertEquals(EstadoPresupuesto.AGOTADO, result.estadoPresupuesto)
        assertEquals(0, bd(100.0).compareTo(result.porcentajeConsumido))
    }

    @Test
    fun presupuestoOcio_gastosSuperanPresupuesto_restanteEsCero() {
        val presupuesto = presupuestoOcio(100.0, fechaInicio = ahora)
        val gasto = gastoVarConFecha(200.0, esOcio = true, fechaMilis = ahora)

        val result = OcioCalculator.calcularPresupuestoOcio(presupuesto, listOf(gasto), emptyList(), ahora)
        assertBdEquals(bd(0.0), result.presupuestoRestante)
    }

    @Test
    fun presupuestoOcio_gastoFijoEsOcio_seContabiliza() {
        val fechaInicio = ahora
        val presupuesto = presupuestoOcio(1000.0, fechaInicio = fechaInicio)
        val gastoFijoOcio = gastoFijoConFecha(200.0, esOcio = true, fechaMilis = fechaInicio)
        val gastoFijoNormal = gastoFijoConFecha(300.0, esOcio = false, fechaMilis = fechaInicio)

        val result = OcioCalculator.calcularPresupuestoOcio(presupuesto, emptyList(), listOf(gastoFijoOcio, gastoFijoNormal), ahora)
        assertBdEquals(bd(200.0), result.gastosOcioDelPeriodo)
        assertBdEquals(bd(800.0), result.presupuestoRestante)
    }

    // ===== 6: Simulación no modifica saldo ni registra transacciones =====

    @Test
    fun simularCompra_compraAsequible_disminuyeDisponible() {
        val presupuesto = presupuestoOcio(1000.0, fechaInicio = ahora)
        val result = OcioCalculator.calcularPresupuestoOcio(presupuesto, emptyList(), emptyList(), ahora)

        val simulacion = OcioCalculator.simularCompra(BigDecimal.valueOf(200.0), result)

        assertBdEquals(bd(200.0), simulacion.precioCompra)
        assertBdEquals(bd(1000.0), simulacion.disponibleAntes)
        assertBdEquals(bd(800.0), simulacion.disponibleDespues)
        assertEquals(0, bd(20.0).compareTo(simulacion.porcentajeConsumido))
    }

    @Test
    fun simularCompra_saldoInsuficiente_disponibleEsCero() {
        val presupuesto = presupuestoOcio(100.0, fechaInicio = ahora)
        val result = OcioCalculator.calcularPresupuestoOcio(presupuesto, emptyList(), emptyList(), ahora)

        val simulacion = OcioCalculator.simularCompra(BigDecimal.valueOf(150.0), result)

        assertBdEquals(bd(100.0), simulacion.disponibleAntes)
        assertBdEquals(bd(0.0), simulacion.disponibleDespues)
        assertTrue(simulacion.recomendacion.contains("No"))
    }

    @Test
    fun simularCompra_noModificaPresupuestoResult_original() {
        val presupuesto = presupuestoOcio(1000.0, fechaInicio = ahora)
        val result = OcioCalculator.calcularPresupuestoOcio(presupuesto, emptyList(), emptyList(), ahora)
        val restanteOriginal = result.presupuestoRestante

        OcioCalculator.simularCompra(BigDecimal.valueOf(300.0), result)

        assertEquals(0, restanteOriginal.compareTo(result.presupuestoRestante))
        assertBdEquals(bd(1000.0), result.presupuestoRestante)
    }

    @Test
    fun simularCompra_umbral90porciento_muestraAdvertencia() {
        val presupuesto = presupuestoOcio(1000.0, fechaInicio = ahora)
        val gasto = gastoVarConFecha(850.0, esOcio = true, fechaMilis = ahora)
        val result = OcioCalculator.calcularPresupuestoOcio(presupuesto, listOf(gasto), emptyList(), ahora)

        val simulacion = OcioCalculator.simularCompra(BigDecimal.valueOf(100.0), result)
        assertTrue(simulacion.recomendacion.contains("mucho"))
    }

    @Test
    fun simularCompra_compraPequena_muestraRecomendacionPositiva() {
        val presupuesto = presupuestoOcio(1000.0, fechaInicio = ahora)
        val result = OcioCalculator.calcularPresupuestoOcio(presupuesto, emptyList(), emptyList(), ahora)

        val simulacion = OcioCalculator.simularCompra(BigDecimal.valueOf(50.0), result)
        assertTrue(simulacion.recomendacion.contains("margen"))
    }

    // ===== 7: Aislamiento por usuario (verificado por reparcelación en repositorio) =====

    @Test
    fun calcularRemanenteAcumulable_devuelvePresupuestoRestante() {
        val presupuesto = presupuestoOcio(1000.0, fechaInicio = ahora)
        val gasto = gastoVarConFecha(300.0, esOcio = true, fechaMilis = ahora)
        val result = OcioCalculator.calcularPresupuestoOcio(presupuesto, listOf(gasto), emptyList(), ahora)

        val remanente = OcioCalculator.calcularRemanenteAcumulable(presupuesto, listOf(gasto), emptyList(), ahora)
        assertEquals(0, result.presupuestoRestante.compareTo(remanente))
        assertBdEquals(bd(700.0), remanente)
    }

    // ===== Pruebas de conversion de periodicidad =====

    @Test
    fun presupuestoOcio_gastoSemanal_enPresupuestoMensual_convierteCorrectamente() {
        val fechaInicio = ahora
        val presupuesto = presupuestoOcio(1000.0, periodicidad = "mensual", fechaInicio = fechaInicio)
        val gastoSemanal = GastoVariable(
            userId = 1, nombre = "Gasto", categoria = "Ocio",
            cantidad = 100.0, periodicidad = "Semanal",
            fechaMillis = fechaInicio, esOcio = true
        )

        val result = OcioCalculator.calcularPresupuestoOcio(presupuesto, listOf(gastoSemanal), emptyList(), ahora)

        val expectedMensual = BigDecimal.valueOf(100.0 * 52 / 12).setScale(2, ROUND)
        assertBdEquals(expectedMensual, result.gastosOcioDelPeriodo)
        assertTrue(result.presupuestoRestante.compareTo(BigDecimal.ZERO) > 0)
    }

    // ===== Pruebas de decimales =====

    @Test
    fun presupuestoOcio_redondeaADosDecimales() {
        val presupuesto = presupuestoOcio(10.0, fechaInicio = ahora)
        val gasto = gastoVarConFecha(3.333, esOcio = true, fechaMilis = ahora)

        val result = OcioCalculator.calcularPresupuestoOcio(presupuesto, listOf(gasto), emptyList(), ahora)

        val expectedGastos = BigDecimal.valueOf(3.333).setScale(2, ROUND)
        assertBdEquals(expectedGastos, result.gastosOcioDelPeriodo)
        assertEquals(2, result.gastosOcioDelPeriodo.scale())
    }

    @Test
    fun presupuestoOcio_conGastoFijoYVariableEsOcio_losDosContados() {
        val fechaInicio = ahora
        val presupuesto = presupuestoOcio(1000.0, fechaInicio = fechaInicio)
        val gastoFijo = gastoFijoConFecha(100.0, esOcio = true, fechaMilis = fechaInicio)
        val gastoVar = gastoVarConFecha(200.0, esOcio = true, fechaMilis = fechaInicio)

        val result = OcioCalculator.calcularPresupuestoOcio(presupuesto, listOf(gastoVar), listOf(gastoFijo), ahora)

        assertBdEquals(bd(300.0), result.gastosOcioDelPeriodo)
        assertBdEquals(bd(700.0), result.presupuestoRestante)
    }

    @Test
    fun presupuestoOcio_porcentajeConsumido_0porcentCuandoNoHayGastos() {
        val presupuesto = presupuestoOcio(1000.0, fechaInicio = ahora)
        val result = OcioCalculator.calcularPresupuestoOcio(presupuesto, emptyList(), emptyList(), ahora)
        assertEquals(0, bd(0.0).compareTo(result.porcentajeConsumido))
    }

    @Test
    fun presupuestoOcio_porcentajeConsumido_noExcede100() {
        val presupuesto = presupuestoOcio(100.0, fechaInicio = ahora)
        val gasto = gastoVarConFecha(500.0, esOcio = true, fechaMilis = ahora)

        val result = OcioCalculator.calcularPresupuestoOcio(presupuesto, listOf(gasto), emptyList(), ahora)

        assertTrue(result.porcentajeConsumido.compareTo(BigDecimal.valueOf(100)) <= 0)
        assertEquals(EstadoPresupuesto.AGOTADO, result.estadoPresupuesto)
    }

    // ===== Gastos de ocio recurrentes sin fecha (agenda vigente) =====

    @Test
    fun presupuestoOcio_gastoVariableRecurrenteSinFecha_seContabiliza() {
        val presupuesto = presupuestoOcio(1000.0, fechaInicio = ahora)
        val gastoRecurrente = gastoVariableMensual(250.0, esOcio = true, fechaMillis = null)

        val result = OcioCalculator.calcularPresupuestoOcio(presupuesto, listOf(gastoRecurrente), emptyList(), ahora)

        assertBdEquals(bd(250.0), result.gastosOcioDelPeriodo)
        assertBdEquals(bd(750.0), result.presupuestoRestante)
    }

    @Test
    fun presupuestoOcio_gastoFijoRecurrenteSinFecha_seContabiliza() {
        val presupuesto = presupuestoOcio(1000.0, fechaInicio = ahora)
        val gastoFijoRecurrente = gastoFijoMensual(100.0, esOcio = true, fechaMillis = null)
        val gastoFijoNormal = gastoFijoMensual(300.0, esOcio = false, fechaMillis = null)

        val result = OcioCalculator.calcularPresupuestoOcio(presupuesto, emptyList(), listOf(gastoFijoRecurrente, gastoFijoNormal), ahora)

        assertBdEquals(bd(100.0), result.gastosOcioDelPeriodo)
    }

    // ===== Normalización de periodo y rollover =====

    private fun restarDias(millis: Long, dias: Long): Long = millis - dias * 24L * 3600_000

    private fun gastoVariableSemanal(cantidad: Double, esOcio: Boolean = true) =
        GastoVariable(userId = 1, nombre = "Gasto semanal", categoria = "Ocio", cantidad = cantidad, periodicidad = "Semanal", fechaMillis = null, esOcio = esOcio)

    @Test
    fun normalizarPeriodoActual_presupuestoNulo_devuelveNull() {
        assertNull(OcioCalculator.normalizarPeriodoActual(null, emptyList(), emptyList(), ahora))
    }

    @Test
    fun normalizarPeriodoActual_periodoVigente_devuelveNull() {
        val presupuesto = presupuestoOcio(1000.0, fechaInicio = ahora, rolloverEnabled = true)

        assertNull(OcioCalculator.normalizarPeriodoActual(presupuesto, emptyList(), emptyList(), ahora))
    }

    @Test
    fun normalizarPeriodoActual_rolloverActivo_acumulaRemanenteNoGastado() {
        val presupuesto = presupuestoOcio(
            montoPresupuestado = 1000.0,
            periodicidad = "semanal",
            rolloverEnabled = true,
            fechaInicio = restarDias(ahora, 10)
        )
        val gastoSemanal = gastoVariableSemanal(200.0)

        val result = OcioCalculator.normalizarPeriodoActual(presupuesto, listOf(gastoSemanal), emptyList(), ahora)

        assertTrue(result != null)
        assertEquals(0, bd(800.0).compareTo(BigDecimal.valueOf(result!!.remanenteAcumulado).setScale(BD_SCALE, ROUND)))
        assertTrue(result.fechaInicio > presupuesto.fechaInicio)
    }

    @Test
    fun normalizarPeriodoActual_rolloverDesactivado_reiniciaRemanente() {
        val presupuesto = presupuestoOcio(
            montoPresupuestado = 1000.0,
            periodicidad = "semanal",
            rolloverEnabled = false,
            fechaInicio = restarDias(ahora, 10),
            remanenteAcumulado = 50.0
        )

        val result = OcioCalculator.normalizarPeriodoActual(presupuesto, emptyList(), emptyList(), ahora)

        assertTrue(result != null)
        assertEquals(0.0, result!!.remanenteAcumulado, 0.0001)
        assertTrue(result.fechaInicio > presupuesto.fechaInicio)
        assertNull(OcioCalculator.normalizarPeriodoActual(result, emptyList(), emptyList(), ahora))
    }

    @Test
    fun simularCompra_conRemanente_usaPresupuestoTotalDisponible() {
        val presupuesto = presupuestoOcio(1000.0, fechaInicio = ahora, remanenteAcumulado = 500.0)
        val result = OcioCalculator.calcularPresupuestoOcio(presupuesto, emptyList(), emptyList(), ahora)

        val simulacion = OcioCalculator.simularCompra(BigDecimal.valueOf(300.0), result)

        assertEquals(0, bd(20.0).compareTo(simulacion.porcentajeConsumido))
        assertBdEquals(bd(1200.0), simulacion.disponibleDespues)
    }
}
