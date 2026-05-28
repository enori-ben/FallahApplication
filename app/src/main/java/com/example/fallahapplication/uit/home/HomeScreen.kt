package com.example.fallahapplication.uit.home

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.fallahapplication.R
import com.example.fallahapplication.uit.components.*
import com.example.fallahapplication.ui.theme.*
import com.example.fallahapplication.viewmodel.HomeViewModel
import kotlinx.coroutines.delay
import androidx.compose.animation.core.*
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel = hiltViewModel()
) {
    LaunchedEffect(Unit) {
        viewModel.cleanupDuplicateCategories()
    }

    val categories by viewModel.categories.collectAsState()
    val todayRevenue by viewModel.todayRevenue.collectAsState()
    val todayProfit by viewModel.todayProfit.collectAsState()
    val salesCount by viewModel.todaySalesCount.collectAsState()
    val totalDebt by viewModel.totalUnpaidDebt.collectAsState()
    val lowStock by viewModel.lowStockProducts.collectAsState()

    val uniqueCategories = remember(categories) {
        categories.distinctBy { it.name }
    }

    var isVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        delay(100)
        isVisible = true
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            stringResource(R.string.fallah_app),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            stringResource(R.string.subtitle),
                            fontSize = 11.sp,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { navController.navigate("all_products") }) {
                        Icon(Icons.Outlined.Store, contentDescription = stringResource(R.string.products), tint = Color.White)
                    }
                    IconButton(onClick = { navController.navigate("reports") }) {
                        Icon(Icons.Outlined.BarChart, contentDescription = stringResource(R.string.reports), tint = Color.White)
                    }
                    IconButton(onClick = { navController.navigate("settings") }) {
                        Icon(Icons.Outlined.Settings, contentDescription = stringResource(R.string.settings), tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = FallahGreen
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .background(
                    Brush.verticalGradient(
                        colors = listOf(FallahBg, Color.White)
                    )
                )
        ) {
            // Greeting Section
            AnimatedContent(
                targetState = isVisible,
                transitionSpec = {
                    fadeIn(animationSpec = tween(500)) togetherWith
                            fadeOut(animationSpec = tween(500))
                }
            ) { visible ->
                if (visible) {
                    GreetingSection()
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Stats Grid
            StatsGrid(
                todayRevenue = todayRevenue ?: 0.0,
                todayProfit = todayProfit ?: 0.0,
                totalDebt = totalDebt ?: 0.0,
                salesCount = salesCount,
                onRevenueClick = { navController.navigate("reports") },
                onProfitClick = { navController.navigate("reports") },
                onDebtClick = { navController.navigate("debts") },
                onCustomersClick = { navController.navigate("customers") }
            )

            // Low Stock Warning
            if (lowStock.isNotEmpty()) {
                AnimatedVisibility(
                    visible = true,
                    enter = slideInHorizontally() + fadeIn(),
                    exit = slideOutHorizontally() + fadeOut()
                ) {
                    LowStockWarningCard(
                        count = lowStock.size,
                        onClick = { navController.navigate("all_products") }
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Categories Section
            SectionHeader(
                title = stringResource(R.string.categories),
                icon = "📁"
            )

            // Categories Grid
            if (uniqueCategories.isNotEmpty()) {
                val filteredCategories = uniqueCategories.filter {
                    it.name != "إرشادات الخبير" && it.name != "expert_advice"
                }

                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp)
                        .heightIn(max = 1100.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                    userScrollEnabled = false
                ) {

                    items(filteredCategories) { category ->
                        CategoryCardModern(
                            icon = category.icon,
                            name = category.name,
                            count = stringResource(R.string.products_count),
                            color = Color(category.color),
                            onClick = {
                                navController.navigate("products/${category.id}/${category.name.replace(" ", "_")}")
                            }
                        )
                    }

                    // الزبائن
                    item {
                        CategoryCardModern(
                            icon = "👥",
                            name = stringResource(R.string.customers),
                            count = stringResource(R.string.management),
                            color = Color(0xFF2196F3),
                            onClick = { navController.navigate("customers") }
                        )
                    }

                    item {
                        CategoryCardModern(
                            icon = "👨‍🌾",
                            name = stringResource(R.string.expert_advice),
                            count = stringResource(R.string.expert_tips),
                            color = Color(0xFF9C27B0),
                            onClick = { navController.navigate("expert_advice") }
                        )
                    }

                    item {
                        CategoryCardModern(
                            icon = "📊",
                            name = stringResource(R.string.reports),
                            count = stringResource(R.string.and_profits),
                            color = Color(0xFF7C4DFF),
                            onClick = { navController.navigate("reports") }
                        )
                    }
                }
            } else {
                LoadingIndicator()
            }

            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

@Composable
fun GreetingSection() {
    val currentHour = remember { java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY) }
    val greeting = when (currentHour) {
        in 0..11 -> stringResource(R.string.morning)
        in 12..16 -> stringResource(R.string.afternoon)
        else -> stringResource(R.string.evening)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFE8F5E9)
        ),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    greeting,
                    fontSize = 14.sp,
                    color = FallahGreen,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    stringResource(R.string.welcome_message),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2C3E50)
                )
                Text(
                    stringResource(R.string.welcome_subtitle),
                    fontSize = 12.sp,
                    color = Color(0xFF666666)
                )
            }
            Surface(
                shape = CircleShape,
                color = FallahGreen.copy(alpha = 0.15f),
                modifier = Modifier.size(50.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text("🌿", fontSize = 28.sp)
                }
            }
        }
    }
}

