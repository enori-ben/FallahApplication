package com.example.fallahapplication.uit.products

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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.fallahapplication.Screen
import com.example.fallahapplication.data.model.Product
import com.example.fallahapplication.uit.components.FallahTopBar
import com.example.fallahapplication.uit.components.toLocaleString
import com.example.fallahapplication.ui.theme.FallahGreen
import com.example.fallahapplication.ui.theme.FallahRed
import com.example.fallahapplication.viewmodel.CartViewModel
import com.example.fallahapplication.viewmodel.ProductViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductsScreen(
    categoryId: Long,
    categoryName: String,
    onBack: () -> Unit,
    onAddProduct: () -> Unit,
    onGoToCart: () -> Unit,
    cartViewModel: CartViewModel,
    productViewModel: ProductViewModel = hiltViewModel(),
    navController: NavController = rememberNavController() // أضف navController
) {
    val products by productViewModel.getProductsByCategory(categoryId).collectAsState(initial = emptyList())
    val isLoading by productViewModel.isLoading.collectAsState()

    Scaffold(
        topBar = {
            FallahTopBar(
                title = categoryName,
                onBack = onBack,
                actions = {

                    IconButton(onClick = onGoToCart) {
                        BadgedBox(
                            badge = {
                                val itemCount by cartViewModel.uiState.collectAsState()
                                if (itemCount.itemCount > 0) {
                                    Badge(
                                        containerColor = FallahRed,
                                        contentColor = Color.White
                                    ) {
                                        Text("${itemCount.itemCount}", fontSize = 10.sp)
                                    }
                                }
                            }
                        ) {
                            Icon(Icons.Outlined.ShoppingCart, null, tint = Color.White)
                        }
                    }
                    // زر إضافة منتج
                    IconButton(onClick = onAddProduct) {
                        Icon(Icons.Outlined.Add, null, tint = Color.White)
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddProduct,
                containerColor = FallahGreen,
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Outlined.Add, "إضافة منتج")
            }
        }
    ) { paddingValues ->
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = FallahGreen)
            }
        } else if (products.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("📦", fontSize = 64.sp)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        "لا توجد منتجات في هذا القسم",
                        fontSize = 16.sp,
                        color = Color(0xFF888888)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = onAddProduct,
                        colors = ButtonDefaults.buttonColors(containerColor = FallahGreen)
                    ) {
                        Text("أضف أول منتج")
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(products, key = { it.id }) { product ->
                    ProductCard(
                        product = product,
                        onClick = {
                            navController.navigate(Screen.ProductDetail.createRoute(product.id))
                        },
                        onAddToCart = {
                            cartViewModel.addToCart(product)
                        }
                    )
                }
                item { Spacer(modifier = Modifier.height(80.dp)) }
            }
        }
    }
}

@Composable
fun ProductCard(
    product: Product,
    onClick: () -> Unit,
    onAddToCart: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
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
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
                if (product.description.isNotBlank()) {
                    Text(
                        product.description,
                        fontSize = 12.sp,
                        color = Color(0xFF888888),
                        maxLines = 1
                    )
                }
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "${product.sellingPrice.toInt().toLocaleString()} دج",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = FallahGreen
                    )
                    if (product.purchasePrice > 0) {
                        Text(
                            "شراء: ${product.purchasePrice.toInt().toLocaleString()} دج",
                            fontSize = 11.sp,
                            color = Color(0xFF888888)
                        )
                    }
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (product.isLowStock) Color(0xFFFFEBEE) else FallahGreen.copy(alpha = 0.1f)
                ) {
                    Text(
                        if (product.isLowStock) "⚠️ مخزون منخفض" else "${product.quantity} ${product.unit}",
                        fontSize = 11.sp,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                        color = if (product.isLowStock) FallahRed else FallahGreen
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Button(
                    onClick = onAddToCart,
                    colors = ButtonDefaults.buttonColors(containerColor = FallahGreen),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.height(32.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp)
                ) {
                    Icon(
                        Icons.Outlined.AddShoppingCart,
                        null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("أضف", fontSize = 12.sp)
                }
            }
        }
    }
}