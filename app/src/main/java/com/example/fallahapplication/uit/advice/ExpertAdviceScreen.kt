package com.example.fallahapplication.uit.advice

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Send
import androidx.compose.material.icons.filled.*
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
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.fallahapplication.data.model.Customer
import com.example.fallahapplication.uit.components.FallahTopBar
import com.example.fallahapplication.uit.components.toLocaleString
import com.example.fallahapplication.ui.theme.FallahGreen
import com.example.fallahapplication.ui.theme.FallahGreenSurface
import com.example.fallahapplication.viewmodel.CustomerViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpertAdviceScreen(
    onBack: () -> Unit,
    customerViewModel: CustomerViewModel = hiltViewModel()
) {
    val customers by customerViewModel.customers.collectAsState()
    val context = LocalContext.current

    var adviceText by remember { mutableStateOf("تذكير: حان موعد تسميد الأراضي، يرجى استخدام الأسمدة الورقية.") }
    var selectedCustomers by remember { mutableStateOf(setOf<Customer>()) }
    var selectAll by remember { mutableStateOf(false) }
    var showSendDialog by remember { mutableStateOf(false) }
    var showCopyDialog by remember { mutableStateOf(false) }

    val customersWithPhone = customers.filter { it.phone.isNotBlank() && it.phone.length >= 9 }

    LaunchedEffect(selectAll) {
        selectedCustomers = if (selectAll) customersWithPhone.toSet() else emptySet()
    }

    Scaffold(
        topBar = {
            FallahTopBar(
                title = "👨‍🌾 إرشادات الخبير",
                subtitle = "أرسل نصائح وإرشادات للفلاحين",
                onBack = onBack
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFFF5F7F0))
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
            ) {
                // بطاقة المعلومات
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = FallahGreenSurface),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = FallahGreen.copy(alpha = 0.15f),
                            modifier = Modifier.size(50.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text("👨‍🌾", fontSize = 28.sp)
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                "إرشادات الخبير الزراعي",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF2C3E50)
                            )
                            Text(
                                "يمكنك كتابة إرشادات أو تحذيرات وإرسالها للفلاحين",
                                fontSize = 11.sp,
                                color = Color(0xFF666666)
                            )
                        }
                    }
                }

                // حقل كتابة الإرشاد
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            "📝 كتابة إرشاد جديد",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF2C3E50)
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = adviceText,
                            onValueChange = { adviceText = it },
                            label = { Text("محتوى الإرشاد") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            minLines = 4,
                            maxLines = 6,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = FallahGreen
                            )
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            "أمثلة سريعة:",
                            fontSize = 11.sp,
                            color = Color(0xFF888888)
                        )
                        Spacer(modifier = Modifier.height(6.dp))

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Surface(
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable {
                                        adviceText = "⚠️ تحذير: ظهرت آفة المن على الأشجار، يرجى رش المبيد المناسب فوراً."
                                    },
                                shape = RoundedCornerShape(16.dp),
                                color = FallahGreen.copy(alpha = 0.1f)
                            ) {
                                Box(modifier = Modifier.height(36.dp), contentAlignment = Alignment.Center) {
                                    Text("تحذير من الآفات", fontSize = 10.sp, color = FallahGreen)
                                }
                            }

                            Surface(
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable {
                                        adviceText = "💧 نصيحة: نظراً لارتفاع الحرارة، يرجى زيادة الري بمعدل 20%."
                                    },
                                shape = RoundedCornerShape(16.dp),
                                color = FallahGreen.copy(alpha = 0.1f)
                            ) {
                                Box(modifier = Modifier.height(36.dp), contentAlignment = Alignment.Center) {
                                    Text("نصيحة الري", fontSize = 10.sp, color = FallahGreen)
                                }
                            }

                            Surface(
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable {
                                        adviceText = "🌱 تذكير: حان موعد تسميد الأراضي، يرجى استخدام الأسمدة الورقية."
                                    },
                                shape = RoundedCornerShape(16.dp),
                                color = FallahGreen.copy(alpha = 0.1f)
                            ) {
                                Box(modifier = Modifier.height(36.dp), contentAlignment = Alignment.Center) {
                                    Text("موعد التسميد", fontSize = 10.sp, color = FallahGreen)
                                }
                            }
                        }
                    }
                }

                // اختيار المستلمين
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "👥 إرسال إلى:",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF2C3E50)
                            )

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                modifier = Modifier.clickable { selectAll = !selectAll }
                            ) {
                                Checkbox(
                                    checked = selectAll,
                                    onCheckedChange = { selectAll = it },
                                    colors = CheckboxDefaults.colors(checkedColor = FallahGreen)
                                )
                                Text("اختيار الكل", fontSize = 12.sp, color = Color(0xFF666666))
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            "المرسل إليهم (${selectedCustomers.size} من ${customersWithPhone.size})",
                            fontSize = 12.sp,
                            color = FallahGreen,
                            fontWeight = FontWeight.Medium
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        if (customersWithPhone.isEmpty()) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(32.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("📱", fontSize = 40.sp)
                                    Text("لا يوجد زبائن مسجلين", fontSize = 13.sp, color = Color(0xFF888888))
                                    Text("أضف زبائن وأرقام هواتفهم أولاً", fontSize = 11.sp, color = Color(0xFFAAAAAA))
                                }
                            }
                        } else {
                            LazyColumn(
                                modifier = Modifier.heightIn(max = 200.dp),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                items(customersWithPhone) { customer ->
                                    CustomerCheckboxItem(
                                        customer = customer,
                                        isSelected = selectedCustomers.contains(customer),
                                        onToggle = {
                                            selectedCustomers = if (selectedCustomers.contains(customer)) {
                                                selectedCustomers - customer
                                            } else {
                                                selectedCustomers + customer
                                            }
                                        }
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }

            // أزرار الإجراءات
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // زر نسخ الرسالة
                Button(
                    onClick = {
                        copyToClipboard(context, adviceText)
                        Toast.makeText(context, "📋 تم نسخ الإرشاد إلى الحافظة", Toast.LENGTH_SHORT).show()
                    },
                    enabled = adviceText.isNotBlank(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF607D8B))
                ) {
                    Icon(Icons.Outlined.ContentCopy, contentDescription = null, modifier = Modifier.size(20.dp), tint = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("نسخ الإرشاد", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
                }

                // زر عرض الأرقام وإرسال
                Button(
                    onClick = { showSendDialog = true },
                    enabled = adviceText.isNotBlank() && selectedCustomers.isNotEmpty(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .height(52.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = FallahGreen)
                ) {
                    Icon(Icons.AutoMirrored.Outlined.Send, contentDescription = null, modifier = Modifier.size(20.dp), tint = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "📨 إرسال للـ ${selectedCustomers.size} فلاح",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }

    // ✅ نافذة عرض الأرقام والإرسال الفردي
    if (showSendDialog) {
        AlertDialog(
            onDismissRequest = { showSendDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("📱", fontSize = 24.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("إرسال الإرشاد", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column {
                    Text("قم بنسخ الإرشاد أولاً، ثم اختر الفلاح الذي تريد إرسال الرسالة له:", fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        "📋 الإرشاد:",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = FallahGreenSurface,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            adviceText,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(10.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        "👥 أرقام الفلاحين (${selectedCustomers.size}):",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(4.dp))

                    // قائمة الأرقام
                    LazyColumn(
                        modifier = Modifier.heightIn(max = 250.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        items(selectedCustomers.toList()) { customer ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        sendWhatsAppDirect(context, customer.phone, adviceText, customer.name)
                                    }
                                    .background(Color(0xFFF5F5F5), RoundedCornerShape(8.dp))
                                    .padding(10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(customer.name, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                                    Text("📞 ${customer.phone}", fontSize = 11.sp, color = Color(0xFF888888))
                                }
                                Button(
                                    onClick = {
                                        sendWhatsAppDirect(context, customer.phone, adviceText, customer.name)
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = FallahGreen),
                                    modifier = Modifier.height(32.dp)
                                ) {
                                    Text("إرسال", fontSize = 11.sp)
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showSendDialog = false }) {
                    Text("إغلاق")
                }
            }
        )
    }
}

@Composable
fun CustomerCheckboxItem(
    customer: Customer,
    isSelected: Boolean,
    onToggle: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onToggle)
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Checkbox(
                checked = isSelected,
                onCheckedChange = { onToggle() },
                colors = CheckboxDefaults.colors(checkedColor = FallahGreen)
            )
            Column {
                Text(customer.name, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                Text("📞 ${customer.phone}", fontSize = 10.sp, color = Color(0xFF888888))
            }
        }
        if (customer.totalDebt > 0) {
            Text(
                "دين: ${customer.totalDebt.toInt().toLocaleString()} دج",
                fontSize = 10.sp,
                color = Color(0xFFE53935)
            )
        }
    }
}

// دالة نسخ إلى الحافظة
fun copyToClipboard(context: Context, text: String) {
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clip = ClipData.newPlainText("إرشاد الخبير", text)
    clipboard.setPrimaryClip(clip)
}

// دالة إرسال مباشر عبر واتساب
fun sendWhatsAppDirect(context: Context, phone: String, message: String, customerName: String) {
    val formattedPhone = formatPhoneNumber(phone)
    val fullMessage = buildAdviceMessage(message, customerName)

    if (formattedPhone.isNotEmpty()) {
        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse("https://wa.me/$formattedPhone?text=${Uri.encode(fullMessage)}")
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    } else {
        Toast.makeText(context, "رقم الهاتف غير صحيح: $phone", Toast.LENGTH_SHORT).show()
    }
}

fun formatPhoneNumber(phone: String): String {
    var cleaned = phone.trim()
        .replace(" ", "")
        .replace("-", "")
        .replace("(", "")
        .replace(")", "")

    if (cleaned.startsWith("+")) {
        cleaned = cleaned.substring(1)
    }

    return when {
        cleaned.startsWith("0") -> "213${cleaned.drop(1)}"
        cleaned.startsWith("213") -> cleaned
        cleaned.length == 9 -> "213$cleaned"
        cleaned.length == 10 && cleaned.startsWith("5") -> "213$cleaned"
        cleaned.matches(Regex("^213[0-9]{9}\$")) -> cleaned
        else -> ""
    }
}

fun buildAdviceMessage(adviceText: String, customerName: String): String {
    return """
        🌿 *محل الأمل للأدوية الفلاحية*
        ━━━━━━━━━━━━━━━━━━━━
        👨‍🌾 *إرشادات الخبير الزراعي*
        ━━━━━━━━━━━━━━━━━━━━
        
        السيد/ة $customerName
        
        $adviceText
        
        ━━━━━━━━━━━━━━━━━━━━
        نتمنى لكم موسمًا مباركًا 🌾
    """.trimIndent()
}