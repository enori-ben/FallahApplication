package com.example.fallahapplication

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.fallahapplication.uit.navigation.FallahNavHost
import com.example.fallahapplication.ui.theme.FallahAppTheme
import dagger.hilt.android.AndroidEntryPoint
import java.util.Locale

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // تطبيق اللغة المحفوظة قبل إنشاء الواجهة
        applySavedLanguage(this)

        enableEdgeToEdge()
        setContent {
            FallahAppTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    FallahNavHost()
                }
            }
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        // إعادة تعيين اللغة عند تغيير الإعدادات
        applySavedLanguage(this)
    }
}

// دالة لحفظ اللغة في SharedPreferences
fun saveLanguage(context: Context, languageCode: String) {
    val prefs = context.getSharedPreferences("settings", Context.MODE_PRIVATE)
    prefs.edit().putString("language", languageCode).apply()
}

// دالة لتطبيق اللغة المحفوظة
fun applySavedLanguage(context: Context) {
    val prefs = context.getSharedPreferences("settings", Context.MODE_PRIVATE)
    val languageCode = prefs.getString("language", "ar") ?: "ar"
    setLocale(context, languageCode)
}

// دالة لتغيير اللغة
fun setLocale(context: Context, languageCode: String) {
    val locale = Locale(languageCode)
    Locale.setDefault(locale)

    val config = Configuration(context.resources.configuration)
    config.setLocale(locale)

    context.resources.updateConfiguration(config, context.resources.displayMetrics)

    // حفظ اللغة
    saveLanguage(context, languageCode)
}

// دالة لإعادة تشغيل التطبيق بعد تغيير اللغة
fun restartApp(context: Context) {
    val intent = context.packageManager.getLaunchIntentForPackage(context.packageName)
    intent?.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
    context.startActivity(intent)

    // إنهاء النشاط الحالي
    if (context is ComponentActivity) {
        context.finish()
    }
}