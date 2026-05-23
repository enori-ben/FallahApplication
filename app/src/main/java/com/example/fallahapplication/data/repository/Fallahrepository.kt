package com.example.fallahapplication.data.repository

import com.example.fallahapplication. data. local. *
import com.example.fallahapplication. data. model. *
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FallahRepository @Inject constructor(
    private val categoryDao: CategoryDao,
    private val productDao: ProductDao,
    private val customerDao: CustomerDao,
    private val saleDao: SaleDao,
    private val saleItemDao: SaleItemDao,
    private val debtDao: DebtDao,
    private val paymentDao: PaymentDao
) {
    // ===================== CATEGORIES =====================
    fun getAllCategories(): Flow<List<Category>> = categoryDao.getAllCategories()
    suspend fun insertCategory(category: Category) = categoryDao.insertCategory(category)
    suspend fun updateCategory(category: Category) = categoryDao.updateCategory(category)
    suspend fun deleteCategory(category: Category) = categoryDao.deleteCategory(category)

    // ===================== PRODUCTS =====================
    fun getAllProducts(): Flow<List<Product>> = productDao.getAllProducts()
    fun getProductsByCategory(categoryId: Long): Flow<List<Product>> = productDao.getProductsByCategory(categoryId)
    fun searchProducts(query: String): Flow<List<Product>> = productDao.searchProducts(query)
    fun getLowStockProducts(): Flow<List<Product>> = productDao.getLowStockProducts()
    suspend fun getProductById(id: Long): Product? = productDao.getProductById(id)
    suspend fun insertProduct(product: Product) = productDao.insertProduct(product)
    suspend fun updateProduct(product: Product) = productDao.updateProduct(product)
    suspend fun deleteProduct(product: Product) = productDao.deleteProduct(product)

    // ===================== CUSTOMERS =====================
    fun getAllCustomers(): Flow<List<Customer>> = customerDao.getAllCustomers()
    fun searchCustomers(query: String): Flow<List<Customer>> = customerDao.searchCustomers(query)
    fun getCustomersWithDebt(): Flow<List<Customer>> = customerDao.getCustomersWithDebt()
    suspend fun getCustomerById(id: Long): Customer? = customerDao.getCustomerById(id)
    suspend fun getCustomerByName(name: String): Customer? = customerDao.getCustomerByName(name)  // ✅ أضف هذا
    suspend fun insertCustomer(customer: Customer): Long = customerDao.insertCustomer(customer)
    suspend fun updateCustomer(customer: Customer) = customerDao.updateCustomer(customer)
    suspend fun deleteCustomer(customer: Customer) = customerDao.deleteCustomer(customer)


    // ===================== SALES =====================
    fun getAllSales(): Flow<List<Sale>> = saleDao.getAllSales()
    fun getTodaySales(): Flow<List<Sale>> = saleDao.getTodaySales()
    fun getSalesByCustomer(customerId: Long): Flow<List<Sale>> = saleDao.getSalesByCustomer(customerId)
    fun getTodayRevenue(): Flow<Double?> = saleDao.getTodayRevenue()
    fun getMonthRevenue(): Flow<Double?> = saleDao.getMonthRevenue()
    fun getTodayProfit(): Flow<Double?> = saleDao.getTodayProfit()
    fun getTodaySalesCount(): Flow<Int> = saleDao.getTodaySalesCount()
    fun getSaleItems(saleId: Long): Flow<List<SaleItem>> = saleItemDao.getSaleItems(saleId)
    fun getTopSellingProducts(): Flow<List<TopProduct>> = saleItemDao.getTopSellingProducts()

    suspend fun completeSale(
        cartItems: List<CartItem>,
        customerName: String,
        customerId: Long?,
        paymentType: PaymentType,
        paidAmount: Double,
        notes: String
    ): Long {
        val totalAmount = cartItems.sumOf { it.total }
        val lastId = saleDao.getLastInvoiceId() ?: 0
        val invoiceNumber = "INV-${(lastId + 1).toString().padStart(5, '0')}"

        val sale = Sale(
            invoiceNumber = invoiceNumber,
            customerId = customerId,
            customerName = customerName,
            totalAmount = totalAmount,
            paidAmount = if (paymentType == PaymentType.CASH) totalAmount
            else if (paymentType == PaymentType.PARTIAL) paidAmount
            else 0.0,
            paymentType = paymentType,
            notes = notes
        )
        val saleId = saleDao.insertSale(sale)

        // Insert sale items and update stock
        val items = cartItems.map { cartItem ->
            productDao.decreaseStock(cartItem.product.id, cartItem.quantity)
            SaleItem(
                saleId = saleId,
                productId = cartItem.product.id,
                productName = cartItem.product.name,
                quantity = cartItem.quantity,
                unitPrice = cartItem.unitPrice
            )
        }
        saleItemDao.insertSaleItems(items)

        // ✅ إصلاح: معالجة الدين بشكل صحيح
        if (customerId != null && paymentType != PaymentType.CASH) {
            // حساب مبلغ الدين
            val debtAmount = when (paymentType) {
                PaymentType.DEBT -> totalAmount
                PaymentType.PARTIAL -> totalAmount - paidAmount
                else -> 0.0
            }

            if (debtAmount > 0) {
                // إضافة سجل الدين
                debtDao.insertDebt(
                    Debt(
                        customerId = customerId,
                        saleId = saleId,
                        amount = debtAmount,
                        paidAmount = 0.0,
                        isPaid = false
                    )
                )
                // تحديث إجمالي الدين في جدول الزبائن
                customerDao.addDebt(customerId, debtAmount)
            }
        }

        // تحديث إحصائيات الزبون
        if (customerId != null) {
            customerDao.addPurchase(customerId, totalAmount, System.currentTimeMillis())
        }

        return saleId
    }

    // ===================== DEBTS =====================
    fun getDebtsByCustomer(customerId: Long): Flow<List<Debt>> = debtDao.getDebtsByCustomer(customerId)
    fun getTotalUnpaidDebt(): Flow<Double?> = debtDao.getTotalUnpaidDebt()
    fun getCustomerRemainingDebt(customerId: Long): Flow<Double?> = debtDao.getCustomerRemainingDebt(customerId)

    suspend fun recordPayment(customerId: Long, amount: Double, notes: String) {
        paymentDao.insertPayment(Payment(customerId = customerId, amount = amount, notes = notes))
        debtDao.applyPayment(customerId, amount)
        customerDao.payDebt(customerId, amount)
    }

    // ===================== PAYMENTS =====================
    fun getPaymentsByCustomer(customerId: Long): Flow<List<Payment>> = paymentDao.getPaymentsByCustomer(customerId)

    // ===================== REPORTS =====================
    suspend fun getSaleWithItems(saleId: Long): Pair<Sale?, List<SaleItem>> {
        val sale = saleDao.getSaleById(saleId)
        val items = saleItemDao.getSaleItemsSync(saleId)
        return Pair(sale, items)
    }

    suspend fun getSaleItemsSync(saleId: Long): List<SaleItem> =
        saleItemDao.getSaleItemsSync(saleId)

}
