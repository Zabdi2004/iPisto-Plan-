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
            try {
                val ingresos = finanzasRepository.getIngresosByUserOnce(userId)
                val gastosFijos = finanzasRepository.getGastosFijosByUserOnce(userId)
                val gastosVariables = finanzasRepository.getGastosVariablesByUserOnce(userId)
                val deudas = finanzasRepository.getDeudasByUserOnce(userId)
                val metas = metasRepository.getMetasByUserOnce(userId)
                val balance = BalanceCalculator.calcularBalance(ingresos, gastosFijos, gastosVariables, deudas)

                _uiState.value = FinanzasUiState(
                    ingresos = ingresos,
                    gastosFijos = gastosFijos,
                    gastosVariables = gastosVariables,
                    deudas = deudas,
                    metas = metas,
                    balance = balance,
                    isLoading = false
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Error al cargar datos"
                )
            }
        }
    }

    fun calcularBalance() {
        viewModelScope.launch {
            val ingresos = finanzasRepository.getIngresosByUserOnce(userId)
            val gastosFijos = finanzasRepository.getGastosFijosByUserOnce(userId)
            val gastosVariables = finanzasRepository.getGastosVariablesByUserOnce(userId)
            val deudas = finanzasRepository.getDeudasByUserOnce(userId)
            val balance = BalanceCalculator.calcularBalance(ingresos, gastosFijos, gastosVariables, deudas)

            _uiState.value = _uiState.value.copy(balance = balance)
        }
    }

    fun insertIngreso(ingreso: Ingreso) {
        viewModelScope.launch {
            val ingresoWithUser = ingreso.copy(userId = userId)
            finanzasRepository.insertIngreso(ingresoWithUser)
            loadData()
        }
    }

    fun updateIngreso(ingreso: Ingreso) {
        viewModelScope.launch {
            val ingresoWithUser = ingreso.copy(userId = userId)
            finanzasRepository.updateIngreso(ingresoWithUser)
            loadData()
        }
    }

    fun deleteIngreso(ingreso: Ingreso) {
        viewModelScope.launch {
            finanzasRepository.deleteIngreso(ingreso)
            loadData()
        }
    }

    fun insertGastoFijo(gastoFijo: GastoFijo) {
        viewModelScope.launch {
            val gastoWithUser = gastoFijo.copy(userId = userId)
            finanzasRepository.insertGastoFijo(gastoWithUser)
            loadData()
        }
    }

    fun updateGastoFijo(gastoFijo: GastoFijo) {
        viewModelScope.launch {
            val gastoWithUser = gastoFijo.copy(userId = userId)
            finanzasRepository.updateGastoFijo(gastoWithUser)
            loadData()
        }
    }

    fun deleteGastoFijo(gastoFijo: GastoFijo) {
        viewModelScope.launch {
            finanzasRepository.deleteGastoFijo(gastoFijo)
            loadData()
        }
    }

    fun insertGastoVariable(gastoVariable: GastoVariable) {
        viewModelScope.launch {
            val gastoWithUser = gastoVariable.copy(userId = userId)
            finanzasRepository.insertGastoVariable(gastoWithUser)
            loadData()
        }
    }

    fun updateGastoVariable(gastoVariable: GastoVariable) {
        viewModelScope.launch {
            val gastoWithUser = gastoVariable.copy(userId = userId)
            finanzasRepository.updateGastoVariable(gastoWithUser)
            loadData()
        }
    }

    fun deleteGastoVariable(gastoVariable: GastoVariable) {
        viewModelScope.launch {
            finanzasRepository.deleteGastoVariable(gastoVariable)
            loadData()
        }
    }

    fun insertDeuda(deuda: Deuda) {
        viewModelScope.launch {
            val deudaWithUser = deuda.copy(userId = userId)
            finanzasRepository.insertDeuda(deudaWithUser)
            loadData()
        }
    }

    fun updateDeuda(deuda: Deuda) {
        viewModelScope.launch {
            val deudaWithUser = deuda.copy(userId = userId)
            finanzasRepository.updateDeuda(deudaWithUser)
            loadData()
        }
    }

    fun deleteDeuda(deuda: Deuda) {
        viewModelScope.launch {
            finanzasRepository.deleteDeuda(deuda)
            loadData()
        }
    }

    fun insertMeta(meta: MetaAhorro) {
        viewModelScope.launch {
            val metaWithUser = meta.copy(userId = userId)
            metasRepository.insertMeta(metaWithUser)
            loadData()
        }
    }

    fun updateMeta(meta: MetaAhorro) {
        viewModelScope.launch {
            val metaWithUser = meta.copy(userId = userId)
            metasRepository.updateMeta(metaWithUser)
            loadData()
        }
    }

    fun deleteMeta(meta: MetaAhorro) {
        viewModelScope.launch {
            metasRepository.deleteMeta(meta)
            loadData()
        }
    }

    fun nuevaEvaluacion() {
        viewModelScope.launch {
            finanzasRepository.deleteAllFinancialData(userId)
            metasRepository.deleteAllMetasByUser(userId)
            loadData()
        }
    }

    fun formatCurrency(amount: Double): String = CurrencyFormatter.format(amount)
}
