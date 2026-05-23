package com.example.fallahapplication

sealed class Screen(val route: String) {

    object Home : Screen("home")

    object Products : Screen("products/{categoryId}/{categoryName}") {
        fun createRoute(categoryId: Long, categoryName: String) =
            "products/$categoryId/${categoryName.replace(" ", "_")}"
    }

    object AllProducts : Screen("all_products")

    object ProductDetail : Screen("product/{productId}") {
        fun createRoute(productId: Long) = "product/$productId"
    }

    object AddProduct : Screen("add_product") {
        fun createRoute(categoryId: Long? = null) =
            if (categoryId != null) "add_product?categoryId=$categoryId" else "add_product"
    }

    // ✅ مسار تعديل المنتج
    object EditProduct : Screen("edit_product/{productId}") {
        fun createRoute(productId: Long) = "edit_product/$productId"
    }

    object Cart : Screen("cart")

    object Customers : Screen("customers")

    object CustomerDetail : Screen("customer/{customerId}") {
        fun createRoute(customerId: Long) = "customer/$customerId"
    }

    object AddCustomer : Screen("add_customer")

    object Debts     : Screen("debts")
    object Reports   : Screen("reports")
    object Settings  : Screen("settings")

    object ExpertAdvice    : Screen("expert_advice")
    object AdvancedReports : Screen("advanced_reports")
}