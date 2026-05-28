package com.example.fallahapplication.viewmodel

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fallahapplication.data.model.Customer
import com.example.fallahapplication.data.repository.FallahRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DebtViewModel @Inject constructor(
    private val repository: FallahRepository
) : ViewModel() {

    val customersWithDebt = repository.getCustomersWithDebt()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val totalUnpaidDebt = repository.getTotalUnpaidDebt()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    fun recordPayment(customerId: Long, amount: Double, notes: String) {
        viewModelScope.launch {
            repository.recordPayment(customerId, amount, notes)
        }
    }

    // ✅ دالة جديدة: تسديد الدين وإرسال إشعار واتساب
    fun recordPaymentWithWhatsApp(
        context: Context,
        customer: Customer,
        amount: Double,
        notes: String,
        onComplete: () -> Unit
    ) {
        viewModelScope.launch {
            try {
                // 1. تسجيل الدفع في قاعدة البيانات
                repository.recordPayment(customer.id, amount, notes)

                // 2. حساب المبلغ المتبقي بعد التسديد
                val newRemainingDebt = customer.totalDebt - amount

                // 3. بناء رسالة واتساب
                val whatsappMessage = buildWhatsAppPaymentMessage(
                    customerName = customer.name,
                    paidAmount = amount,
                    remainingDebt = newRemainingDebt,
                    notes = notes
                )

                // 4. إرسال الرسالة عبر واتساب (إذا كان هناك رقم هاتف)
                if (customer.phone.isNotBlank() && customer.phone.length >= 9) {
                    sendWhatsAppMessage(context, customer.phone, whatsappMessage)
                }

                onComplete()

            } catch (e: Exception) {
                e.printStackTrace()
                onComplete()
            }
        }
    }

    // بناء رسالة واتساب للتسديد
    private fun buildWhatsAppPaymentMessage(
        customerName: String,
        paidAmount: Double,
        remainingDebt: Double,
        notes: String
    ): String {
        val date = java.text.SimpleDateFormat("dd/MM/yyyy HH:mm", java.util.Locale.getDefault())
            .format(java.util.Date())

        return """
            🌿 *محل الأمل للأدوية الفلاحية*
            ━━━━━━━━━━━━━━━━━━━━━━━━━
            💰 *إشعار تسديد دين*
            ━━━━━━━━━━━━━━━━━━━━━━━━━
            
            👤 *الزبون:* $customerName
            📅 *التاريخ:* $date
            
            💵 *المبلغ المسدد:* ${paidAmount.toInt()} دج
            📊 *المبلغ المتبقي:* ${remainingDebt.toInt()} دج
            
            ${if (notes.isNotBlank()) "📝 *ملاحظات:* $notes\n" else ""}
            
            ━━━━━━━━━━━━━━━━━━━━━━━━━
            ✅ تم تسجيل الدفع بنجاح
            🌾 شكراً لثقتكم
        """.trimIndent()
    }

    // إرسال رسالة عبر واتساب
    private fun sendWhatsAppMessage(context: Context, phoneNumber: String, message: String) {
        try {
            // تنسيق رقم الهاتف
            var formattedPhone = phoneNumber
                .trim()
                .replace(" ", "")
                .replace("-", "")
                .replace("+", "")

            if (formattedPhone.startsWith("0")) {
                formattedPhone = "213${formattedPhone.drop(1)}"
            }

            val intent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse("https://wa.me/$formattedPhone?text=${Uri.encode(message)}")
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun loadDebtData() {
        // لا شيء
    }
}