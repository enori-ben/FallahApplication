package com.example.fallahapplication.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fallahapplication.data.model.Product
import com.example.fallahapplication.data.repository.FallahRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductViewModel @Inject constructor(
    private val repository: FallahRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _selectedCategoryId = MutableStateFlow<Long?>(null)

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val products: StateFlow<List<Product>> =
        combine(_searchQuery, _selectedCategoryId) { query, catId ->
            Pair(query, catId)
        }.flatMapLatest { (query, catId) ->
            when {
                query.isNotBlank() -> repository.searchProducts(query)
                catId != null -> repository.getProductsByCategory(catId)
                else -> repository.getAllProducts()
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setCategoryFilter(categoryId: Long?) {
        _selectedCategoryId.value = categoryId
    }

    suspend fun getProductById(id: Long): Product? = repository.getProductById(id)

    fun saveProduct(product: Product) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                if (product.id == 0L) {
                    repository.insertProduct(product)
                } else {
                    repository.updateProduct(product)
                }
            } finally {
                _isLoading.value = false
            }
        }
    }
    fun getProductsByCategory(categoryId: Long): Flow<List<Product>> {
        return repository.getProductsByCategory(categoryId)
    }

    fun deleteProduct(product: Product) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                repository.deleteProduct(product)
            } finally {
                _isLoading.value = false
            }
        }
    }
}