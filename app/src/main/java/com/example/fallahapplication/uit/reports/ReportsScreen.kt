package com.example.fallahapplication.uit.reports

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.navigation.NavController
import com.example.fallahapplication.data.model.Sale
import com.example.fallahapplication.data.local.TopProduct
import com.example.fallahapplication.uit.components.FallahTopBar
import com.example.fallahapplication.uit.components.ReportRow
import com.example.fallahapplication.uit.components.SectionCard
import com.example.fallahapplication.ui.theme.FallahGreen
import com.example.fallahapplication.ui.theme.FallahRed
import com.example.fallahapplication.viewmodel.ReportViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportsScreen(
    navController: NavController,
    onBack: () -> Unit,
    viewModel: ReportViewModel = hiltViewModel()
) {
    val todayRevenue by viewModel.todayRevenue.collectAsState()
    val todayProfit by viewModel.todayProfit.collectAsState()
    val todaySalesCount by viewModel.todaySalesCount.collectAsState()
    val monthRevenue by viewModel.monthRevenue.collectAsState()
    val totalUnpaidDebt by viewModel.totalUnpaidDebt.collectAsState()
    val topProducts by viewModel.topProducts.collectAsState()
    val recentSales by viewModel.recentSales.collectAsState()
    val lowStockProducts by viewModel.lowStockProducts.collectAsState()

    Scaffold(
        topBar = { FallahTopBar(title = "التقارير والإحصائيات", onBack = onBack) }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                SectionCard {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("إحصائيات اليوم", style = MaterialTheme.typography.titleMedium)
                        Icon(Icons.Outlined.Today, contentDescription = null, tint = FallahGreen)
                    }
                    Spacer(modifier = Modifier.height(12.dp))

                    ReportRow("عدد الفواتير", todaySalesCount.toString())
                    ReportRow("إجمالي المبيعات", formatNumber(todayRevenue), FallahGreen)
                    ReportRow("الأرباح", formatNumber(todayProfit), FallahGreen)
                }
            }

            item {
                SectionCard {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("إحصائيات الشهر", style = MaterialTheme.typography.titleMedium)
                        Icon(Icons.Outlined.DateRange, contentDescription = null, tint = FallahGreen)
                    }
                    Spacer(modifier = Modifier.height(12.dp))

                    ReportRow("إجمالي مبيعات الشهر", formatNumber(monthRevenue), FallahGreen)
                    ReportRow("إجمالي الديون", formatNumber(totalUnpaidDebt), FallahRed)
                }
            }

            if (topProducts.isNotEmpty()) {
                item {
                    SectionCard {
                        Text("المنتجات الأكثر مبيعاً", style = MaterialTheme.typography.titleMedium)
                        Spacer(modifier = Modifier.height(12.dp))

                        topProducts.forEachIndexed { index, product ->
                            TopProductRow(index = index + 1, product = product)
                        }
                    }
                }
            }

            if (lowStockProducts.isNotEmpty()) {
                item {
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF8E1))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("⚠️ تنبيه المخزون", fontWeight = FontWeight.Bold)
                                Text("${lowStockProducts.size} منتج", fontSize = 12.sp, color = Color(0xFF888888))
                            }
                            Spacer(modifier = Modifier.height(8.dp))

                            lowStockProducts.take(3).forEach { product ->
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(product.name, fontSize = 13.sp)
                                    Text(
                                        "المتبقي: ${product.quantity} ${product.unit}",
                                        fontSize = 12.sp,
                                        color = FallahRed
                                    )
                                }
                            }
                        }
                    }
                }
            }

            if (recentSales.isNotEmpty()) {
                item {
                    SectionCard {
                        Text("آخر المبيعات", style = MaterialTheme.typography.titleMedium)
                        Spacer(modifier = Modifier.height(12.dp))

                        recentSales.take(5).forEach { sale ->
                            RecentSaleItem(sale = sale)
                        }
                    }
                }
            }

            item {
                Button(
                    onClick = { navController.navigate("advanced_reports") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = FallahGreen)
                ) {
                    Icon(Icons.Outlined.BarChart, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("التقارير المتقدمة 📊", fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

@Composable
fun TopProductRow(index: Int, product: TopProduct) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(
                "#$index",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = if (index == 1) FallahGreen else Color(0xFF888888)
            )
            Column {
                Text(product.productName, fontWeight = FontWeight.Medium)
                Text("كمية: ${product.totalQty}", fontSize = 11.sp, color = Color(0xFF888888))
            }
        }
        Text(
            formatNumber(product.totalRev),
            fontWeight = FontWeight.SemiBold,
            color = FallahGreen
        )
    }
}

@Composable
fun RecentSaleItem(sale: Sale) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(sale.invoiceNumber, fontWeight = FontWeight.Medium)
            Text(sale.customerName, fontSize = 11.sp, color = Color(0xFF888888))
        }
        Column(horizontalAlignment = Alignment.End) {
            Text(formatNumber(sale.totalAmount), color = FallahGreen)
            Text(
                formatTime(sale.createdAt),
                fontSize = 11.sp,
                color = Color(0xFF888888)
            )
        }
    }
    HorizontalDivider(color = Color(0xFFEEEEEE), thickness = 0.5.dp)
}

fun formatNumber(value: Double?): String {
    val v = value ?: 0.0
    return String.format(Locale.US, "%.0f", v).let {
        val number = it.toLong()
        when {
            number >= 1_000_000 -> "${number / 1_000_000}M دج"
            number >= 1_000 -> "${number / 1_000}K دج"
            else -> "$number دج"
        }
    }
}

fun formatNumber(value: Double): String {
    return when {
        value >= 1_000_000 -> "${(value / 1_000_000).toInt()}M دج"
        value >= 1_000 -> "${(value / 1_000).toInt()}K دج"
        else -> "${value.toInt()} دج"
    }
}

fun formatTime(timestamp: Long): String {
    val date = Date(timestamp)
    val formatter = SimpleDateFormat("HH:mm", Locale.US)
    return formatter.format(date)
}

fun formatDate(timestamp: Long): String {
    val date = Date(timestamp)
    val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.US)
    return formatter.format(date)
}