@Composable
fun StatsGrid(
    todayRevenue: Double,
    todayProfit: Double,
    totalDebt: Double,
    salesCount: Int,
    onRevenueClick: () -> Unit,
    onProfitClick: () -> Unit,
    onDebtClick: () -> Unit,
    onCustomersClick: () -> Unit
) {
    Column(
        modifier = Modifier.padding(horizontal = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ModernStatCard(
                icon = "💰",
                value = formatAmount(todayRevenue),
                label = stringResource(R.string.sales_today),
                onClick = onRevenueClick,
                iconColor = FallahGreen,
                modifier = Modifier.weight(1f)
            )
            ModernStatCard(
                icon = "📊",
                value = formatAmount(todayProfit),
                label = stringResource(R.string.profits),
                onClick = onProfitClick,
                iconColor = Color(0xFFF57C00),
                modifier = Modifier.weight(1f)
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ModernStatCard(
                icon = "💳",
                value = formatAmount(totalDebt),
                label = stringResource(R.string.debts),
                onClick = onDebtClick,
                iconColor = FallahRed,
                modifier = Modifier.weight(1f)
            )
            ModernStatCard(
                icon = "👥",
                value = salesCount.toString(),
                label = stringResource(R.string.customers),
                onClick = onCustomersClick,
                iconColor = Color(0xFF1976D2),
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun ModernStatCard(
    icon: String,
    value: String,
    label: String,
    onClick: () -> Unit,
    iconColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .clickable(onClick = onClick)
            .shadow(4.dp, RoundedCornerShape(14.dp), spotColor = Color(0xFF000000).copy(alpha = 0.05f)),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = iconColor.copy(alpha = 0.1f),
                modifier = Modifier.size(48.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(icon, fontSize = 24.sp)
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    value,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2C3E50)
                )
                Text(
                    label,
                    fontSize = 11.sp,
                    color = Color(0xFF888888)
                )
            }
        }
    }
}

@Composable
fun LowStockWarningCard(count: Int, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3E0)),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = CircleShape,
                color = FallahAmber.copy(alpha = 0.15f),
                modifier = Modifier.size(40.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        Icons.Default.Warning,
                        contentDescription = stringResource(R.string.warning),
                        tint = FallahAmber,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    stringResource(R.string.low_stock_warning),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFFE65100)
                )
                Text(
                    "$count ${stringResource(R.string.low_stock_items)}",
                    fontSize = 11.sp,
                    color = Color(0xFFBF360C)
                )
            }
            Icon(
                Icons.Outlined.ChevronRight,
                contentDescription = null,
                tint = FallahAmber,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
fun SectionHeader(title: String, icon: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(icon, fontSize = 20.sp)
            Text(
                title,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2C3E50)
            )
        }
    }
}

@Composable
fun CategoryCardModern(
    icon: String,
    name: String,
    count: String,
    color: Color,
    onClick: () -> Unit
) {
    var isPressed by remember { mutableStateOf(false) }
    val interactionSource = remember { MutableInteractionSource() }

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.98f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "scale"
    )


    LaunchedEffect(interactionSource) {
        interactionSource.interactions.collect { interaction ->
            isPressed = when (interaction) {
                is PressInteraction.Press -> true
                is PressInteraction.Release -> false
                is PressInteraction.Cancel -> false
                else -> false
            }
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .clickable(
                onClick = onClick,
                indication = ripple(color = color, radius = 80.dp),
                interactionSource = interactionSource
            )
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(16.dp),
                clip = false
            ),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(color.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Text(icon, fontSize = 30.sp)
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                name,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF1A1A1A),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                count,
                fontSize = 11.sp,
                color = color,
                fontWeight = FontWeight.Normal
            )
        }
    }
}

fun formatAmount(value: Double?): String {
    val v = value ?: 0.0
    return when {
        v >= 1000000 -> "${(v / 1000000).toInt()}M"
        v >= 1000 -> "${(v / 1000).toInt()}K"
        else -> v.toInt().toString()
    }
}