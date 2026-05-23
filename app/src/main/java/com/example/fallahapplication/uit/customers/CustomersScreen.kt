package com.example.fallahapplication.uit.customers

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.fallahapplication.data.model.Customer
import com.example.fallahapplication.uit.components.FallahTopBar
import com.example.fallahapplication.uit.components.SearchBar
import com.example.fallahapplication.uit.components.toLocaleString
import com.example.fallahapplication.ui.theme.FallahGreen
import com.example.fallahapplication.ui.theme.FallahRed
import com.example.fallahapplication.viewmodel.CustomerViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomersScreen(
    onCustomerClick: (Long) -> Unit,
    onAddCustomer: () -> Unit,
    onBack: () -> Unit,
    viewModel: CustomerViewModel = hiltViewModel()
) {
    val customers    by viewModel.customers.collectAsState()
    val searchQuery  by viewModel.searchQuery.collectAsState()
    var deleteTarget by remember { mutableStateOf<Customer?>(null) }

    Scaffold(
        topBar = {
            FallahTopBar(
                title = "الزبائن (${customers.size})",
                onBack = onBack,
                actions = {
                    IconButton(onClick = onAddCustomer) {
                        Icon(Icons.Outlined.PersonAdd, contentDescription = "إضافة", tint = Color.White)
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {

            SearchBar(
                query = searchQuery,
                onQueryChange = viewModel::setSearchQuery,
                placeholder = "بحث عن زبون..."
            )

            if (customers.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("👥", fontSize = 64.sp)
                        Spacer(Modifier.height(12.dp))
                        Text("لا يوجد زبائن", fontSize = 16.sp, color = Color(0xFF888888))
                        Spacer(Modifier.height(8.dp))
                        Button(
                            onClick = onAddCustomer,
                            colors = ButtonDefaults.buttonColors(containerColor = FallahGreen)
                        ) { Text("أضف أول زبون") }
                    }
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(customers, key = { it.id }) { customer ->
                        CustomerCard(
                            customer = customer,
                            onClick  = { onCustomerClick(customer.id) },
                            onDelete = { deleteTarget = customer }
                        )
                    }
                    item { Spacer(Modifier.height(80.dp)) }
                }
            }
        }
    }

    // ── نافذة تأكيد الحذف ──
    deleteTarget?.let { customer ->
        AlertDialog(
            onDismissRequest = { deleteTarget = null },
            title = { Text("حذف الزبون") },
            text  = { Text("هل تريد حذف \"${customer.name}\" نهائياً؟ سيتم حذف جميع سجلاته.") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.deleteCustomer(customer)
                    deleteTarget = null
                }) { Text("حذف", color = FallahRed) }
            },
            dismissButton = {
                TextButton(onClick = { deleteTarget = null }) { Text("إلغاء") }
            }
        )
    }
}

@Composable
fun CustomerCard(
    customer: Customer,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {

            // الصف الأول: الاسم + زر الحذف
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = FallahGreen.copy(alpha = .12f),
                        modifier = Modifier.size(36.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text("👤", fontSize = 18.sp)
                        }
                    }
                    Column {
                        Text(customer.name, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                        if (customer.phone.isNotBlank()) {
                            Text(
                                "📞 ${customer.phone}",
                                fontSize = 12.sp,
                                color = Color(0xFF666666)
                            )
                        }
                    }
                }
                IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                    Icon(
                        Icons.Outlined.DeleteOutline,
                        contentDescription = "حذف",
                        tint = Color(0xFFCCCCCC),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(Modifier.height(10.dp))
            HorizontalDivider(color = Color(0xFFF0F0F0), thickness = 0.5.dp)
            Spacer(Modifier.height(10.dp))

            // الصف الثاني: الإحصائيات
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                // إجمالي المشتريات
                CustomerStatChip(
                    label = "المشتريات",
                    value = "${customer.totalPurchases.toInt().toLocaleString()} دج",
                    color = FallahGreen
                )

                // الدين
                CustomerStatChip(
                    label = "الدين",
                    value = if (customer.totalDebt > 0)
                        "${customer.totalDebt.toInt().toLocaleString()} دج"
                    else "لا دين",
                    color = if (customer.totalDebt > 0) FallahRed else FallahGreen,
                    bgColor = if (customer.totalDebt > 0)
                        Color(0xFFFFEBEE) else Color(0xFFE8F5E8)
                )

                // عدد المشتريات
                CustomerStatChip(
                    label = "آخر شراء",
                    value = if (customer.lastPurchaseDate != null)
                        formatShortDate(customer.lastPurchaseDate)
                    else "—",
                    color = Color(0xFF1976D2)
                )
            }
        }
    }
}

@Composable
fun CustomerStatChip(
    label: String,
    value: String,
    color: Color,
    bgColor: Color = color.copy(alpha = .08f)
) {
    Surface(shape = RoundedCornerShape(8.dp), color = bgColor) {
        Column(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(value, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = color)
            Text(label, fontSize = 10.sp, color = Color(0xFF888888))
        }
    }
}

fun formatShortDate(timestamp: Long): String {
    val sdf = java.text.SimpleDateFormat("dd/MM", java.util.Locale.getDefault())
    return sdf.format(java.util.Date(timestamp))
}