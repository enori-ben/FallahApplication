package com.example.fallahapplication.uit.debts

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
    val customersWithDebt by viewModel.customersWithDebt.collectAsState()
    val totalUnpaidDebt   by viewModel.totalUnpaidDebt.collectAsState()
    var payTarget by remember { mutableStateOf<Customer?>(null) }

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
                            customer  = customer,
                            onClick   = { onCustomerClick(customer.id) },
                            onPay     = { payTarget = customer }
                        )
                    }
                    item { Spacer(Modifier.height(80.dp)) }
                }
            }
        }
    }

    // ── نافذة التسديد السريع ──
    payTarget?.let { customer ->
        QuickPaymentDialog(
            customer  = customer,
            onDismiss = { payTarget = null },
            onConfirm = { amount, notes ->
                viewModel.recordPayment(customer.id, amount, notes)
                payTarget = null
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
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(customer.name, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                    if (customer.phone.isNotBlank()) {
                        Text("📞 ${customer.phone}", fontSize = 12.sp, color = Color(0xFF666666))
                    }
                }
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

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "إجمالي المشتريات: ${customer.totalPurchases.toInt().toLocaleString()} دج",
                    fontSize = 12.sp,
                    color = Color(0xFF666666)
                )
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

@Composable
fun QuickPaymentDialog(
    customer: Customer,
    onDismiss: () -> Unit,
    onConfirm: (Double, String) -> Unit
) {
    var amount by remember { mutableStateOf("") }
    var notes  by remember { mutableStateOf("") }

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