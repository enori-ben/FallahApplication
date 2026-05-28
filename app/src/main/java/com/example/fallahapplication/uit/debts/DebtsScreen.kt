package com.example.fallahapplication.uit.debts

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.fallahapplication.data.model.Customer
import com.example.fallahapplication.uit.components.FallahButton
import com.example.fallahapplication.uit.components.FallahTopBar
import com.example.fallahapplication.uit.components.toLocaleString
import com.example.fallahapplication.ui.theme.FallahGreen
import com.example.fallahapplication.ui.theme.FallahRed
import com.example.fallahapplication.viewmodel.DebtViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DebtsScreen(
    onCustomerClick: (Long) -> Unit,
    onBack: () -> Unit,
    viewModel: DebtViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val customersWithDebt by viewModel.customersWithDebt.collectAsState()
    val totalUnpaidDebt by viewModel.totalUnpaidDebt.collectAsState()
    var payTarget by remember { mutableStateOf<Customer?>(null) }
    var showSuccessDialog by remember { mutableStateOf(false) }
    var lastPaymentInfo by remember { mutableStateOf<Pair<String, Double>?>(null) }

    Scaffold(
        topBar = { FallahTopBar(title = "💰 الديون", onBack = onBack) }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {

            // ── بطاقة الإجمالي ──
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0xFFFFEBEE))
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            "إجمالي الديون غير المسددة",
                            fontSize = 13.sp,
                            color = Color(0xFF888888)
                        )
                        Text(
                            "${(totalUnpaidDebt ?: 0.0).toInt().toLocaleString()} دج",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = FallahRed
                        )
                        Text(
                            "${customersWithDebt.size} زبون مدين",
                            fontSize = 12.sp,
                            color = Color(0xFF888888)
                        )
                    }
                    Icon(
                        Icons.Outlined.AccountBalance,
                        contentDescription = null,
                        tint = FallahRed,
                        modifier = Modifier.size(52.dp)
                    )
                }
            }

            if (customersWithDebt.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("✅", fontSize = 64.sp)
                        Spacer(Modifier.height(12.dp))
                        Text("لا توجد ديون", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = FallahGreen)
                        Text("جميع الزبائن سددوا ديونهم", fontSize = 14.sp, color = Color(0xFF888888))
                    }
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(customersWithDebt, key = { it.id }) { customer ->
                        DebtCustomerCard(
                            customer = customer,
                            onClick = { onCustomerClick(customer.id) },
                            onPay = { payTarget = customer }
                        )
                    }
                    item { Spacer(Modifier.height(80.dp)) }
                }
            }
        }
    }

    // ── نافذة التسديد السريع (محدثة) ──
    payTarget?.let { customer ->
        QuickPaymentDialog(
            customer = customer,
            onDismiss = { payTarget = null },
            onConfirm = { amount, notes ->
                // تسجيل الدفع وإرسال واتساب
                viewModel.recordPaymentWithWhatsApp(
                    context = context,
                    customer = customer,
                    amount = amount,
                    notes = notes,
                    onComplete = {
                        lastPaymentInfo = Pair(customer.name, amount)
                        showSuccessDialog = true
                        payTarget = null
                    }
                )
            }
        )
    }

    // ── نافذة نجاح التسديد مع إمكانية إرسال واتساب يدوياً ──
    if (showSuccessDialog && lastPaymentInfo != null) {
        val (customerName, amount) = lastPaymentInfo!!
        val remainingDebt = customersWithDebt.find { it.name == customerName }?.totalDebt ?: 0.0

        AlertDialog(
            onDismissRequest = { showSuccessDialog = false },
            title = { Text("✅ تم تسديد الدين") },
            text = {
                Column {
                    Text("تم تسجيل مبلغ ${amount.toInt().toLocaleString()} دج", fontSize = 14.sp)
                    Text("لصالح $customerName", fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "المبلغ المتبقي: ${remainingDebt.toInt().toLocaleString()} دج",
                        fontSize = 13.sp,
                        color = if (remainingDebt > 0) FallahRed else FallahGreen,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        "📱 تم فتح واتساب تلقائياً لإرسال الإشعار",
                        fontSize = 11.sp,
                        color = Color(0xFF888888)
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { showSuccessDialog = false }) {
                    Text("حسناً", color = FallahGreen)
                }
            }
        )
    }
}

