package com.example.fallahapplication.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fallahapplication.data.model.*
import com.example.fallahapplication.data.repository.FallahRepository
import com.example.fallahapplication.uit.components.toLocaleString
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CartUiState(
    val cartItems: List<CartItem> = emptyList(),
    val customerName: String = "",
    val customerId: Long? = null,
    val paymentType: PaymentType = PaymentType.CASH,
    val paidAmount: String = "",
    val notes: String = "",
    val isLoading: Boolean = false,
    val saleCompleted: Boolean = false,
    val lastSaleId: Long? = null,
    val error: String? = null
) {
    val totalAmount: Double get() = cartItems.sumOf { it.total }
    val itemCount: Int get() = cartItems.sumOf { it.quantity }
    val remainingDebt: Double get() {
        return if (paymentType == PaymentType.PARTIAL) {
            totalAmount - (paidAmount.toDoubleOrNull() ?: 0.0)
        } else 0.0
    }
}

@HiltViewModel
class CartViewModel @Inject constructor(
    private val repository: FallahRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CartUiState())
    val uiState: StateFlow<CartUiState> = _uiState.asStateFlow()

    fun addToCart(product: Product) {
        val currentItems = _uiState.value.cartItems.toMutableList()
        val existingIndex = currentItems.indexOfFirst { it.product.id == product.id }

        if (existingIndex >= 0) {
            val existing = currentItems[existingIndex]
            currentItems[existingIndex] = existing.copy(quantity = existing.quantity + 1)
        } else {
            currentItems.add(CartItem(product = product, quantity = 1))
        }

        _uiState.update { it.copy(cartItems = currentItems) }
    }

    fun updateQuantity(productId: Long, quantity: Int) {
        val currentItems = _uiState.value.cartItems.toMutableList()
        val index = currentItems.indexOfFirst { it.product.id == productId }

        if (index >= 0) {
            if (quantity <= 0) {
                currentItems.removeAt(index)
            } else {
                currentItems[index] = currentItems[index].copy(quantity = quantity)
            }
            _uiState.update { it.copy(cartItems = currentItems) }
        }
    }

    fun removeFromCart(productId: Long) {
        _uiState.update { state ->
            state.copy(cartItems = state.cartItems.filter { it.product.id != productId })
        }
    }

    fun setCustomer(customer: Customer?) {
        _uiState.update {
            it.copy(
                customerName = customer?.name ?: "",
                customerId = customer?.id
            )
        }
    }

    fun setCustomerName(name: String) {
        _uiState.update { it.copy(customerName = name, customerId = null) }
    }

    fun setPaymentType(type: PaymentType) {
        _uiState.update { it.copy(paymentType = type) }
    }

    fun setPaidAmount(amount: String) {
        _uiState.update { it.copy(paidAmount = amount) }
    }

    fun setNotes(notes: String) {
        _uiState.update { it.copy(notes = notes) }
    }

    fun completeSale() {
        viewModelScope.launch {
            val state = _uiState.value
            if (state.cartItems.isEmpty()) {
                _uiState.update { it.copy(error = "السلة فارغة") }
                return@launch
            }

            _uiState.update { it.copy(isLoading = true, error = null) }

            try {
                var finalCustomerId = state.customerId
                var finalCustomerName = state.customerName.ifBlank { "زبون نقدي" }

                // ✅ البحث عن الزبون إذا كان الاسم موجوداً ولم يتم تحديد ID
                if (state.customerName.isNotBlank() && finalCustomerId == null) {
                    val existingCustomer = repository.getCustomerByName(state.customerName)
                    if (existingCustomer != null) {
                        finalCustomerId = existingCustomer.id
                        finalCustomerName = existingCustomer.name
                        android.util.Log.d("CartViewModel", "✅ تم العثور على الزبون: ${existingCustomer.name} (ID: ${existingCustomer.id})")
                    } else {
                        // إنشاء زبون جديد
                        val newCustomer = Customer(
                            name = state.customerName,
                            phone = "",
                            address = "",
                            totalPurchases = 0.0,
                            totalDebt = 0.0,
                            lastPurchaseDate = null
                        )
                        finalCustomerId = repository.insertCustomer(newCustomer)
                        finalCustomerName = newCustomer.name
                        android.util.Log.d("CartViewModel", "✅ تم إنشاء زبون جديد: ${newCustomer.name} (ID: $finalCustomerId)")
                    }
                }

                android.util.Log.d("CartViewModel", "📝 جاري حفظ البيع للزبون: $finalCustomerName (ID: $finalCustomerId)")

                val saleId = repository.completeSale(
                    cartItems = state.cartItems,
                    customerName = finalCustomerName,
                    customerId = finalCustomerId,
                    paymentType = state.paymentType,
                    paidAmount = state.paidAmount.toDoubleOrNull() ?: 0.0,
                    notes = state.notes
                )

                val message = when (state.paymentType) {
                    PaymentType.CASH -> "✅ تم البيع بنجاح! المبلغ: ${state.totalAmount.toInt().toLocaleString()} دج"
                    PaymentType.DEBT -> "✅ تم البيع بنجاح! تم تسجيل دين بقيمة: ${state.totalAmount.toInt().toLocaleString()} دج"
                    PaymentType.PARTIAL -> {
                        val debt = state.totalAmount - (state.paidAmount.toDoubleOrNull() ?: 0.0)
                        "✅ تم البيع بنجاح! المدفوع: ${state.paidAmount.toInt().toLocaleString()} دج، المتبقي: ${debt.toInt().toLocaleString()} دج"
                    }
                }

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        saleCompleted = true,
                        lastSaleId = saleId,
                        error = message
                    )
                }
            } catch (e: Exception) {
                android.util.Log.e("CartViewModel", "❌ خطأ في البيع: ${e.message}", e)
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "حدث خطأ أثناء البيع"
                    )
                }
            }
        }
    }

    fun clearCart() {
        _uiState.value = CartUiState()
    }

    fun resetSaleCompleted() {
        _uiState.update { it.copy(saleCompleted = false) }
    }

    fun addToCartWithQuantity(product: Product, quantity: Int, unitPrice: Double) {
        val currentItems = _uiState.value.cartItems.toMutableList()
        val existingIndex = currentItems.indexOfFirst { it.product.id == product.id }

        if (existingIndex >= 0) {
            val existing = currentItems[existingIndex]
            currentItems[existingIndex] = existing.copy(
                quantity = existing.quantity + quantity,
                unitPrice = unitPrice
            )
        } else {
            currentItems.add(CartItem(product = product, quantity = quantity, unitPrice = unitPrice))
        }

        _uiState.update { it.copy(cartItems = currentItems) }
    }
}