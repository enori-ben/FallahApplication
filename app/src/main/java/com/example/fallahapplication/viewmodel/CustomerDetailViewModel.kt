package com.example.fallahapplication.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fallahapplication.data.model.Debt
import com.example.fallahapplication.data.model.Payment
import com.example.fallahapplication.data.model.Sale
import com.example.fallahapplication.data.model.SaleItem
import com.example.fallahapplication.data.repository.FallahRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SaleWithItems(
    val sale: Sale,
    val items: List<SaleItem>
)

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class CustomerDetailViewModel @Inject constructor(
    private val repository: FallahRepository
) : ViewModel() {

    private val _customerId = MutableStateFlow(0L)

    val customer = _customerId.flatMapLatest { id ->
        flow {
            emit(repository.getCustomerById(id))
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val debts = _customerId.flatMapLatest { id ->
        repository.getDebtsByCustomer(id)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val payments = _customerId.flatMapLatest { id ->
        repository.getPaymentsByCustomer(id)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val sales = _customerId.flatMapLatest { id ->
        repository.getSalesByCustomer(id)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val remainingDebt = debts.map { debtsList ->
        debtsList.sumOf { it.remainingAmount }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val salesWithItems: StateFlow<List<SaleWithItems>> = sales.flatMapLatest { salesList ->
        flow {
            val result = mutableListOf<SaleWithItems>()
            for (sale in salesList) {
                val items = repository.getSaleItemsSync(sale.id)
                result.add(SaleWithItems(sale = sale, items = items))
            }
            emit(result)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun loadCustomerData(customerId: Long) {
        _customerId.value = customerId
    }

    fun recordPayment(amount: Double, notes: String) {
        viewModelScope.launch {
            val id = _customerId.value
            if (id > 0) {
                repository.recordPayment(id, amount, notes)
                loadCustomerData(id)
            }
        }
    }
}