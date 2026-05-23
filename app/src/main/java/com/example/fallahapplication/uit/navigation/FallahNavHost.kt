package com.example.fallahapplication.uit.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.example.fallahapplication.Screen
import com.example.fallahapplication.uit.advice.ExpertAdviceScreen
import com.example.fallahapplication.uit.cart.CartScreen
import com.example.fallahapplication.uit.customers.AddCustomerScreen
import com.example.fallahapplication.uit.customers.CustomerDetailScreen
import com.example.fallahapplication.uit.customers.CustomersScreen
import com.example.fallahapplication.uit.debts.DebtsScreen
import com.example.fallahapplication.uit.home.HomeScreen
import com.example.fallahapplication.uit.products.*
import com.example.fallahapplication.uit.reports.AdvancedReportsScreen
import com.example.fallahapplication.uit.reports.ReportsScreen
import com.example.fallahapplication.uit.settings.SettingsScreen
import com.example.fallahapplication.viewmodel.CartViewModel

private val FallahGreen = Color(0xFF2D6A2D)

sealed class BottomNavItem(val screen: Screen, val icon: ImageVector, val label: String) {
    object Home      : BottomNavItem(Screen.Home,        Icons.Outlined.Home,           "الرئيسية")
    object Products  : BottomNavItem(Screen.AllProducts, Icons.Outlined.Store,          "المنتجات")
    object Cart      : BottomNavItem(Screen.Cart,        Icons.Outlined.ShoppingCart,   "السلة")
    object Debts     : BottomNavItem(Screen.Debts,       Icons.Outlined.AccountBalance, "الديون")
    object Customers : BottomNavItem(Screen.Customers,   Icons.Outlined.People,         "الزبائن")
}

