package com.finanzaspersonales.gt.domain.calculator

enum class ChartType { LINES, BARS, SCATTER }

data class FinancialChartPoint(val label: String, val amount: Double)

object FinancialChartData {
    fun fromBalance(balance: BalanceFinanciero?): List<FinancialChartPoint> = balance?.let {
        listOf(
            FinancialChartPoint("Ingresos", it.ingresosMensuales),
            FinancialChartPoint("Gastos fijos", it.gastosFijosMensuales),
            FinancialChartPoint("Gastos variables", it.gastosVariablesMensuales),
            FinancialChartPoint("Pagos de deuda", it.pagosDeudaMensuales),
            FinancialChartPoint("Disponible", it.dineroDisponible)
        )
    }.orEmpty()
}
