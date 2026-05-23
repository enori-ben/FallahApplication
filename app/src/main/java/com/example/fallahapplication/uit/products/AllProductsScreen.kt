package com.example.fallahapplication.uit.products

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.example.fallahapplication.ui.theme.FallahRed
import com.example.fallahapplication.uit.components.EmptyState
import com.example.fallahapplication.uit.components.FallahTopBar
import com.example.fallahapplication.uit.components.LoadingIndicator
import com.example.fallahapplication.uit.components.LowStockBadge
import com.example.fallahapplication.uit.components.toLocaleString
import com.example.fallahapplication.viewmodel.ProductViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AllProductsScreen(
    onBack: () -> Unit,
    onProductClick: (Long) -> Unit,
    viewModel: ProductViewModel = hiltViewModel()
) {
    val products by viewModel.products.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    Scaffold(
        topBar = { FallahTopBar(title = "جميع المنتجات", onBack = onBack) }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when {
                isLoading -> {
                    LoadingIndicator()
                }
                products.isEmpty() -> {
                    EmptyState(
                        icon = "📦",
                        message = "لا توجد منتجات",
                        onClick = { /* TODO: Add product */ }
                    )
                }
                else -> {
                    LazyColumn(
                        contentPadding = PaddingValues(12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(products, key = { it.id }) { product ->
                            ProductItemCard(
                                product = product,
                                onClick = { onProductClick(product.id) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ProductItemCard(
    product: com.example.fallahapplication.data.model.Product,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(10.dp),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    product.name,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    "السعر: ${product.sellingPrice.toInt().toLocaleString()} دج",
                    fontSize = 12.sp,
                    color = Color(0xFF888888)
                )
                if (product.isLowStock) {
                    LowStockBadge()
                }
            }
            Text(
                "المخزون: ${product.quantity}",
                fontSize = 12.sp,
                color = if (product.isLowStock) FallahRed else Color(0xFF888888)
            )
        }
    }
}