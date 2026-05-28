package com.example.fallahapplication.uit.products

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.fallahapplication.data.model.Product
import com.example.fallahapplication.uit.components.FallahButton
import com.example.fallahapplication.uit.components.FallahTopBar
import com.example.fallahapplication.uit.components.SectionCard
import com.example.fallahapplication.uit.components.toLocaleString
import com.example.fallahapplication.ui.theme.FallahGreen
import com.example.fallahapplication.ui.theme.FallahRed
import com.example.fallahapplication.viewmodel.ProductViewModel
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailScreen(
    productId: Long,
    onBack: () -> Unit,
    onEdit: (Long) -> Unit,
    onProductDeleted: () -> Unit = onBack,
    viewModel: ProductViewModel = hiltViewModel()
) {
    var product by remember { mutableStateOf<Product?>(null) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    LaunchedEffect(productId) {
        product = viewModel.getProductById(productId)
    }

    if (product == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                CircularProgressIndicator(color = FallahGreen)
                Spacer(modifier = Modifier.height(12.dp))
                Text("جاري تحميل المنتج...", fontSize = 14.sp, color = Color(0xFF888888))
            }
        }
        return
    }

    val p = product!!

    Scaffold(
        topBar = {
            FallahTopBar(
                title = p.name,
                onBack = onBack,
                actions = {
                    IconButton(onClick = { onEdit(p.id) }) {
                        Icon(Icons.Outlined.Edit, null, tint = Color.White)
                    }
                    IconButton(onClick = { showDeleteDialog = true }) {
                        Icon(Icons.Outlined.DeleteOutline, null, tint = Color.White)
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (p.isLowStock) Color(0xFFFFEBEE) else Color(0xFFE8F5E8))
                    .padding(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            if (p.isLowStock) "⚠️ مخزون منخفض" else "✅ متوفر في المخزون",
                            fontWeight = FontWeight.SemiBold,
                            color = if (p.isLowStock) FallahRed else FallahGreen
                        )
                        Text(
                            "${p.quantity} ${p.unit}",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (p.isLowStock) FallahRed else FallahGreen
                        )
                    }
                    Text(if (p.isLowStock) "📉" else "📦", fontSize = 40.sp)
                }
            }

            SectionCard {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("معلومات المنتج", style = MaterialTheme.typography.titleSmall)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("الاسم:", color = Color(0xFF888888))
                        Text(p.name, fontWeight = FontWeight.Medium)
                    }

                    if (p.description.isNotBlank()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("الوصف:", color = Color(0xFF888888))
                            Text(p.description, fontWeight = FontWeight.Medium)
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("الوحدة:", color = Color(0xFF888888))
                        Text(p.unit)
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("حد التنبيه:", color = Color(0xFF888888))
                        Text("${p.minQuantity} ${p.unit}")
                    }
                }
            }

            SectionCard {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("الأسعار والربح", style = MaterialTheme.typography.titleSmall)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("سعر الشراء:", color = Color(0xFF888888))
                        Text("${p.purchasePrice.toInt().toLocaleString()} دج")
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("سعر البيع:", color = Color(0xFF888888))
                        Text(
                            "${p.sellingPrice.toInt().toLocaleString()} دج",
                            fontWeight = FontWeight.Bold,
                            color = FallahGreen
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("الربح:", color = Color(0xFF888888))
                        Text(
                            String.format(Locale.US, "%d دج (%.1f%%)", p.profit.toInt(), p.profitPercent),
                            color = if (p.profit > 0) FallahGreen else FallahRed
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            FallahButton(
                text = "✏️ تعديل المنتج",
                onClick = { onEdit(p.id) }
            )
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("حذف المنتج") },
            text = { Text("هل تريد حذف \"${p.name}\" نهائياً من المخزون؟") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteProduct(p)
                        showDeleteDialog = false
                        onProductDeleted()
                    }
                ) {
                    Text("حذف", color = FallahRed)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("إلغاء")
                }
            }
        )
    }
}