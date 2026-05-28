package com.example.fallahapplication.uit.cart

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fallahapplication.data.model.CartItem
import com.example.fallahapplication.data.model.PaymentType
import com.example.fallahapplication.uit.components.*
import com.example.fallahapplication.ui.theme.*
import com.example.fallahapplication.uit.utils.PrintHelper
import com.example.fallahapplication.viewmodel.CartUiState
import com.example.fallahapplication.viewmodel.CartViewModel
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(
    onBack: () -> Unit,
    onSaleComplete: () -> Unit,
    viewModel: CartViewModel
) {
    val context = LocalContext.current
    val printHelper = remember { PrintHelper(context) }
    val uiState by viewModel.uiState.collectAsState()

    if (uiState.saleCompleted) {
        SaleSuccessScreen(
            uiState = uiState,
            onNewSale = {
                viewModel.clearCart()
                viewModel.resetSaleCompleted()
                onSaleComplete()
            },
            printHelper = printHelper
        )
        return
    }

    if (uiState.cartItems.isEmpty()) {
        EmptyCartScreen(onBack = onBack)
        return
    }

    Scaffold(
        topBar = {
            FallahTopBar(
                title = "سلة البيع",
                subtitle = "${uiState.itemCount} منتج",
                onBack = onBack
            )
        },
        bottomBar = {
            Surface(shadowElevation = 8.dp) {
                Column(modifier = Modifier.fillMaxWidth().padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("الإجمالي", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                        Text(
                            "${uiState.totalAmount.toInt().toLocaleString()} دج",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = FallahGreen
                        )
                    }
                    FallahButton(
                        text = if (uiState.isLoading) "جاري الحفظ..." else "✓  تأكيد البيع",
                        enabled = !uiState.isLoading,
                        onClick = viewModel::completeSale
                    )
                    uiState.error?.let { err ->
                        Text(
                            err,
                            fontSize = 12.sp,
                            color = FallahRed,
                            modifier = Modifier.fillMaxWidth().padding(top = 6.dp),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    ) { padding ->
        LazyColumn(
            contentPadding = PaddingValues(12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.padding(padding)
        ) {
            // عناصر السلة
            items(uiState.cartItems, key = { it.product.id }) { item ->
                CartItemCard(
                    item = item,
                    onIncrease = { viewModel.updateQuantity(item.product.id, item.quantity + 1) },
                    onDecrease = { viewModel.updateQuantity(item.product.id, item.quantity - 1) },
                    onRemove   = { viewModel.removeFromCart(item.product.id) }
                )
            }

            // معلومات الزبون
            item {
                SectionCard {
                    Text("معلومات الزبون", style = MaterialTheme.typography.titleSmall, modifier = Modifier.padding(bottom = 8.dp))
                    OutlinedTextField(
                        value = uiState.customerName,
                        onValueChange = { viewModel.setCustomerName(it) },
                        label = { Text("اسم الزبون") },
                        placeholder = { Text("زبون نقدي") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        singleLine = true,
                        leadingIcon = { Icon(Icons.Outlined.Person, null, tint = Color(0xFF888888)) },
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = FallahGreen)
                    )
                }
            }

            // طريقة الدفع
            item {
                SectionCard {
                    Text("طريقة الدفع", style = MaterialTheme.typography.titleSmall, modifier = Modifier.padding(bottom = 8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        PayTypeChip(
                            label    = "نقدًا",
                            selected = uiState.paymentType == PaymentType.CASH,
                            onClick  = { viewModel.setPaymentType(PaymentType.CASH) },
                            modifier = Modifier.weight(1f)
                        )
                        PayTypeChip(
                            label    = "جزئي",
                            selected = uiState.paymentType == PaymentType.PARTIAL,
                            onClick  = { viewModel.setPaymentType(PaymentType.PARTIAL) },
                            modifier = Modifier.weight(1f)
                        )
                        PayTypeChip(
                            label    = "دين",
                            selected = uiState.paymentType == PaymentType.DEBT,
                            onClick  = { viewModel.setPaymentType(PaymentType.DEBT) },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    if (uiState.paymentType == PaymentType.PARTIAL) {
                        Spacer(Modifier.height(10.dp))
                        OutlinedTextField(
                            value = uiState.paidAmount,
                            onValueChange = viewModel::setPaidAmount,
                            label = { Text("المبلغ المدفوع (دج)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp),
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = FallahGreen)
                        )
                        if (uiState.remainingDebt > 0) {
                            Spacer(Modifier.height(8.dp))
                            Box(
                                Modifier.fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFFFFEBEE))
                                    .padding(10.dp)
                            ) {
                                Text(
                                    "المتبقي كدين: ${uiState.remainingDebt.toInt().toLocaleString()} دج",
                                    fontSize = 13.sp, color = FallahRed, fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }

                    if (uiState.paymentType == PaymentType.DEBT) {
                        Spacer(Modifier.height(8.dp))
                        Box(
                            Modifier.fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFFFFEBEE))
                                .padding(10.dp)
                        ) {
                            Text(
                                "سيُسجَّل كدين: ${uiState.totalAmount.toInt().toLocaleString()} دج",
                                fontSize = 13.sp, color = FallahRed, fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }

            // ملاحظات
            item {
                SectionCard {
                    Text("ملاحظات", style = MaterialTheme.typography.titleSmall, modifier = Modifier.padding(bottom = 8.dp))
                    OutlinedTextField(
                        value = uiState.notes,
                        onValueChange = viewModel::setNotes,
                        label = { Text("اختياري...") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        minLines = 2,
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = FallahGreen)
                    )
                }
            }

            item { Spacer(Modifier.height(20.dp)) }
        }
    }
}

// ══════════════════════════════════════════════════════════
// صفحة نجاح البيع — تظهر تفاصيل الفاتورة كاملة
// ══════════════════════════════════════════════════════════
@Composable
fun SaleSuccessScreen(
    uiState: CartUiState,
    onNewSale: () -> Unit,
    printHelper: PrintHelper
) {
    val context = LocalContext.current

    val customerName  = uiState.customerName.ifBlank { "زبون نقدي" }
    val totalAmount   = uiState.totalAmount
    val paidAmount    = when (uiState.paymentType) {
        PaymentType.CASH    -> totalAmount
        PaymentType.PARTIAL -> uiState.paidAmount.toDoubleOrNull() ?: 0.0
        PaymentType.DEBT    -> 0.0
    }
    val debtAmount    = totalAmount - paidAmount
    val paymentLabel  = uiState.paymentType.label

    // طباعة الفاتورة عند ظهور الشاشة
    LaunchedEffect(Unit) {
        if (uiState.lastSaleId != null) {
            printHelper.printInvoice(
                invoiceNumber = uiState.lastSaleId.toString(),
                customerName = uiState.customerName.ifBlank { "زبون نقدي" },
                customerPhone = "",
                items = uiState.cartItems,
                totalAmount = uiState.totalAmount,
                paidAmount = uiState.paidAmount.toDoubleOrNull() ?: 0.0,
                paymentType = uiState.paymentType,
                date = System.currentTimeMillis()
            )
        }
    }

    Scaffold(
        topBar = {
            FallahTopBar(title = "تمّت عملية البيع ✓")
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // ── رأس النجاح ──
            item {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(RoundedCornerShape(40.dp))
                            .background(FallahGreenSurface),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Outlined.CheckCircle,
                            contentDescription = null,
                            tint = FallahGreen,
                            modifier = Modifier.size(52.dp)
                        )
                    }
                    Spacer(Modifier.height(10.dp))
                    Text(
                        "تمّت عملية البيع بنجاح!",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = FallahGreen
                    )
                    Text(
                        "فاتورة رقم: ${uiState.lastSaleId?.let { "INV-${it.toString().padStart(5,'0')}" } ?: "---"}",
                        fontSize = 13.sp,
                        color = Color(0xFF888888),
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }

            // ── بيانات الزبون والدفع ──
            item {
                SectionCard {
                    Text("تفاصيل الفاتورة", style = MaterialTheme.typography.titleSmall, modifier = Modifier.padding(bottom = 10.dp))

                    InvoiceRow(label = "الزبون",        value = customerName)
                    InvoiceRow(label = "طريقة الدفع",   value = paymentLabel)
                    InvoiceRow(label = "إجمالي الفاتورة", value = "${totalAmount.toInt().toLocaleString()} دج", valueColor = FallahGreen, bold = true)

                    if (uiState.paymentType != PaymentType.CASH) {
                        InvoiceRow(
                            label      = "المبلغ المدفوع",
                            value      = "${paidAmount.toInt().toLocaleString()} دج",
                            valueColor = FallahGreen
                        )
                        InvoiceRow(
                            label      = "المتبقي كدين",
                            value      = "${debtAmount.toInt().toLocaleString()} دج",
                            valueColor = FallahRed,
                            bold       = debtAmount > 0
                        )
                    }
                }
            }

            // ── قائمة المنتجات المباعة ──
            item {
                SectionCard {
                    Text("المنتجات", style = MaterialTheme.typography.titleSmall, modifier = Modifier.padding(bottom = 10.dp))
                    uiState.cartItems.forEach { item ->
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 5.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                "${item.product.name}  ×${item.quantity}",
                                fontSize = 14.sp,
                                color = Color(0xFF333333)
                            )
                            Text(
                                "${item.total.toInt().toLocaleString()} دج",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = FallahGreen
                            )
                        }
                        HorizontalDivider(color = Color(0xFFF0F0F0), thickness = 0.5.dp)
                    }
                    // المجموع
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("المجموع", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                        Text(
                            "${totalAmount.toInt().toLocaleString()} دج",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = FallahGreen
                        )
                    }
                }
            }

            // ── أزرار الإجراءات ──
            item {
                // زر واتساب
                Button(
                    onClick = {
                        val msg = buildWhatsAppMessage(
                            customerName  = customerName,
                            cartItems     = uiState.cartItems,
                            totalAmount   = totalAmount,
                            paidAmount    = paidAmount,
                            debtAmount    = debtAmount,
                            paymentType   = uiState.paymentType,
                            invoiceId     = uiState.lastSaleId
                        )
                        val intent = Intent(Intent.ACTION_VIEW).apply {
                            data = Uri.parse("https://wa.me/?text=${Uri.encode(msg)}")
                        }
                        context.startActivity(intent)
                    },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366))
                ) {
                    Icon(Icons.Outlined.Send, contentDescription = null, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("إرسال الفاتورة عبر واتساب", fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                }

                Spacer(Modifier.height(10.dp))

                // زر بيعة جديدة
                FallahButton(
                    text    = "بيعة جديدة +",
                    onClick = onNewSale
                )
            }

            item { Spacer(Modifier.height(20.dp)) }
        }
    }
}

// ── صف في الفاتورة ──
@Composable
fun InvoiceRow(
    label: String,
    value: String,
    valueColor: Color = Color(0xFF333333),
    bold: Boolean = false
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 5.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, fontSize = 13.sp, color = Color(0xFF888888))
        Text(
            value,
            fontSize = 14.sp,
            fontWeight = if (bold) FontWeight.Bold else FontWeight.Medium,
            color = valueColor
        )
    }
    HorizontalDivider(color = Color(0xFFF5F5F5), thickness = 0.5.dp)
}

// ── بناء رسالة واتساب ──
fun buildWhatsAppMessage(
    customerName: String,
    cartItems: List<CartItem>,
    totalAmount: Double,
    paidAmount: Double,
    debtAmount: Double,
    paymentType: PaymentType,
    invoiceId: Long?
): String {
    val invoiceNum = invoiceId?.let { "INV-${it.toString().padStart(5, '0')}" } ?: ""
    val sb = StringBuilder()
    sb.appendLine("*🌿 محل الأمل للأدوية الفلاحية*")
    sb.appendLine("─────────────────────")
    if (invoiceNum.isNotBlank()) sb.appendLine("📄 فاتورة رقم: $invoiceNum")
    sb.appendLine("👤 الزبون: $customerName")
    sb.appendLine("─────────────────────")
    sb.appendLine("*المنتجات:*")
    cartItems.forEach { item ->
        sb.appendLine("• ${item.product.name} × ${item.quantity}  =  ${item.total.toInt().toLocaleString2()} دج")
    }
    sb.appendLine("─────────────────────")
    sb.appendLine("💰 *الإجمالي: ${totalAmount.toInt().toLocaleString2()} دج*")
    when (paymentType) {
        PaymentType.CASH -> sb.appendLine("✅ تم الدفع نقدًا")
        PaymentType.PARTIAL -> {
            sb.appendLine("💵 المدفوع: ${paidAmount.toInt().toLocaleString2()} دج")
            sb.appendLine("⚠️ المتبقي: ${debtAmount.toInt().toLocaleString2()} دج")
        }
        PaymentType.DEBT -> sb.appendLine("📌 مسجّل كدين كامل: ${totalAmount.toInt().toLocaleString2()} دج")
    }
    sb.appendLine("─────────────────────")
    sb.append("شكراً على ثقتكم 🌿")
    return sb.toString()
}

private fun Int.toLocaleString2(): String = "%,d".format(this)

// ── بطاقة عنصر السلة ──
@Composable
fun CartItemCard(
    item: CartItem,
    onIncrease: () -> Unit,
    onDecrease: () -> Unit,
    onRemove: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(item.product.name, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                Text(
                    "${item.unitPrice.toInt().toLocaleString()} دج / ${item.product.unit}",
                    fontSize = 11.sp, color = Color(0xFF888888)
                )
            }
            Text(
                "${item.total.toInt().toLocaleString()} دج",
                fontSize = 14.sp, fontWeight = FontWeight.SemiBold,
                color = FallahGreen, modifier = Modifier.padding(end = 8.dp)
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onDecrease, modifier = Modifier.size(32.dp)) {
                    Icon(Icons.Outlined.Remove, contentDescription = null, tint = FallahGreen, modifier = Modifier.size(18.dp))
                }
                Text(
                    "${item.quantity}",
                    fontSize = 15.sp, fontWeight = FontWeight.Medium,
                    modifier = Modifier.width(28.dp),
                    textAlign = TextAlign.Center
                )
                IconButton(onClick = onIncrease, modifier = Modifier.size(32.dp)) {
                    Icon(Icons.Outlined.Add, contentDescription = null, tint = FallahGreen, modifier = Modifier.size(18.dp))
                }
                IconButton(onClick = onRemove, modifier = Modifier.size(32.dp)) {
                    Icon(Icons.Outlined.Delete, contentDescription = null, tint = FallahRed, modifier = Modifier.size(18.dp))
                }
            }
        }
    }
}

// ── سلة فارغة ──
@Composable
fun EmptyCartScreen(onBack: () -> Unit) {
    Scaffold(topBar = { FallahTopBar(title = "سلة البيع", onBack = onBack) }) { padding ->
        Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("🛒", fontSize = 64.sp)
                Spacer(Modifier.height(12.dp))
                Text("السلة فارغة", fontSize = 18.sp, color = Color(0xFF888888))
                Spacer(Modifier.height(6.dp))
                Text("أضف منتجات من صفحة الأقسام", fontSize = 13.sp, color = Color(0xFFAAAAAA))
            }
        }
    }
}