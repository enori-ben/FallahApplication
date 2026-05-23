package com.example.fallahapplication

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fallahapplication.ui.theme.*

// ───── Top App Bar ─────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FallahTopBar(
    title: String,
    subtitle: String = "",
    onBack: (() -> Unit)? = null,
    actions: @Composable RowScope.() -> Unit = {}
) {
    TopAppBar(
        title = {
            Column {
                Text(title, style = MaterialTheme.typography.titleMedium, color = Color.White)
                if (subtitle.isNotBlank())
                    Text(subtitle, fontSize = 11.sp, color = Color.White.copy(alpha = .8f))
            }
        },
        navigationIcon = {
            if (onBack != null) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Outlined.ArrowForward, contentDescription = "رجوع", tint = Color.White)
                }
            }
        },
        actions = actions,
        colors = TopAppBarDefaults.topAppBarColors(containerColor = FallahGreen)
    )
}

// ───── Stat Card ─────
@Composable
fun StatCard(
    value: String,
    label: String,
    valueColor: Color = FallahGreen,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(10.dp),
        border = BorderStroke(.5.dp, FallahCardBorder),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(
            modifier = Modifier.padding(10.dp).fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(value, fontSize = 20.sp, fontWeight = FontWeight.SemiBold, color = valueColor)
            Text(label, fontSize = 11.sp, color = Color(0xFF888888), modifier = Modifier.padding(top = 2.dp))
        }
    }
}

// ───── Section Card ─────
@Composable
fun SectionCard(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(.5.dp, FallahCardBorder),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(0.dp),
        content = { Column(modifier = Modifier.padding(14.dp), content = content) }
    )
}

// ───── Green Banner ─────
@Composable
fun GreenBanner(title: String, subtitle: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(FallahGreen)
            .padding(14.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(title, fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
            Text(subtitle, fontSize = 12.sp, color = Color.White.copy(alpha = .85f), modifier = Modifier.padding(top = 4.dp))
        }
    }
}

// ───── Category Card ─────
@Composable
fun CategoryCard(
    icon: String,
    name: String,
    count: String,
    countAlert: Boolean = false,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(.5.dp, FallahCardBorder),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(icon, fontSize = 32.sp)
            Text(name, fontSize = 15.sp, fontWeight = FontWeight.Medium, modifier = Modifier.padding(top = 6.dp), textAlign = TextAlign.Center)
            Text(
                count,
                fontSize = 12.sp,
                color = if (countAlert) FallahRed else Color(0xFF888888),
                fontWeight = if (countAlert) FontWeight.Medium else FontWeight.Normal,
                modifier = Modifier.padding(top = 3.dp)
            )
        }
    }
}

// ───── Payment Type Button ─────
@Composable
fun PayTypeChip(label: String, selected: Boolean, onClick: () -> Unit) {
    val borderColor = if (selected) FallahGreen else Color(0xFFDDDDDD)
    val bgColor     = if (selected) FallahGreenSurface else Color.White
    val textColor   = if (selected) FallahGreen else Color(0xFF555555)
    Surface(
        modifier = Modifier.clickable(onClick = onClick),
        shape = RoundedCornerShape(8.dp),
        color = bgColor,
        border = BorderStroke(if (selected) 1.5.dp else .5.dp, borderColor)
    ) {
        Text(
            label,
            modifier = Modifier.padding(vertical = 9.dp, horizontal = 4.dp).fillMaxWidth(),
            textAlign = TextAlign.Center,
            fontSize = 13.sp,
            fontWeight = if (selected) FontWeight.Medium else FontWeight.Normal,
            color = textColor
        )
    }
}

// ───── Low Stock Badge ─────
@Composable
fun LowStockBadge() {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .background(Color(0xFFFFEBEE))
            .padding(horizontal = 6.dp, vertical = 2.dp)
    ) {
        Text("مخزون منخفض", fontSize = 10.sp, color = FallahRed)
    }
}

// ───── Green Primary Button ─────
@Composable
fun FallahButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.fillMaxWidth().height(50.dp),
        shape = RoundedCornerShape(10.dp),
        colors = ButtonDefaults.buttonColors(containerColor = FallahGreen)
    ) {
        Text(text, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
    }
}

// ───── Outlined Secondary Button ─────
@Composable
fun FallahOutlinedButton(text: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.fillMaxWidth().height(50.dp),
        shape = RoundedCornerShape(10.dp),
        border = BorderStroke(1.5.dp, FallahGreen),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = FallahGreen)
    ) {
        Text(text, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
    }
}

// ───── Divider Row ─────
@Composable
fun ReportRow(label: String, value: String, valueColor: Color = FallahGreen, bold: Boolean = false) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, fontSize = 14.sp, color = if (bold) Color(0xFF1A1A1A) else Color(0xFF444444), fontWeight = if (bold) FontWeight.SemiBold else FontWeight.Normal)
        Text(value, fontSize = 14.sp, fontWeight = if (bold) FontWeight.SemiBold else FontWeight.Medium, color = valueColor)
    }
    if (!bold) HorizontalDivider(color = Color(0xFFF0F0F0), thickness = .5.dp)
}