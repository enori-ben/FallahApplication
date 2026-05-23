package com.example.fallahapplication.uit.customers

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.fallahapplication.data.model.PaymentType
import com.example.fallahapplication.uit.components.FallahButton
import com.example.fallahapplication.uit.components.FallahTopBar
import com.example.fallahapplication.uit.components.SectionCard
import com.example.fallahapplication.uit.components.toLocaleString
import com.example.fallahapplication.ui.theme.*
import com.example.fallahapplication.viewmodel.CustomerDetailViewModel
import com.example.fallahapplication.viewmodel.SaleWithItems
import java.text.SimpleDateFormat
import java.util.*

@Suppress("LocaleLanguage")
fun formatDate(timestamp: Long): String {
    return try {
        val date = Date(timestamp)
        val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        formatter.format(date)
    } catch (e: Exception) {
        ""
    }
}

@Suppress("LocaleLanguage")
fun formatDateTime(timestamp: Long): String {
    return try {
        val date = Date(timestamp)
        val formatter = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        formatter.format(date)
    } catch (e: Exception) {
        ""
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomerDetailScreen(
    customerId: Long,
    onBack: () -> Unit,
    viewModel: CustomerDetailViewModel = hiltViewModel()
) {
    val customer by viewModel.customer.collectAsState()
    val remainingDebt by viewModel.remainingDebt.collectAsState()
    val salesWithItems by viewModel.salesWithItems.collectAsState()
    val payments by viewModel.payments.collectAsState()

    var showPaymentDialog by remember { mutableStateOf(false) }

    LaunchedEffect(customerId) {
        viewModel.loadCustomerData(customerId)
    }

    Scaffold(
        topBar = {
            FallahTopBar(
                title = customer?.name ?: "تفاصيل الزبون",
                onBack = onBack
            )
        },
        floatingActionButton = {
            if (remainingDebt > 0) {
                FloatingActionButton(
                    onClick = { showPaymentDialog = true },
                    containerColor = FallahGreen,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Outlined.Payment, contentDescription = "تسديد")
                }
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // معلومات الزبون
            item {
                SectionCard {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("معلومات الزبون", style = MaterialTheme.typography.titleSmall)
                            Spacer(Modifier.height(8.dp))
                            if (customer?.phone?.isNotBlank() == true) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Icon(Icons.Outlined.Phone, null, tint = FallahGreen, modifier = Modifier.size(16.dp))
                                    Text(customer!!.phone, fontSize = 13.sp)
                                }
                            }
                            if (customer?.address?.isNotBlank() == true) {
                                Spacer(Modifier.height(4.dp))
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Icon(Icons.Outlined.LocationOn, null, tint = FallahGreen, modifier = Modifier.size(16.dp))
                                    Text(customer!!.address, fontSize = 13.sp)
                                }
                            }
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text("إجمالي المشتريات", fontSize = 11.sp, color = Color(0xFF888888))
                            Text(
                                "${customer?.totalPurchases?.toInt()?.toLocaleString() ?: "0"} دج",
                                fontSize = 18.sp, fontWeight = FontWeight.Bold, color = FallahGreen
                            )
                            Spacer(Modifier.height(4.dp))
                            Text("عدد الفواتير", fontSize = 11.sp, color = Color(0xFF888888))
                            Text(
                                "${salesWithItems.size}",
                                fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1976D2)
                            )
                        }
                    }
                }
            }

            // بطاقة الدين المتبقي
            if (remainingDebt > 0) {
                item {
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE))
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("الدين المتبقي", fontSize = 14.sp, color = Color(0xFF888888))
                                Text(
                                    "${remainingDebt.toInt().toLocaleString()} دج",
                                    fontSize = 26.sp, fontWeight = FontWeight.Bold, color = FallahRed
                                )
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(Icons.Outlined.Warning, null, tint = FallahRed, modifier = Modifier.size(36.dp))
                                Spacer(Modifier.height(6.dp))
                                Button(
                                    onClick = { showPaymentDialog = true },
                                    colors = ButtonDefaults.buttonColors(containerColor = FallahRed),
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                    modifier = Modifier.height(32.dp)
                                ) {
                                    Text("تسديد", fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }
            } else {
                // إذا لم يكن هناك دين
                item {
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E8))
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("الدين", fontSize = 14.sp, color = Color(0xFF888888))
                                Text(
                                    "لا يوجد ديون",
                                    fontSize = 20.sp, fontWeight = FontWeight.Bold, color = FallahGreen
                                )
                            }
                            Icon(Icons.Outlined.CheckCircle, null, tint = FallahGreen, modifier = Modifier.size(36.dp))
                        }
                    }
                }
            }

            // سجل المدفوعات
            if (payments.isNotEmpty()) {
                item {
                    SectionCard {
                        Text("سجل المدفوعات", style = MaterialTheme.typography.titleSmall)
                        Spacer(Modifier.height(8.dp))
                        payments.take(5).forEach { payment ->
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Text("💵", fontSize = 14.sp)
                                    Column {
                                        Text("${payment.amount.toInt().toLocaleString()} دج",
                                            fontWeight = FontWeight.Medium, fontSize = 14.sp, color = FallahGreen)
                                        if (payment.notes.isNotBlank())
                                            Text(payment.notes, fontSize = 11.sp, color = Color(0xFF888888))
                                    }
                                }
                                Text(
                                    formatDate(payment.createdAt),
                                    fontSize = 11.sp, color = Color(0xFF888888)
                                )
                            }
                            HorizontalDivider(color = Color(0xFFF0F0F0), thickness = 0.5.dp)
                        }
                    }
                }
            }

            // الفواتير السابقة
            if (salesWithItems.isNotEmpty()) {
                item {
                    Text(
                        "سجل المشتريات (${salesWithItems.size} فاتورة)",
                        style = MaterialTheme.typography.titleSmall,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }

                items(salesWithItems, key = { it.sale.id }) { saleWithItems ->
                    SaleInvoiceCard(saleWithItems = saleWithItems)
                }
            } else {
                item {
                    Box(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("🛍️", fontSize = 40.sp)
                            Spacer(Modifier.height(8.dp))
                            Text("لا توجد مشتريات بعد", fontSize = 14.sp, color = Color(0xFF888888))
                        }
                    }
                }
            }

            item { Spacer(Modifier.height(80.dp)) }
        }
    }

    // نافذة التسديد
    if (showPaymentDialog) {
        PaymentDialog(
            customerName = customer?.name ?: "",
            remainingDebt = remainingDebt,
            onDismiss = { showPaymentDialog = false },
            onConfirm = { amount, notes ->
                viewModel.recordPayment(amount, notes)
                showPaymentDialog = false
            }
        )
    }
}

