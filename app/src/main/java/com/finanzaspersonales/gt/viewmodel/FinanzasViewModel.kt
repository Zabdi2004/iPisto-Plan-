package com.finanzaspersonales.gt.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.finanzaspersonales.gt.data.local.entity.Ingreso
import com.finanzaspersonales.gt.data.local.entity.GastoFijo
import com.finanzaspersonales.gt.data.local.entity.GastoVariable
import com.finanzaspersonales.gt.data.local.entity.Deuda
import com.finanzaspersonales.gt.data.local.entity.MetaAhorro
import com.finanzaspersonales.gt.data.local.entity.PresupuestoOcio
import com.finanzaspersonales.gt.data.local.entity.PeriodicidadPresupuesto
import com.finanzaspersonales.gt.data.repository.FinanzasRepository
import com.finanzaspersonales.gt.data.repository.MetasRepository
import com.finanzaspersonales.gt.data.repository.PresupuestoOcioRepository
import com.finanzaspersonales.gt.domain.calculator.BalanceCalculator
import com.finanzaspersonales.gt.domain.calculator.BalanceFinanciero
import com.finanzaspersonales.gt.domain.calculator.DineroDisponibleOcioResult
import com.finanzaspersonales.gt.domain.calculator.EstadoPresupuesto
import com.finanzaspersonales.gt.domain.calculator.OcioCalculator
import com.finanzaspersonales.gt.domain.calculator.OcioPresupuestoResult
import com.finanzaspersonales.gt.domain.calculator.SimulacionCompraResult
import com.finanzaspersonales.gt.utils.CurrencyFormatter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.retryWhen
import kotlinx.coroutines.CancellationException
import java.math.BigDecimal

