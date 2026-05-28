package com.example.fallahapplication.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fallahapplication.data.model.Customer
import com.example.fallahapplication.data.repository.FallahRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class CustomerViewModel @Inject constructor(
    private val repository: FallahRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    val customers = _searchQuery.flatMapLatest { query ->
        if (query.isBlank()) repository.getAllCustomers()
        else repository.searchCustomers(query)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val customersCount = repository.getAllCustomers()
        .map { it.size }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    fun setSearchQuery(query: String) { _searchQuery.value = query }

    fun saveCustomer(customer: Customer) {
        viewModelScope.launch {
            if (customer.id == 0L) repository.insertCustomer(customer)
            else repository.updateCustomer(customer)
        }
    }

    fun deleteCustomer(customer: Customer) {
        viewModelScope.launch {
            repository.deleteCustomer(customer)
        }
    }
}