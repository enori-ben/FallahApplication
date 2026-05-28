package com.example.fallahapplication.uit.products

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.fallahapplication.data.model.Product
import com.example.fallahapplication.uit.components.FallahButton
import com.example.fallahapplication.uit.components.FallahTopBar
import com.example.fallahapplication.ui.theme.FallahGreen
import com.example.fallahapplication.ui.theme.FallahGreenSurface
import com.example.fallahapplication.ui.theme.FallahRed
import com.example.fallahapplication.viewmodel.HomeViewModel
import com.example.fallahapplication.viewmodel.ProductViewModel

// ════════════════════════════════════════════════
// إضافة منتج جديد
// ════════════════════════════════════════════════
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddProductScreen(
    preselectedCategoryId: Long?,
    onBack: () -> Unit,
    onSaved: () -> Unit,
    productViewModel: ProductViewModel = hiltViewModel(),
    homeViewModel: HomeViewModel = hiltViewModel()
) {
    ProductFormScreen(
        title              = "إضافة منتج جديد",
        existingProduct    = null,
        preselectedCatId   = preselectedCategoryId,
        onBack             = onBack,
        onSaved            = onSaved,
        productViewModel   = productViewModel,
        homeViewModel      = homeViewModel
    )
}

// ════════════════════════════════════════════════
// تعديل منتج موجود
// ════════════════════════════════════════════════
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProductScreen(
    productId: Long,
    onBack: () -> Unit,
    onSaved: () -> Unit,
    productViewModel: ProductViewModel = hiltViewModel(),
    homeViewModel: HomeViewModel = hiltViewModel()
) {
    var product by remember { mutableStateOf<Product?>(null) }
    var loaded  by remember { mutableStateOf(false) }

    LaunchedEffect(productId) {
        product = productViewModel.getProductById(productId)
        loaded  = true
    }

    if (!loaded) {
        Box(modifier = Modifier.fillMaxSize()) {
            CircularProgressIndicator(
                modifier = Modifier.align(androidx.compose.ui.Alignment.Center),
                color = FallahGreen
            )
        }
        return
    }

    ProductFormScreen(
        title            = "تعديل المنتج",
        existingProduct  = product,
        preselectedCatId = product?.categoryId,
        onBack           = onBack,
        onSaved          = onSaved,
        productViewModel = productViewModel,
        homeViewModel    = homeViewModel
    )
}

// ════════════════════════════════════════════════
// النموذج المشترك (إضافة + تعديل)
// ════════════════════════════════════════════════
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProductFormScreen(
    title: String,
    existingProduct: Product?,
    preselectedCatId: Long?,
    onBack: () -> Unit,
    onSaved: () -> Unit,
    productViewModel: ProductViewModel,
    homeViewModel: HomeViewModel
) {
    val categories by homeViewModel.categories.collectAsState()
    val isLoading  by productViewModel.isLoading.collectAsState()

    var name        by remember { mutableStateOf(existingProduct?.name        ?: "") }
    var description by remember { mutableStateOf(existingProduct?.description ?: "") }
    var buyPrice    by remember { mutableStateOf(existingProduct?.purchasePrice?.toInt()?.toString() ?: "") }
    var sellPrice   by remember { mutableStateOf(existingProduct?.sellingPrice?.toInt()?.toString()  ?: "") }
    var quantity    by remember { mutableStateOf(existingProduct?.quantity?.toString() ?: "") }
    var minQuantity by remember { mutableStateOf(existingProduct?.minQuantity?.toString() ?: "5") }
    var unit        by remember { mutableStateOf(existingProduct?.unit ?: "وحدة") }
    var selectedCat by remember { mutableStateOf(preselectedCatId) }
    var expanded    by remember { mutableStateOf(false) }

    val profit = (sellPrice.toDoubleOrNull() ?: 0.0) - (buyPrice.toDoubleOrNull() ?: 0.0)
    val isEdit = existingProduct != null

    Scaffold(
        topBar = { FallahTopBar(title = title, onBack = onBack) }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("اسم المنتج *") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                leadingIcon = { Icon(Icons.Outlined.Inventory2, null, tint = Color(0xFF888888)) },
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = FallahGreen)
            )

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("الوصف (اختياري)") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                minLines = 2,
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = FallahGreen)
            )

            ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }) {
                OutlinedTextField(
                    value = categories.find { it.id == selectedCat }
                        ?.let { "${it.icon} ${it.name}" } ?: "اختر القسم",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("القسم *") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                    modifier = Modifier.fillMaxWidth().menuAnchor(),
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = FallahGreen)
                )
                ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                    categories.forEach { cat ->
                        DropdownMenuItem(
                            text = { Text("${cat.icon} ${cat.name}") },
                            onClick = { selectedCat = cat.id; expanded = false }
                        )
                    }
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = buyPrice,
                    onValueChange = { buyPrice = it },
                    label = { Text("سعر الشراء (دج)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = FallahGreen)
                )
                OutlinedTextField(
                    value = sellPrice,
                    onValueChange = { sellPrice = it },
                    label = { Text("سعر البيع (دج)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = FallahGreen)
                )
            }

            if (profit != 0.0) {
                Box(
                    modifier = Modifier.fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (profit > 0) FallahGreenSurface else Color(0xFFFFEBEE))
                        .padding(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            if (profit > 0) "✅ الربح:" else "⚠️ خسارة:",
                            fontSize = 13.sp,
                            color = if (profit > 0) FallahGreen else FallahRed
                        )
                        Text(
                            "${profit.toInt()} دج  (${
                                if (buyPrice.toDoubleOrNull() != null && buyPrice.toDouble() > 0)
                                    String.format("%.1f", (profit / buyPrice.toDouble()) * 100) + "%"
                                else "—"
                            })",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (profit > 0) FallahGreen else FallahRed
                        )
                    }
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = quantity,
                    onValueChange = { quantity = it },
                    label = { Text("الكمية *") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = FallahGreen)
                )
                OutlinedTextField(
                    value = unit,
                    onValueChange = { unit = it },
                    label = { Text("الوحدة") },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = FallahGreen)
                )
            }

            OutlinedTextField(
                value = minQuantity,
                onValueChange = { minQuantity = it },
                label = { Text("حد التنبيه (أقل كمية)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                supportingText = { Text("سيظهر تنبيه عند الوصول لهذه الكمية") },
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = FallahGreen)
            )

            Spacer(Modifier.height(8.dp))

            FallahButton(
                text = when {
                    isLoading -> "جاري الحفظ..."
                    isEdit    -> "💾 حفظ التعديلات"
                    else      -> "✅ إضافة المنتج"
                },
                enabled = name.isNotBlank() && selectedCat != null && !isLoading,
                onClick = {
                    val product = Product(
                        id            = existingProduct?.id ?: 0L,
                        name          = name.trim(),
                        description   = description.trim(),
                        categoryId    = selectedCat ?: 0L,
                        purchasePrice = buyPrice.toDoubleOrNull()  ?: 0.0,
                        sellingPrice  = sellPrice.toDoubleOrNull() ?: 0.0,
                        quantity      = quantity.toIntOrNull()     ?: 0,
                        minQuantity   = minQuantity.toIntOrNull()  ?: 5,
                        unit          = unit.ifBlank { "وحدة" }
                    )
                    productViewModel.saveProduct(product)
                    onSaved()
                }
            )

            Spacer(Modifier.height(24.dp))
        }
    }
}