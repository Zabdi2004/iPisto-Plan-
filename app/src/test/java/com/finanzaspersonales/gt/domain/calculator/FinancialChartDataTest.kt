package com.finanzaspersonales.gt.domain.calculator

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class FinancialChartDataTest {
    @Test fun offersExactlyThreeDisplayModesAndDefaultsToLines() {
        assertEquals(listOf(ChartType.LINES, ChartType.BARS, ChartType.SCATTER), ChartType.values().toList())
        assertEquals(ChartType.LINES, ChartType.values().first())
    }

    @Test fun chartDataHasSameMonthlyBalanceValuesRegardlessOfVisualMode() {
        val balance = BalanceFinanciero(1000.0, 200.0, 100.0, 50.0, 500.0, 650.0, null, "SALUDABLE")
        val points = FinancialChartData.fromBalance(balance)
        assertEquals(listOf("Ingresos", "Gastos fijos", "Gastos variables", "Pagos de deuda", "Disponible"), points.map { it.label })
        assertEquals(listOf(1000.0, 200.0, 100.0, 50.0, 650.0), points.map { it.amount })
        ChartType.values().forEach { assertEquals(points, FinancialChartData.fromBalance(balance)) }
    }

    @Test fun missingBalanceProducesEmptyDataForEmptyState() {
        assertTrue(FinancialChartData.fromBalance(null).isEmpty())
    }
}
