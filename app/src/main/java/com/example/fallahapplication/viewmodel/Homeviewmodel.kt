package com.example.fallahapplication.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fallahapplication.data.model.Category
import com.example.fallahapplication.data.repository.FallahRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: FallahRepository
) : ViewModel() {

    private val _categories = MutableStateFlow<List<Category>>(emptyList())
    val categories: StateFlow<List<Category>> = _categories.asStateFlow()
    private var defaultCategoriesAdded = false

    val todayRevenue = repository.getTodayRevenue()
        .catch { emit(0.0) }
        .stateIn(viewModelScope, SharingStarted.Eagerly, 0.0)

    val todayProfit = repository.getTodayProfit()
        .catch { emit(0.0) }
        .stateIn(viewModelScope, SharingStarted.Eagerly, 0.0)

    val todaySalesCount = repository.getTodaySalesCount()
        .catch { emit(0) }
        .stateIn(viewModelScope, SharingStarted.Eagerly, 0)

    val totalUnpaidDebt = repository.getTotalUnpaidDebt()
        .catch { emit(0.0) }
        .stateIn(viewModelScope, SharingStarted.Eagerly, 0.0)

    val lowStockProducts = repository.getLowStockProducts()
        .catch { emit(emptyList()) }
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    // ✅ عدد الزبائن الحقيقي (ليس عدد الفواتير)
    val customersCount = repository.getAllCustomers()
        .catch { emit(emptyList()) }
        .map { it.size }
        .stateIn(viewModelScope, SharingStarted.Eagerly, 0)

    init { loadCategories() }

    private fun loadCategories() {
        viewModelScope.launch {
            repository.getAllCategories()
                .catch { emit(emptyList()) }
                .collect { list ->
                    val unique = list.distinctBy { it.name }
                    _categories.value = unique
                    if (!defaultCategoriesAdded && unique.isEmpty()) {
                        defaultCategoriesAdded = true
                        addDefaultCategories()
                    }
                }
        }
    }

    private fun addDefaultCategories() {
        viewModelScope.launch {
            listOf(
                Category(name = "مبيدات حشرية", icon = "🐛", color = 0xFFE53935, description = "مبيدات فعالة ضد الحشرات"),
                Category(name = "مبيدات فطرية", icon = "🍄", color = 0xFF8E24AA, description = "للقضاء على الفطريات"),
                Category(name = "مبيدات أعشاب", icon = "🌿", color = 0xFF43A047, description = "مكافحة الحشائش الضارة"),
                Category(name = "أسمدة ورقية",  icon = "🍃", color = 0xFF1E88E5, description = "أسمدة تُرش على الأوراق"),
                Category(name = "أسمدة أرضية",  icon = "🌱", color = 0xFF6D4C41, description = "أسمدة تُضاف للتربة"),
                Category(name = "بذور معتمدة",  icon = "🌾", color = 0xFFF9A825, description = "بذور عالية الجودة"),
                Category(name = "معدات الري",   icon = "💧", color = 0xFF0288D1, description = "معدات وأدوات الري"),
                Category(name = "أدوات زراعية", icon = "🔧", color = 0xFF546E7A, description = "أدوات ومعدات زراعية"),
                Category(name = "خدمات فلاحية", icon = "🚜", color = 0xFF7B1FA2, description = "خدمات استشارية وزراعية")
            ).forEach { repository.insertCategory(it) }
        }
    }

    fun reset() { defaultCategoriesAdded = false; loadCategories() }
    fun cleanupDuplicateCategories() {
        viewModelScope.launch {
            val allCategories = repository.getAllCategories().first()
            val uniqueCategories = allCategories.distinctBy { it.name }

            if (allCategories.size != uniqueCategories.size) {
                val toDelete = allCategories.filter { category ->
                    uniqueCategories.count { it.name == category.name } > 1
                }
                toDelete.forEach { repository.deleteCategory(it) }

                loadCategories()
            }
        }
    }
}