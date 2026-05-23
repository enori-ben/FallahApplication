package com.example.fallahapplication.ui.theme

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

val FallahGreen        = Color(0xFF2D6A2D)
val FallahGreenLight   = Color(0xFF4CAF50)
val FallahGreenSurface = Color(0xFFE8F5E8)
val FallahGreenBanner  = Color(0xFF1B5E20)
val FallahRed          = Color(0xFFE53935)
val FallahAmber        = Color(0xFFF57C00)
val FallahBg           = Color(0xFFF4F8F4)
val FallahCardBorder   = Color(0xFF0C260C)

private val LightColorScheme = lightColorScheme(
    primary         = FallahGreen,
    onPrimary       = Color.White,
    primaryContainer= FallahGreenSurface,
    secondary       = FallahGreenLight,
    background      = FallahBg,
    surface         = Color.White,
    onBackground    = Color(0xFF1A1A1A),
    onSurface       = Color(0xFF1A1A1A),
    outline         = FallahCardBorder,
    error           = FallahRed
)

@Composable
fun FallahAppTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = Typography(
            titleLarge  = TextStyle(fontWeight = FontWeight.SemiBold, fontSize = 20.sp),
            titleMedium = TextStyle(fontWeight = FontWeight.Medium,   fontSize = 16.sp),
            titleSmall  = TextStyle(fontWeight = FontWeight.Medium,   fontSize = 14.sp),
            bodyLarge   = TextStyle(fontWeight = FontWeight.Normal,   fontSize = 15.sp),
            bodyMedium  = TextStyle(fontWeight = FontWeight.Normal,   fontSize = 13.sp),
            labelSmall  = TextStyle(fontWeight = FontWeight.Normal,   fontSize = 10.sp)
        ),
        content = content
    )
}