@Composable
fun DebtCustomerCard(
    customer: Customer,
    onClick: () -> Unit,
    onPay: () -> Unit
) {
    val context = LocalContext.current

    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // الصف الأول: اسم الزبون ورقم الهاتف والدين
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // الجهة اليمنى: اسم الزبون ورقم الهاتف
                Column(modifier = Modifier.weight(1f)) {
                    Text(customer.name, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                    if (customer.phone.isNotBlank()) {
                        Text("📞 ${customer.phone}", fontSize = 12.sp, color = Color(0xFF666666))
                    } else {
                        Text("⚠️ لا يوجد رقم هاتف", fontSize = 10.sp, color = FallahRed)
                    }
                }

                // الجهة اليسرى: قيمة الدين
                Column(horizontalAlignment = Alignment.End) {
                    Text("الدين المستحق", fontSize = 11.sp, color = Color(0xFF888888))
                    Text(
                        "${customer.totalDebt.toInt().toLocaleString()} دج",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = FallahRed
                    )
                }
            }

            Spacer(Modifier.height(10.dp))

            // الصف الثاني: الأزرار والإحصائيات
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // نص إجمالي المشتريات
                Text(
                    "إجمالي المشتريات: ${customer.totalPurchases.toInt().toLocaleString()} دج",
                    fontSize = 12.sp,
                    color = Color(0xFF666666),
                    modifier = Modifier.weight(1f)
                )

                // ✅ مجموعة الأزرار (تسديد + واتساب)
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // زر الواتساب (يظهر فقط إذا كان هناك رقم هاتف)
                    if (customer.phone.isNotBlank()) {
                        IconButton(
                            onClick = {
                                sendWhatsAppMessageDirect(
                                    context = context,
                                    phone = customer.phone,
                                    customerName = customer.name,
                                    remainingDebt = customer.totalDebt
                                )
                            },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                Icons.Outlined.Chat,
                                contentDescription = "إرسال عبر واتساب",
                                tint = Color(0xFF25D366),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    // زر التسديد
                    Button(
                        onClick = onPay,
                        colors = ButtonDefaults.buttonColors(containerColor = FallahGreen),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
                        modifier = Modifier.height(34.dp)
                    ) {
                        Icon(Icons.Outlined.Payment, null, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("تسديد", fontSize = 13.sp)
                    }
                }
            }
        }
    }
}

// ✅ دالة إرسال رسالة واتساب (أضفها خارج الـ Composable)
fun sendWhatsAppMessageDirect(
    context: Context,
    phone: String,
    customerName: String,
    remainingDebt: Double
) {
    try {
        // تنسيق رقم الهاتف
        var formattedPhone = phone
            .trim()
            .replace(" ", "")
            .replace("-", "")
            .replace("(", "")
            .replace(")", "")

        if (formattedPhone.startsWith("0")) {
            formattedPhone = "213${formattedPhone.drop(1)}"
        }

        // بناء الرسالة
        val message = """
            🌿 *محل الأمل للأدوية الفلاحية*
            ━━━━━━━━━━━━━━━━━━━━━━━━━
            
            👤 *الزبون:* $customerName
            💰 *الدين المتبقي:* ${remainingDebt.toInt()} دج
            
            ━━━━━━━━━━━━━━━━━━━━━━━━━
            📱 يرجى تسديد المستحق في أقرب وقت
            🌾 شكراً لتفهمكم
        """.trimIndent()

        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse("https://wa.me/$formattedPhone?text=${Uri.encode(message)}")
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)

    } catch (e: Exception) {
        Toast.makeText(context, "تعذر فتح واتساب: ${e.message}", Toast.LENGTH_SHORT).show()
    }
}

@Composable
fun QuickPaymentDialog(
    customer: Customer,
    onDismiss: () -> Unit,
    onConfirm: (Double, String) -> Unit
) {
    var amount by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
            Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("تسديد دين — ${customer.name}", style = MaterialTheme.typography.titleMedium)

                Box(
                    modifier = Modifier.fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFFFFEBEE))
                        .padding(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("الدين المتبقي:", fontSize = 14.sp)
                        Text(
                            "${customer.totalDebt.toInt().toLocaleString()} دج",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = FallahRed
                        )
                    }
                }

                OutlinedTextField(
                    value = amount,
                    onValueChange = { amount = it },
                    label = { Text("المبلغ المسدَّد (دج)") },
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

                // تحذير إذا لم يكن هناك رقم هاتف
                if (customer.phone.isBlank()) {
                    Text(
                        "⚠️ هذا الزبون ليس لديه رقم هاتف مسجل، لن يتم إرسال إشعار واتساب",
                        fontSize = 11.sp,
                        color = FallahRed
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f)
                    ) { Text("إلغاء") }

                    FallahButton(
                        text = "تأكيد التسديد",
                        enabled = (amount.toDoubleOrNull() ?: 0.0) > 0,
                        onClick = { onConfirm(amount.toDoubleOrNull() ?: 0.0, notes) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}