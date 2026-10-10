package com.finanzaspersonales.gt.domain.calculator

import com.finanzaspersonales.gt.data.local.entity.Deuda
import com.finanzaspersonales.gt.data.local.entity.GastoFijo
import com.finanzaspersonales.gt.data.local.entity.GastoVariable
import com.finanzaspersonales.gt.data.local.entity.Ingreso
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test

class BalanceCalculatorTest {
    @Test fun normalizesWeeklyBiweeklyAndMonthlyIncome() {
        val monthly = BalanceCalculator.calcularIngresosMensuales(listOf(
            Ingreso(userId = 1, nombre = "Semanal", cantidad = 120.0, periodicidad = "Semanal"),
            Ingreso(userId = 1, nombre = "Quincenal", cantidad = 300.0, periodicidad = "Quincenal"),
            Ingreso(userId = 1, nombre = "Mensual", cantidad = 500.0, periodicidad = "Mensual")
        ))
        assertEquals(120.0 * 52 / 12 + 300.0 * 365 / 15 / 12 + 500.0, monthly, 0.000001)
    }

    @Test fun normalizesExpensesAndDebtPayments() {
        assertEquals(52.0, BalanceCalculator.calcularGastosFijosMensuales(listOf(
            GastoFijo(userId = 1, nombre = "Renta", categoria = "Hogar", cantidad = 12.0, periodicidad = "Semanal")
        )), 0.000001)
        assertEquals(30.0 * 365 / 15 / 12, BalanceCalculator.calcularGastosVariablesMensuales(listOf(
            GastoVariable(userId = 1, nombre = "Compra", categoria = "Comida", cantidad = 30.0, periodicidad = "Quincenal")
        )), 0.000001)
        assertEquals(100.0, BalanceCalculator.calcularPagosDeudaMensuales(listOf(
            Deuda(userId = 1, nombre = "Préstamo", montoTotal = 1000.0, pagoPeriodico = 100.0, periodicidad = "Mensual")
        )), 0.000001)
    }

    @Test fun calculatesPositiveAndNegativeBalancesAndDebtTotal() {
        val positive = BalanceCalculator.calcularBalance(
            listOf(Ingreso(userId = 1, nombre = "Trabajo", cantidad = 1000.0, periodicidad = "Mensual")),
            emptyList(), emptyList(), emptyList()
        )
        assertEquals(1000.0, positive.dineroDisponible, 0.0)
        val negative = BalanceCalculator.calcularBalance(
            listOf(Ingreso(userId = 1, nombre = "Trabajo", cantidad = 100.0, periodicidad = "Mensual")),
            listOf(GastoFijo(userId = 1, nombre = "Renta", categoria = "Hogar", cantidad = 150.0, periodicidad = "Mensual")),
            emptyList(), listOf(Deuda(userId = 1, nombre = "Préstamo", montoTotal = 800.0, pagoPeriodico = 0.0, periodicidad = "Mensual"))
        )
        assertEquals(-50.0, negative.dineroDisponible, 0.0)
        assertEquals(800.0, negative.deudaTotal, 0.0)
    }

    @Test fun usesExactFinancialStatusBoundaries() {
        assertEquals("Saludable", balanceWithExpenseRatio(59.999).estadoFinanciero)
        assertEquals("Precaución", balanceWithExpenseRatio(60.0).estadoFinanciero)
        assertEquals("Precaución", balanceWithExpenseRatio(79.999).estadoFinanciero)
        assertEquals("Crítico", balanceWithExpenseRatio(80.0).estadoFinanciero)
    }

    @Test fun zeroIncomeDoesNotPresentAnInventedPercentage() {
        val result = BalanceCalculator.calcularBalance(emptyList(), emptyList(), emptyList(), emptyList())
        assertNull(result.porcentajeUtilizado)
        assertEquals("Sin ingresos", result.estadoFinanciero)
        assertTrue(result.dineroDisponible == 0.0)
    }

    @Test fun normalizesDailyAndSingleMovementsForCurrentMonthOnly() {
        val now = System.currentTimeMillis()
        assertEquals(10.0 * 365 / 12, BalanceCalculator.normalizarAMensual(10.0, "Diario"), .000001)
        assertEquals(10.0 * 365 / 15 / 12, BalanceCalculator.normalizarAMensual(10.0, "Quincenal"), .000001)
        assertEquals(75.0, BalanceCalculator.normalizarAMensual(75.0, "Único", now), .000001)
        val oldDate = java.util.Calendar.getInstance().apply { add(java.util.Calendar.MONTH, -2) }.timeInMillis
        assertEquals(0.0, BalanceCalculator.normalizarAMensual(75.0, "Único", oldDate), 0.0)
        assertEquals(0.0, BalanceCalculator.normalizarAMensual(75.0, "Único", null), 0.0)
    }

    @Test fun rejectsNegativeNonFiniteAndUnknownPeriodAmounts() {
        assertThrows(IllegalArgumentException::class.java) {
            BalanceCalculator.calcularIngresosMensuales(listOf(Ingreso(userId = 1, nombre = "x", cantidad = -1.0, periodicidad = "Mensual")))
        }
        assertThrows(IllegalArgumentException::class.java) {
            BalanceCalculator.calcularIngresosMensuales(listOf(Ingreso(userId = 1, nombre = "x", cantidad = Double.NaN, periodicidad = "Mensual")))
        }
        assertThrows(IllegalArgumentException::class.java) {
            BalanceCalculator.calcularIngresosMensuales(listOf(Ingreso(userId = 1, nombre = "x", cantidad = 1.0, periodicidad = "Anual")))
        }
    }

    private fun balanceWithExpenseRatio(ratio: Double) = BalanceCalculator.calcularBalance(
        listOf(Ingreso(userId = 1, nombre = "Trabajo", cantidad = 100.0, periodicidad = "Mensual")),
        listOf(GastoFijo(userId = 1, nombre = "Gasto", categoria = "Otro", cantidad = ratio, periodicidad = "Mensual")),
        emptyList(), emptyList()
    )
}
