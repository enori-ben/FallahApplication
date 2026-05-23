package com.example.fallahapplication.uit.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowForward
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.automirrored.outlined.ArrowForward
import androidx.compose.ui.unit.sp
import com.example.fallahapplication.ui.theme.FallahCardBorder
import com.example.fallahapplication.ui.theme.FallahGreen
import com.example.fallahapplication.ui.theme.FallahGreenSurface
import com.example.fallahapplication.ui.theme.FallahRed

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
                    Icon(
                        Icons.AutoMirrored.Outlined.ArrowForward,
                        contentDescription = "رجوع",
                        tint = Color.White
                    )
                }
            }
        },
        actions = actions,
        colors = TopAppBarDefaults.topAppBarColors(containerColor = FallahGreen)
    )
}
@Composable
fun CartBadge(count: Int) {
    if (count > 0) {
        Box(
            modifier = Modifier
                .offset(x = 12.dp, y = (-8).dp)
                .size(18.dp)
                .background(FallahRed, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = if (count > 99) "99+" else count.toString(),
                color = Color.White,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    placeholder: String = "بحث...",
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 8.dp),
        placeholder = { Text("🔍 $placeholder") },
        shape = RoundedCornerShape(10.dp),
        singleLine = true,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = FallahGreen,
            unfocusedBorderColor = FallahCardBorder
        ),
        leadingIcon = { Icon(Icons.Outlined.Search, contentDescription = null, tint = Color(0xFF888888)) },
        trailingIcon = {
            if (query.isNotBlank()) {
                IconButton(onClick = { onQueryChange("") }) {
                    Icon(Icons.Outlined.Close, contentDescription = null, tint = Color(0xFF888888))
                }
            }
        }
    )
}

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

@Composable
fun CategoryCard(
    icon: String,
    name: String,
    count: String,
    countAlert: Boolean = false,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(0.5.dp, FallahCardBorder),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(icon, fontSize = 28.sp)
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = name,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF2C3E50),
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = count,
                fontSize = 10.sp,
                color = if (countAlert) FallahRed else Color(0xFF888888),
                fontWeight = if (countAlert) FontWeight.Medium else FontWeight.Normal,
                maxLines = 1
            )
        }
    }
}

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

@Composable
fun PayTypeChip(label: String, selected: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val borderColor = if (selected) FallahGreen else Color(0xFFDDDDDD)
    val bgColor     = if (selected) FallahGreenSurface else Color.White
    val textColor   = if (selected) FallahGreen else Color(0xFF555555)
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        color = bgColor,
        border = BorderStroke(if (selected) 1.5.dp else .5.dp, borderColor),
        onClick = onClick
    ) {
        Box(Modifier.padding(vertical = 9.dp), contentAlignment = Alignment.Center) {
            Text(label, fontSize = 13.sp, fontWeight = if (selected) FontWeight.Medium else FontWeight.Normal, color = textColor)
        }
    }
}

@Composable
fun LoadingIndicator() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            CircularProgressIndicator(color = FallahGreen)
            Spacer(modifier = Modifier.height(12.dp))
            Text("جاري التحميل...", fontSize = 13.sp, color = Color(0xFF888888))
        }
    }
}

@Composable
fun EmptyState(
    icon: String = "📦",
    message: String = "لا توجد بيانات",
    onClick: (() -> Unit)? = null
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(icon, fontSize = 48.sp)
            Spacer(modifier = Modifier.height(12.dp))
            Text(message, fontSize = 14.sp, color = Color(0xFF888888))
            if (onClick != null) {
                Spacer(modifier = Modifier.height(16.dp))
                FallahButton(
                    text = "إضافة جديدة",
                    onClick = onClick,
                    modifier = Modifier.width(150.dp)
                )
            }
        }
    }
}

fun Int.toLocaleString(): String = "%,d".format(this)
fun Double.toLocaleString(): String = "%,.0f".format(this)