// بطاقة الفاتورة مع تفاصيل المنتجات
@Composable
fun SaleInvoiceCard(saleWithItems: SaleWithItems) {
    val sale = saleWithItems.sale
    val items = saleWithItems.items
    val dateStr = formatDateTime(sale.createdAt)
    val isPaid = sale.paymentType == PaymentType.CASH
    val isDebt = sale.paymentType == PaymentType.DEBT

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // رأس الفاتورة
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(sale.invoiceNumber, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = Color(0xFF333333))
                    Text(dateStr, fontSize = 11.sp, color = Color(0xFF888888))
                }
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = when {
                        isPaid -> Color(0xFFE8F5E8)
                        isDebt -> Color(0xFFFFEBEE)
                        else -> Color(0xFFFFF8E1)
                    }
                ) {
                    Text(
                        text = when {
                            isPaid -> "✓ نقدًا"
                            isDebt -> "دين"
                            else -> "جزئي"
                        },
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = when {
                            isPaid -> FallahGreen
                            isDebt -> FallahRed
                            else -> Color(0xFFE65100)
                        },
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = Color(0xFFF0F0F0))

            // قائمة المنتجات
            items.forEach { item ->
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        "• ${item.productName}  ×${item.quantity}",
                        fontSize = 13.sp,
                        color = Color(0xFF444444),
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        "${item.totalPrice.toInt().toLocaleString()} دج",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF333333)
                    )
                }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 6.dp), color = Color(0xFFF0F0F0))

            // المجموع
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("الإجمالي", fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                Text(
                    "${sale.totalAmount.toInt().toLocaleString()} دج",
                    fontSize = 15.sp, fontWeight = FontWeight.Bold, color = FallahGreen
                )
            }

            // المدفوع والمتبقي
            if (!isPaid) {
                Spacer(Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("المدفوع", fontSize = 12.sp, color = Color(0xFF888888))
                    Text(
                        "${sale.paidAmount.toInt().toLocaleString()} دج",
                        fontSize = 12.sp, color = FallahGreen
                    )
                }
                if (sale.remainingAmount > 0) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("المتبقي", fontSize = 12.sp, color = FallahRed)
                        Text(
                            "${sale.remainingAmount.toInt().toLocaleString()} دج",
                            fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = FallahRed
                        )
                    }
                }
            }
        }
    }
}

// نافذة التسديد
@Composable
fun PaymentDialog(
    customerName: String,
    remainingDebt: Double,
    onDismiss: () -> Unit,
    onConfirm: (Double, String) -> Unit
) {
    var amount by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
            Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("تسديد دين", style = MaterialTheme.typography.titleMedium)
                Text(customerName, fontSize = 14.sp, color = Color(0xFF888888))

                Box(
                    modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFFFFEBEE)).padding(10.dp)
                ) {
                    Text("المتبقي: ${remainingDebt.toInt().toLocaleString()} دج", fontSize = 13.sp, color = FallahRed)
                }

                OutlinedTextField(
                    value = amount,
                    onValueChange = { amount = it },
                    label = { Text("المبلغ (دج)") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                        keyboardType = androidx.compose.ui.text.input.KeyboardType.Decimal
                    ),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = FallahGreen)
                )
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("ملاحظات (اختياري)") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = FallahGreen)
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(onClick = onDismiss, modifier = Modifier.weight(1f)) {
                        Text("إلغاء")
                    }
                    FallahButton(
                        text = "تسديد",
                        enabled = (amount.toDoubleOrNull() ?: 0.0) > 0,
                        onClick = { onConfirm(amount.toDoubleOrNull() ?: 0.0, notes) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}