@Composable
fun FallahNavHost() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val cartViewModel: CartViewModel = hiltViewModel()
    val cartState by cartViewModel.uiState.collectAsState()

    val bottomNavScreens = listOf(
        BottomNavItem.Home,
        BottomNavItem.Products,
        BottomNavItem.Cart,
        BottomNavItem.Debts,
        BottomNavItem.Customers,
    )

    val showBottomBar = currentDestination?.route?.let { route ->
        bottomNavScreens.any { it.screen.route == route } || route == Screen.Home.route
    } ?: true

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar(containerColor = MaterialTheme.colorScheme.surface, tonalElevation = 0.dp) {
                    bottomNavScreens.forEach { item ->
                        val selected = currentDestination?.hierarchy?.any { it.route == item.screen.route } == true
                        NavigationBarItem(
                            selected = selected,
                            onClick = {
                                navController.navigate(item.screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = {
                                if (item is BottomNavItem.Cart && cartState.itemCount > 0) {
                                    BadgedBox(badge = {
                                        Badge(containerColor = Color(0xFFE53935)) {
                                            Text("${cartState.itemCount}", color = Color.White)
                                        }
                                    }) { Icon(item.icon, item.label) }
                                } else {
                                    Icon(item.icon, item.label)
                                }
                            },
                            label = { Text(item.label, style = MaterialTheme.typography.labelSmall) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor   = FallahGreen,
                                selectedTextColor   = FallahGreen,
                                indicatorColor      = Color(0xFFE8F5E8),
                                unselectedIconColor = Color(0xFF888888),
                                unselectedTextColor = Color(0xFF888888)
                            )
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController    = navController,
            startDestination = "home",
            modifier         = Modifier.padding(innerPadding)
        ) {

            // ── الرئيسية ──
            composable("home") {
                HomeScreen(navController = navController)
            }

            // ── السلة ──
            composable("cart") {
                CartScreen(
                    onBack = { navController.popBackStack() },
                    onSaleComplete = {
                        navController.navigate("home") { popUpTo("home") { inclusive = true } }
                    },
                    viewModel = cartViewModel
                )
            }

            // ── جميع المنتجات ──
            composable(Screen.AllProducts.route) {
                AllProductsScreen(
                    onBack = { navController.popBackStack() },
                    onProductClick = { productId ->
                        navController.navigate(Screen.ProductDetail.createRoute(productId))
                    }
                )
            }

            // ── منتجات القسم (مرة واحدة فقط) ──
            composable(
                route = "products/{categoryId}/{categoryName}",
                arguments = listOf(
                    navArgument("categoryId")   { type = NavType.LongType },
                    navArgument("categoryName") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                val catId   = backStackEntry.arguments?.getLong("categoryId") ?: 0L
                val catName = backStackEntry.arguments?.getString("categoryName")?.replace("_", " ") ?: ""
                ProductsScreen(
                    categoryId    = catId,
                    categoryName  = catName,
                    onBack        = { navController.popBackStack() },
                    onAddProduct  = {
                        navController.navigate(Screen.AddProduct.createRoute(catId))
                    },
                    onGoToCart    = { navController.navigate("cart") },
                    cartViewModel = cartViewModel,
                    navController = navController
                )
            }

            // ── تفاصيل المنتج ──
            composable(
                route = "product/{productId}",
                arguments = listOf(navArgument("productId") { type = NavType.LongType })
            ) { backStackEntry ->
                val productId = backStackEntry.arguments?.getLong("productId") ?: 0L
                ProductDetailScreen(
                    productId = productId,
                    onBack = { navController.popBackStack() },
                    onEdit = { id -> navController.navigate(Screen.EditProduct.createRoute(id)) },
                    onProductDeleted = { navController.popBackStack() }  // تغيير من onDeleted إلى onProductDeleted
                )
            }

            // ── إضافة منتج ──
            composable(
                route = "add_product",
                arguments = listOf(
                    navArgument("categoryId") { type = NavType.StringType; nullable = true; defaultValue = null }
                )
            ) { backStackEntry ->
                val catId = backStackEntry.arguments?.getString("categoryId")?.toLongOrNull()
                AddProductScreen(
                    preselectedCategoryId = catId,
                    onBack = { navController.popBackStack() },
                    onSaved = { navController.popBackStack() }
                )
            }

            // ── تعديل منتج ──
            composable(
                route = "edit_product/{productId}",
                arguments = listOf(navArgument("productId") { type = NavType.LongType })
            ) { backStackEntry ->
                val productId = backStackEntry.arguments?.getLong("productId") ?: 0L
                EditProductScreen(
                    productId = productId,
                    onBack = { navController.popBackStack() },
                    onSaved = { navController.popBackStack() }
                )
            }

            // ── الزبائن ──
            composable(Screen.Customers.route) {
                CustomersScreen(
                    onCustomerClick = { id -> navController.navigate(Screen.CustomerDetail.createRoute(id)) },
                    onAddCustomer = { navController.navigate(Screen.AddCustomer.route) },
                    onBack = { navController.popBackStack() }
                )
            }

            // ── تفاصيل زبون ──
            composable(
                route = "customer/{customerId}",
                arguments = listOf(navArgument("customerId") { type = NavType.LongType })
            ) { backStackEntry ->
                val customerId = backStackEntry.arguments?.getLong("customerId") ?: 0L
                CustomerDetailScreen(
                    customerId = customerId,
                    onBack = { navController.popBackStack() }
                )
            }

            // ── إضافة زبون ──
            composable(Screen.AddCustomer.route) {
                AddCustomerScreen(
                    onBack = { navController.popBackStack() },
                    onSaved = { navController.popBackStack() }
                )
            }

            // ── الديون ──
            composable(Screen.Debts.route) {
                DebtsScreen(
                    onCustomerClick = { id -> navController.navigate(Screen.CustomerDetail.createRoute(id)) },
                    onBack = { navController.popBackStack() }
                )
            }

            // ── التقارير ──
            composable(Screen.Reports.route) {
                ReportsScreen(
                    navController = navController,
                    onBack = { navController.popBackStack() }
                )
            }

            // ── التقارير المتقدمة ──
            composable(Screen.AdvancedReports.route) {
                AdvancedReportsScreen(onBack = { navController.popBackStack() })
            }

            // ── الإعدادات ──
            composable(Screen.Settings.route) {
                SettingsScreen(onBack = { navController.popBackStack() })
            }

            // ── إرشادات الخبير ──
            composable(Screen.ExpertAdvice.route) {
                ExpertAdviceScreen(onBack = { navController.popBackStack() })
            }
        }
    }
}