package com.example.fallahapplication.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fallahapplication.data.repository.FallahRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class ReportViewModel @Inject constructor(
    private val repository: FallahRepository
) : ViewModel() {

    val todayRevenue = repository.getTodayRevenue()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val todayProfit = repository.getTodayProfit()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val todaySalesCount = repository.getTodaySalesCount()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val monthRevenue = repository.getMonthRevenue()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val totalUnpaidDebt = repository.getTotalUnpaidDebt()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val topProducts = repository.getTopSellingProducts()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val recentSales = repository.getAllSales()
        .map { it.take(10) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val lowStockProducts = repository.getLowStockProducts()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
}