data class FinanzasUiState(
    val ingresos: List<Ingreso> = emptyList(),
    val gastosFijos: List<GastoFijo> = emptyList(),
    val gastosVariables: List<GastoVariable> = emptyList(),
    val deudas: List<Deuda> = emptyList(),
    val metas: List<MetaAhorro> = emptyList(),
    val presupuestoOcio: PresupuestoOcio? = null,
    val balance: BalanceFinanciero? = null,
    val dineroDisponibleOcio: DineroDisponibleOcioResult? = null,
    val presupuestoResult: OcioPresupuestoResult? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

data class DatosFinancieros(
    val ingresos: List<Ingreso>,
    val gastosFijos: List<GastoFijo>,
    val gastosVariables: List<GastoVariable>,
    val deudas: List<Deuda>,
    val metas: List<MetaAhorro>
)

class FinanzasViewModel(
    private val finanzasRepository: FinanzasRepository,
    private val metasRepository: MetasRepository,
    private val presupuestoOcioRepository: PresupuestoOcioRepository,
    private val userId: Long
) : ViewModel() {

    private val _uiState = MutableStateFlow(FinanzasUiState())
    val uiState: StateFlow<FinanzasUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        _uiState.value = _uiState.value.copy(isLoading = true)
        viewModelScope.launch {
            val datosFinancieros = combine(
                finanzasRepository.getIngresosByUser(),
                finanzasRepository.getGastosFijosByUser(),
                finanzasRepository.getGastosVariablesByUser(),
                finanzasRepository.getDeudasByUser(),
                metasRepository.getMetasByUser()
            ) { ingresos, gastosFijos, gastosVariables, deudas, metas ->
                DatosFinancieros(ingresos, gastosFijos, gastosVariables, deudas, metas)
            }
            combine(datosFinancieros, presupuestoOcioRepository.getByUser()) { df, presupuestoList ->
                val ingresos = df.ingresos
                val gastosFijos = df.gastosFijos
                val gastosVariables = df.gastosVariables
                val deudas = df.deudas
                val metas = df.metas
                val ahoraMillis = System.currentTimeMillis()
                val presupuestoGuardado = presupuestoList.firstOrNull()
                val presupuesto = runCatching {
                    OcioCalculator.normalizarPeriodoActual(presupuestoGuardado, gastosVariables, gastosFijos, ahoraMillis)
                }.getOrNull() ?: presupuestoGuardado
                val calculation = runCatching { BalanceCalculator.calcularBalance(ingresos, gastosFijos, gastosVariables, deudas) }
                val dineroOcio = runCatching { OcioCalculator.calcularDineroDisponibleOcio(ingresos, gastosFijos, gastosVariables, deudas, metas) }
                val presupuestoResult = runCatching {
                    OcioCalculator.calcularPresupuestoOcio(presupuesto, gastosVariables, gastosFijos, ahoraMillis)
                }
                FinanzasUiState(
                    ingresos = ingresos,
                    gastosFijos = gastosFijos,
                    gastosVariables = gastosVariables,
                    deudas = deudas,
                    metas = metas,
                    presupuestoOcio = presupuesto,
                    balance = calculation.getOrNull(),
                    dineroDisponibleOcio = dineroOcio.getOrNull(),
                    presupuestoResult = presupuestoResult.getOrNull(),
                    isLoading = false,
                    error = calculation.exceptionOrNull()?.localizedMessage
                )
            }.retryWhen { cause, attempt ->
                _uiState.value = _uiState.value.copy(isLoading = false, error = cause.localizedMessage ?: "Error al cargar datos")
                delay((1000L * (1L shl attempt.coerceAtMost(5).toInt())).coerceAtMost(30_000L))
                true
            }.catch { e ->
                _uiState.value = _uiState.value.copy(isLoading = false, error = e.message ?: "Error al cargar datos")
            }.collect { _uiState.value = it }
        }
    }

    private fun mutate(onComplete: (Boolean) -> Unit = {}, action: suspend () -> Result<*>) {
        viewModelScope.launch {
            val result: Result<*> = try {
                action()
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (failure: Exception) {
                Result.failure<Any?>(failure)
            }
            result.fold(
                onSuccess = { _uiState.value = _uiState.value.copy(error = null); onComplete(true) },
                onFailure = { _uiState.value = _uiState.value.copy(error = it.localizedMessage ?: "No se pudo guardar el registro"); onComplete(false) }
            )
        }
    }

    fun insertIngreso(ingreso: Ingreso, onComplete: (Boolean) -> Unit = {}) {
        val ingresoWithUser = ingreso.copy(userId = userId)
        mutate(onComplete) { finanzasRepository.insertIngreso(ingresoWithUser) }
    }

    fun updateIngreso(ingreso: Ingreso, onComplete: (Boolean) -> Unit = {}) {
        val ingresoWithUser = ingreso.copy(userId = userId)
        mutate(onComplete) { finanzasRepository.updateIngreso(ingresoWithUser) }
    }

    fun deleteIngreso(ingreso: Ingreso) = mutate { finanzasRepository.deleteIngreso(ingreso) }

    fun insertGastoFijo(gastoFijo: GastoFijo, onComplete: (Boolean) -> Unit = {}) =
        mutate(onComplete) { finanzasRepository.insertGastoFijo(gastoFijo.copy(userId = userId)) }

    fun updateGastoFijo(gastoFijo: GastoFijo, onComplete: (Boolean) -> Unit = {}) =
        mutate(onComplete) { finanzasRepository.updateGastoFijo(gastoFijo.copy(userId = userId)) }

    fun deleteGastoFijo(gastoFijo: GastoFijo) = mutate { finanzasRepository.deleteGastoFijo(gastoFijo) }

    fun insertGastoVariable(gastoVariable: GastoVariable, onComplete: (Boolean) -> Unit = {}) =
        mutate(onComplete) { finanzasRepository.insertGastoVariable(gastoVariable.copy(userId = userId)) }

    fun updateGastoVariable(gastoVariable: GastoVariable, onComplete: (Boolean) -> Unit = {}) =
        mutate(onComplete) { finanzasRepository.updateGastoVariable(gastoVariable.copy(userId = userId)) }

    fun deleteGastoVariable(gastoVariable: GastoVariable) = mutate { finanzasRepository.deleteGastoVariable(gastoVariable) }

    fun insertDeuda(deuda: Deuda, onComplete: (Boolean) -> Unit = {}) =
        mutate(onComplete) { finanzasRepository.insertDeuda(deuda.copy(userId = userId)) }

    fun updateDeuda(deuda: Deuda, onComplete: (Boolean) -> Unit = {}) =
        mutate(onComplete) { finanzasRepository.updateDeuda(deuda.copy(userId = userId)) }

    fun deleteDeuda(deuda: Deuda) = mutate { finanzasRepository.deleteDeuda(deuda) }

    fun insertMeta(meta: MetaAhorro, onComplete: (Boolean) -> Unit = {}) =
        mutate(onComplete) { metasRepository.insertMeta(meta.copy(userId = userId)) }

    fun updateMeta(meta: MetaAhorro, onComplete: (Boolean) -> Unit = {}) =
        mutate(onComplete) { metasRepository.updateMeta(meta.copy(userId = userId)) }

    fun deleteMeta(meta: MetaAhorro, onComplete: (Boolean) -> Unit = {}) =
        mutate(onComplete) { metasRepository.deleteMeta(meta) }

    fun nuevaEvaluacion() {
        viewModelScope.launch {
            finanzasRepository.deleteAllFinancialData()
            metasRepository.deleteAllMetasByUser()
            presupuestoOcioRepository.deleteByUser()
        }
    }

    fun savePresupuestoOcio(presupuesto: PresupuestoOcio, onComplete: (Boolean) -> Unit = {}) {
        mutate(onComplete) {
            presupuestoOcioRepository.saveOrUpdate(presupuesto.copy(userId = userId))
        }
    }

    fun deletePresupuestoOcio(onComplete: (Boolean) -> Unit = {}) {
        mutate(onComplete) {
            presupuestoOcioRepository.deleteByUser()
        }
    }

    fun simularCompra(montoStr: String): SimulacionCompraResult? {
        val monto = runCatching {
            val bd = BigDecimal(montoStr.replace(",", "."))
            if (!montoStr.contains("-")) bd.setScale(2, java.math.RoundingMode.HALF_UP) else null
        }.getOrNull() ?: return null

        val presupuestoResult = _uiState.value.presupuestoResult ?: return null
        return OcioCalculator.simularCompra(monto, presupuestoResult)
    }

    fun marcarGastoVariableComoOcio(id: Long, esOcio: Boolean) {
        viewModelScope.launch {
            val gasto = finanzasRepository.getGastosVariablesByUserOnce().find { it.id == id }
            if (gasto != null) {
                finanzasRepository.updateGastoVariable(gasto.copy(esOcio = esOcio))
            }
        }
    }

    fun marcarGastoFijoComoOcio(id: Long, esOcio: Boolean) {
        viewModelScope.launch {
            val gasto = finanzasRepository.getGastosFijosByUserOnce().find { it.id == id }
            if (gasto != null) {
                finanzasRepository.updateGastoFijo(gasto.copy(esOcio = esOcio))
            }
        }
    }

    fun formatCurrency(amount: Double): String = CurrencyFormatter.format(amount)

    fun formatCurrencyBd(amount: BigDecimal): String {
        val scaled = amount.setScale(2, java.math.RoundingMode.HALF_UP)
        return CurrencyFormatter.format(scaled.toDouble())
    }
}
