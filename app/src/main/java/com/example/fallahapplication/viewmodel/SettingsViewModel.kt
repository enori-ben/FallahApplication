package com.example.fallahapplication.viewmodel

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fallahapplication.data.repository.FallahRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    val repository: FallahRepository
) : ViewModel() {

    fun clearAllData() {
        viewModelScope.launch {
            // This would need to be implemented in the repository
            // For now, we'll just show a message
            // repository.clearAllData()
        }
    }

    fun exportData(context: Context) {
        viewModelScope.launch {
            try {
                val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
                val fileName = "fallah_export_$timestamp.csv"
                val file = File(context.getExternalFilesDir(null), fileName)

                // Build CSV content
                val csvContent = StringBuilder()

                // Add header
                csvContent.appendLine("التقرير,البيانات")
                csvContent.appendLine("تاريخ التصدير,${Date()}")
                csvContent.appendLine()

                // Export customers
                csvContent.appendLine("الزبائن")
                csvContent.appendLine("الاسم,رقم الهاتف,العنوان,إجمالي الديون,إجمالي المشتريات")

                val customers = repository.getAllCustomers().first()
                customers.forEach { customer ->
                    csvContent.appendLine("${customer.name},${customer.phone},${customer.address},${customer.totalDebt},${customer.totalPurchases}")
                }

                csvContent.appendLine()
                csvContent.appendLine("نهاية التقرير")

                // Write to file
                FileOutputStream(file).use { outputStream ->
                    outputStream.write(csvContent.toString().toByteArray())
                }

                Toast.makeText(context, "تم التصدير إلى: ${file.absolutePath}", Toast.LENGTH_LONG).show()
            } catch (e: Exception) {
                Toast.makeText(context, "خطأ في التصدير: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}