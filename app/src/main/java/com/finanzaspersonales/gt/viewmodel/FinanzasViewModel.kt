package com.finanzaspersonales.gt.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.finanzaspersonales.gt.data.local.entity.Ingreso
import com.finanzaspersonales.gt.data.local.entity.GastoFijo
import com.finanzaspersonales.gt.data.local.entity.GastoVariable
import com.finanzaspersonales.gt.data.local.entity.Deuda
import com.finanzaspersonales.gt.data.local.entity.MetaAhorro
import com.finanzaspersonales.gt.data.repository.FinanzasRepository
import com.finanzaspersonales.gt.data.repository.MetasRepository
import com.finanzaspersonales.gt.domain.calculator.BalanceCalculator
import com.finanzaspersonales.gt.domain.calculator.BalanceFinanciero
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

data class FinanzasUiState(
    val ingresos: List<Ingreso> = emptyList(),
    val gastosFijos: List<GastoFijo> = emptyList(),
    val gastosVariables: List<GastoVariable> = emptyList(),
    val deudas: List<Deuda> = emptyList(),
    val metas: List<MetaAhorro> = emptyList(),
    val balance: BalanceFinanciero? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

class FinanzasViewModel(
    private val finanzasRepository: FinanzasRepository,
    private val metasRepository: MetasRepository,
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
            combine(
                finanzasRepository.getIngresosByUser(),
                finanzasRepository.getGastosFijosByUser(),
                finanzasRepository.getGastosVariablesByUser(),
                finanzasRepository.getDeudasByUser(),
                metasRepository.getMetasByUser()
            ) { ingresos, gastosFijos, gastosVariables, deudas, metas ->
                val calculation = runCatching { BalanceCalculator.calcularBalance(ingresos, gastosFijos, gastosVariables, deudas) }
                FinanzasUiState(
                    ingresos = ingresos,
                    gastosFijos = gastosFijos,
                    gastosVariables = gastosVariables,
                    deudas = deudas,
                    metas = metas,
                    balance = calculation.getOrNull(),
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

    fun calcularBalance() {
        viewModelScope.launch {
            runCatching {
                BalanceCalculator.calcularBalance(
                    finanzasRepository.getIngresosByUserOnce(),
                    finanzasRepository.getGastosFijosByUserOnce(),
                    finanzasRepository.getGastosVariablesByUserOnce(),
                    finanzasRepository.getDeudasByUserOnce()
                )
            }.onSuccess { _uiState.value = _uiState.value.copy(balance = it, error = null) }
                .onFailure { _uiState.value = _uiState.value.copy(error = it.localizedMessage ?: "No se pudo calcular el balance") }
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

    fun deleteMeta(meta: MetaAhorro) = mutate { metasRepository.deleteMeta(meta) }

    fun nuevaEvaluacion() {
        viewModelScope.launch {
            finanzasRepository.deleteAllFinancialData()
            metasRepository.deleteAllMetasByUser()
        }
    }

    fun formatCurrency(amount: Double): String = CurrencyFormatter.format(amount)
}
