package com.example.fallahapplication.uit.settings

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.fallahapplication.MainActivity
import com.example.fallahapplication.applySavedLanguage
import com.example.fallahapplication.restartApp
import com.example.fallahapplication.setLocale
import com.example.fallahapplication.ui.theme.FallahGreen
import com.example.fallahapplication.uit.components.FallahTopBar
import com.example.fallahapplication.uit.components.SectionCard
import com.example.fallahapplication.uit.utils.BackupHelper
import com.example.fallahapplication.viewmodel.SettingsViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    var showBackupDialog by remember { mutableStateOf(false) }
    var showClearDataDialog by remember { mutableStateOf(false) }
    var showExportDialog by remember { mutableStateOf(false) }
    var showLanguageDialog by remember { mutableStateOf(false) }
    var currentLanguage by remember {
        mutableStateOf(
            if (com.example.fallahapplication.applySavedLanguage(context).let {
                    context.getSharedPreferences("settings", Context.MODE_PRIVATE)
                        .getString("language", "ar") == "en"
                }) "English" else "العربية"
        )
    }

    val backupHelper = BackupHelper(context, viewModel.repository)

    Scaffold(
        topBar = { FallahTopBar(title = "الإعدادات", onBack = onBack) }
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
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("🌿", fontSize = 48.sp)
                            Text("تطبيق الفلاح", fontSize = 18.sp, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
                            Text("الإصدار 1.0.0", fontSize = 12.sp, color = Color(0xFF888888))
                            Text("محل الأمل للأدوية الفلاحية", fontSize = 12.sp, color = Color(0xFF888888))
                        }
                    }
                }
            }

            item {
                SectionCard {
                    Text("إدارة البيانات", style = MaterialTheme.typography.titleSmall)
                    Spacer(modifier = Modifier.height(8.dp))

                    SettingItem(
                        icon = Icons.Outlined.Download,
                        title = "تصدير البيانات",
                        subtitle = "تصدير المبيعات والديون والزبائن",
                        onClick = { showExportDialog = true }
                    )

                    HorizontalDivider(color = Color(0xFFEEEEEE), thickness = 0.5.dp)

                    SettingItem(
                        icon = Icons.Outlined.Backup,
                        title = "نسخ احتياطي",
                        subtitle = "تصدير جميع البيانات إلى ملف",
                        onClick = { showBackupDialog = true }
                    )

                    HorizontalDivider(color = Color(0xFFEEEEEE), thickness = 0.5.dp)

                    SettingItem(
                        icon = Icons.Outlined.Language,
                        title = "اللغة / Language",
                        subtitle = currentLanguage,
                        onClick = { showLanguageDialog = true }
                    )

                    HorizontalDivider(color = Color(0xFFEEEEEE), thickness = 0.5.dp)

                    SettingItem(
                        icon = Icons.Outlined.Delete,
                        title = "مسح جميع البيانات",
                        subtitle = "حذف جميع المنتجات والزبائن والفواتير",
                        onClick = { showClearDataDialog = true },
                        titleColor = Color(0xFFE53935)
                    )
                }
            }

            item {
                SectionCard {
                    Text("المساعدة", style = MaterialTheme.typography.titleSmall)
                    Spacer(modifier = Modifier.height(8.dp))

                    SettingItem(
                        icon = Icons.Outlined.Info,
                        title = "حول التطبيق",
                        subtitle = "معلومات عن التطبيق والمطور",
                        onClick = {
                            Toast.makeText(context, "تطبيق فلاح - إدارة المتاجر الزراعية", Toast.LENGTH_SHORT).show()
                        }
                    )

                    SettingItem(
                        icon = Icons.Outlined.Help,
                        title = "الدعم الفني",
                        subtitle = "للتواصل مع الدعم",
                        onClick = {
                            Toast.makeText(context, "للتواصل: contact@fallahapp.com", Toast.LENGTH_SHORT).show()
                        }
                    )
                }
            }
        }
    }

    if (showBackupDialog) {
        AlertDialog(
            onDismissRequest = { showBackupDialog = false },
            title = { Text("نسخ احتياطي") },
            text = { Text("هل تريد تصدير جميع بياناتك إلى ملف؟") },
            confirmButton = {
                TextButton(onClick = {
                    CoroutineScope(Dispatchers.Main).launch {
                        val path = backupHelper.exportData()
                        if (path != null) {
                            Toast.makeText(context, "تم التصدير إلى: $path", Toast.LENGTH_LONG).show()
                        } else {
                            Toast.makeText(context, "فشل التصدير", Toast.LENGTH_SHORT).show()
                        }
                    }
                    showBackupDialog = false
                }) {
                    Text("تصدير")
                }
            },
            dismissButton = {
                TextButton(onClick = { showBackupDialog = false }) {
                    Text("إلغاء")
                }
            }
        )
    }

    if (showLanguageDialog) {
        AlertDialog(
            onDismissRequest = { showLanguageDialog = false },
            title = { Text("اختر اللغة / Select Language") },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                setLocale(context, "ar")
                                currentLanguage = "العربية"
                                showLanguageDialog = false
                                // إعادة تشغيل التطبيق
                                restartApp(context)
                            },
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (currentLanguage == "العربية")
                                Color(0xFFE8F5E8) else Color.White
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("🇸🇦", fontSize = 24.sp)
                            Text("العربية", fontSize = 16.sp)
                            if (currentLanguage == "العربية") {
                                Spacer(modifier = Modifier.weight(1f))
                                Icon(Icons.Outlined.CheckCircle, null, tint = FallahGreen)
                            }
                        }
                    }

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                setLocale(context, "en")
                                currentLanguage = "English"
                                showLanguageDialog = false
                                // إعادة تشغيل التطبيق
                                restartApp(context)
                            },
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (currentLanguage == "English")
                                Color(0xFFE8F5E8) else Color.White
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("🇬🇧", fontSize = 24.sp)
                            Text("English", fontSize = 16.sp)
                            if (currentLanguage == "English") {
                                Spacer(modifier = Modifier.weight(1f))
                                Icon(Icons.Outlined.CheckCircle, null, tint = FallahGreen)
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showLanguageDialog = false }) {
                    Text("إلغاء")
                }
            }
        )
    }

    if (showClearDataDialog) {
        AlertDialog(
            onDismissRequest = { showClearDataDialog = false },
            title = { Text("مسح جميع البيانات") },
            text = { Text("هل أنت متأكد؟ لا يمكن التراجع عن هذا الإجراء.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.clearAllData()
                        showClearDataDialog = false
                        Toast.makeText(context, "تم مسح جميع البيانات", Toast.LENGTH_SHORT).show()
                    }
                ) {
                    Text("نعم", color = FallahGreen)
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearDataDialog = false }) {
                    Text("إلغاء")
                }
            }
        )
    }

    if (showExportDialog) {
        AlertDialog(
            onDismissRequest = { showExportDialog = false },
            title = { Text("تصدير البيانات") },
            text = { Text("سيتم تصدير البيانات إلى ملف CSV") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.exportData(context)
                        showExportDialog = false
                    }
                ) {
                    Text("تصدير", color = FallahGreen)
                }
            },
            dismissButton = {
                TextButton(onClick = { showExportDialog = false }) {
                    Text("إلغاء")
                }
            }
        )
    }
}

@Composable
fun SettingItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    titleColor: Color = Color(0xFF1A1A1A)
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = FallahGreen, modifier = Modifier.size(24.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, fontSize = 14.sp, fontWeight = androidx.compose.ui.text.font.FontWeight.Medium, color = titleColor)
            Text(subtitle, fontSize = 11.sp, color = Color(0xFF888888))
        }
        Icon(Icons.Outlined.ChevronRight, contentDescription = null, tint = Color(0xFFCCCCCC))
    }
}