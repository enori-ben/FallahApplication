package com.example.fallahapplication.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fallahapplication.data.repository.FallahRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DebtViewModel @Inject constructor(
    private val repository: FallahRepository
) : ViewModel() {

    // ✅ تحديث تلقائي من Room مباشرة — لا حاجة لـ trigger
    val customersWithDebt = repository.getCustomersWithDebt()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val totalUnpaidDebt = repository.getTotalUnpaidDebt()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    // ✅ دالة تسديد مباشرة من شاشة الديون
    fun recordPayment(customerId: Long, amount: Double, notes: String) {
        viewModelScope.launch {
            repository.recordPayment(customerId, amount, notes)
        }
    }

    fun loadDebtData() {
        // لا شيء — البيانات تتحدث تلقائياً من Flow
    }
}