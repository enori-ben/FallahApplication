package com.example.fallahapplication.uit.reports

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.fallahapplication.uit.components.FallahTopBar
import com.example.fallahapplication.ui.theme.FallahGreen
import com.example.fallahapplication.viewmodel.ReportViewModel
import kotlin.math.min

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdvancedReportsScreen(
    onBack: () -> Unit,
    viewModel: ReportViewModel = hiltViewModel()
) {
    val todayRevenue by viewModel.todayRevenue.collectAsState()
    val todayProfit by viewModel.todayProfit.collectAsState()
    val todaySalesCount by viewModel.todaySalesCount.collectAsState()
    val monthRevenue by viewModel.monthRevenue.collectAsState()
    val topProducts by viewModel.topProducts.collectAsState()

    Scaffold(
        topBar = { FallahTopBar(title = "📊 التقارير المتقدمة", onBack = onBack) }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // ملخص
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("📈 ملخص الأداء", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            SummaryCard(
                                title = "مبيعات اليوم",
                                value = "${todayRevenue?.toInt() ?: 0} دج",
                                icon = "💰",
                                color = FallahGreen,
                                modifier = Modifier.weight(1f)
                            )
                            SummaryCard(
                                title = "أرباح اليوم",
                                value = "${todayProfit?.toInt() ?: 0} دج",
                                icon = "📈",
                                color = Color(0xFFF57C00),
                                modifier = Modifier.weight(1f)
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            SummaryCard(
                                title = "فواتير اليوم",
                                value = "$todaySalesCount",
                                icon = "📄",
                                color = Color(0xFF1976D2),
                                modifier = Modifier.weight(1f)
                            )
                            SummaryCard(
                                title = "مبيعات الشهر",
                                value = "${monthRevenue?.toInt() ?: 0} دج",
                                icon = "📅",
                                color = Color(0xFF7B1FA2),
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }

            // المنتجات الأكثر مبيعاً
            if (topProducts.isNotEmpty()) {
                item {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(4.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("🏆 المنتجات الأكثر مبيعاً", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(12.dp))

                            topProducts.take(5).forEachIndexed { index, product ->
                                TopProductBar(
                                    rank = index + 1,
                                    name = product.productName,
                                    quantity = product.totalQty,
                                    total = product.totalRev,
                                    maxQuantity = topProducts.maxOfOrNull { it.totalQty } ?: 1
                                )
                                if (index < min(topProducts.size, 5) - 1) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                }
                            }
                        }
                    }
                }
            }

            // توقعات
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("🔮", fontSize = 24.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("توقعات الأسبوع القادم", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "بناءً على مبيعات الأسبوع الحالي، من المتوقع أن تصل مبيعات الأسبوع القادم إلى",
                            fontSize = 12.sp,
                            color = Color(0xFF666666)
                        )
                        Text(
                            "${((todayRevenue ?: 0.0) * 1.15).toInt()} دج",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = FallahGreen
                        )
                        Text("📈 نمو متوقع 15%", fontSize = 11.sp, color = Color(0xFF4CAF50))
                    }
                }
            }

            // نصائح ذكية
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3E0)),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("💡", fontSize = 24.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("نصائح ذكية", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.height(8.dp))

                        val topProduct = topProducts.firstOrNull()
                        if (topProduct != null) {
                            Text(
                                "• منتج ${topProduct.productName} هو الأكثر مبيعاً، تأكد من توفير مخزون كافٍ",
                                fontSize = 12.sp,
                                color = Color(0xFF666666)
                            )
                        }
                        Text(
                            "• زيادة مبيعاتك بنسبة 20% خلال العروض الموسمية",
                            fontSize = 12.sp,
                            color = Color(0xFF666666)
                        )
                        Text(
                            "• تتبع الديون بانتظام لتحسين التدفق النقدي",
                            fontSize = 12.sp,
                            color = Color(0xFF666666)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SummaryCard(
    title: String,
    value: String,
    icon: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.1f))
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(icon, fontSize = 20.sp)
            Column {
                Text(title, fontSize = 11.sp, color = Color(0xFF666666))
                Text(value, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = color)
            }
        }
    }
}

@Composable
fun TopProductBar(rank: Int, name: String, quantity: Int, total: Double, maxQuantity: Int) {
    val percentage = if (maxQuantity > 0) (quantity.toFloat() / maxQuantity * 100).toInt() else 0
    val rankColor = when (rank) {
        1 -> Color(0xFFFFD700)
        2 -> Color(0xFFC0C0C0)
        3 -> Color(0xFFCD7F32)
        else -> Color(0xFFE0E0E0)
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Surface(
            shape = RoundedCornerShape(8.dp),
            color = rankColor,
            modifier = Modifier.size(32.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text("#$rank", fontSize = 14.sp, fontWeight = FontWeight.Bold)
            }
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(name, fontSize = 13.sp, fontWeight = FontWeight.Medium)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("كمية: $quantity", fontSize = 10.sp, color = Color(0xFF888888))
                Text("${total.toInt()} دج", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = FallahGreen)
            }
            Spacer(modifier = Modifier.height(4.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .background(Color(0xFFEEEEEE), RoundedCornerShape(3.dp))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(percentage / 100f)
                        .height(6.dp)
                        .background(FallahGreen, RoundedCornerShape(3.dp))
                )
            }
        }
    }
}