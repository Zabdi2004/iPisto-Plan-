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
                val balance = BalanceCalculator.calcularBalance(ingresos, gastosFijos, gastosVariables, deudas)
                FinanzasUiState(
                    ingresos = ingresos,
                    gastosFijos = gastosFijos,
                    gastosVariables = gastosVariables,
                    deudas = deudas,
                    metas = metas,
                    balance = balance,
                    isLoading = false
                )
            }.catch { e ->
                _uiState.value = _uiState.value.copy(isLoading = false, error = e.message ?: "Error al cargar datos")
            }.collect { _uiState.value = it }
        }
    }

    fun calcularBalance() {
        viewModelScope.launch {
            val ingresos = finanzasRepository.getIngresosByUserOnce()
            val gastosFijos = finanzasRepository.getGastosFijosByUserOnce()
            val gastosVariables = finanzasRepository.getGastosVariablesByUserOnce()
            val deudas = finanzasRepository.getDeudasByUserOnce()
            val balance = BalanceCalculator.calcularBalance(ingresos, gastosFijos, gastosVariables, deudas)

            _uiState.value = _uiState.value.copy(balance = balance)
        }
    }

    fun insertIngreso(ingreso: Ingreso) {
        viewModelScope.launch {
            val ingresoWithUser = ingreso.copy(userId = userId)
            finanzasRepository.insertIngreso(ingresoWithUser)
        }
    }

    fun updateIngreso(ingreso: Ingreso) {
        viewModelScope.launch {
            val ingresoWithUser = ingreso.copy(userId = userId)
            finanzasRepository.updateIngreso(ingresoWithUser)
        }
    }

    fun deleteIngreso(ingreso: Ingreso) {
        viewModelScope.launch {
            finanzasRepository.deleteIngreso(ingreso)
        }
    }

    fun insertGastoFijo(gastoFijo: GastoFijo) {
        viewModelScope.launch {
            val gastoWithUser = gastoFijo.copy(userId = userId)
            finanzasRepository.insertGastoFijo(gastoWithUser)
        }
    }

    fun updateGastoFijo(gastoFijo: GastoFijo) {
        viewModelScope.launch {
            val gastoWithUser = gastoFijo.copy(userId = userId)
            finanzasRepository.updateGastoFijo(gastoWithUser)
        }
    }

    fun deleteGastoFijo(gastoFijo: GastoFijo) {
        viewModelScope.launch {
            finanzasRepository.deleteGastoFijo(gastoFijo)
        }
    }

    fun insertGastoVariable(gastoVariable: GastoVariable) {
        viewModelScope.launch {
            val gastoWithUser = gastoVariable.copy(userId = userId)
            finanzasRepository.insertGastoVariable(gastoWithUser)
        }
    }

    fun updateGastoVariable(gastoVariable: GastoVariable) {
        viewModelScope.launch {
            val gastoWithUser = gastoVariable.copy(userId = userId)
            finanzasRepository.updateGastoVariable(gastoWithUser)
        }
    }

    fun deleteGastoVariable(gastoVariable: GastoVariable) {
        viewModelScope.launch {
            finanzasRepository.deleteGastoVariable(gastoVariable)
        }
    }

    fun insertDeuda(deuda: Deuda) {
        viewModelScope.launch {
            val deudaWithUser = deuda.copy(userId = userId)
            finanzasRepository.insertDeuda(deudaWithUser)
        }
    }

    fun updateDeuda(deuda: Deuda) {
        viewModelScope.launch {
            val deudaWithUser = deuda.copy(userId = userId)
            finanzasRepository.updateDeuda(deudaWithUser)
        }
    }

    fun deleteDeuda(deuda: Deuda) {
        viewModelScope.launch {
            finanzasRepository.deleteDeuda(deuda)
        }
    }

    fun insertMeta(meta: MetaAhorro) {
        viewModelScope.launch {
            val metaWithUser = meta.copy(userId = userId)
            metasRepository.insertMeta(metaWithUser)
        }
    }

    fun updateMeta(meta: MetaAhorro) {
        viewModelScope.launch {
            val metaWithUser = meta.copy(userId = userId)
            metasRepository.updateMeta(metaWithUser)
        }
    }

    fun deleteMeta(meta: MetaAhorro) {
        viewModelScope.launch {
            metasRepository.deleteMeta(meta)
        }
    }

    fun nuevaEvaluacion() {
        viewModelScope.launch {
            finanzasRepository.deleteAllFinancialData()
            metasRepository.deleteAllMetasByUser()
        }
    }

    fun formatCurrency(amount: Double): String = CurrencyFormatter.format(amount)
}
