package com.example.fallahapplication.data.local

import androidx.room.*
import com.example.fallahapplication.data.model.Category
import com.example.fallahapplication.data.model.Customer
import com.example.fallahapplication.data.model.Debt
import com.example.fallahapplication.data.model.Payment
import com.example.fallahapplication.data.model.Product
import com.example.fallahapplication.data.model.Sale
import com.example.fallahapplication.data.model.SaleItem
import com.example.fallahapplication.data.model.*
import kotlinx.coroutines.flow.Flow

// ===================== CATEGORY DAO =====================
@Dao
interface CategoryDao {
    @Query("SELECT * FROM categories ORDER BY name ASC")
    fun getAllCategories(): Flow<List<Category>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategory(category: Category): Long

    @Update
    suspend fun updateCategory(category: Category)

    @Delete
    suspend fun deleteCategory(category: Category)
}

// ===================== PRODUCT DAO =====================
@Dao
interface ProductDao {
    @Query("SELECT * FROM products ORDER BY name ASC")
    fun getAllProducts(): Flow<List<Product>>

    @Query("SELECT * FROM products WHERE categoryId = :categoryId ORDER BY name ASC")
    fun getProductsByCategory(categoryId: Long): Flow<List<Product>>

    @Query("SELECT * FROM products WHERE name LIKE '%' || :query || '%' ORDER BY name ASC")
    fun searchProducts(query: String): Flow<List<Product>>

    @Query("SELECT * FROM products WHERE quantity <= minQuantity")
    fun getLowStockProducts(): Flow<List<Product>>

    @Query("SELECT * FROM products WHERE id = :id")
    suspend fun getProductById(id: Long): Product?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProduct(product: Product): Long

    @Update
    suspend fun updateProduct(product: Product)

    @Delete
    suspend fun deleteProduct(product: Product)

    @Query("UPDATE products SET quantity = quantity - :amount WHERE id = :productId")
    suspend fun decreaseStock(productId: Long, amount: Int)
}

// ===================== CUSTOMER DAO =====================
@Dao
interface CustomerDao {
    @Query("SELECT * FROM customers ORDER BY name ASC")
    fun getAllCustomers(): Flow<List<Customer>>

    @Query("SELECT * FROM customers WHERE name LIKE '%' || :query || '%' ORDER BY name ASC")
    fun searchCustomers(query: String): Flow<List<Customer>>

    @Query("SELECT * FROM customers WHERE totalDebt > 0 ORDER BY totalDebt DESC")
    fun getCustomersWithDebt(): Flow<List<Customer>>

    @Query("SELECT * FROM customers WHERE id = :id")
    suspend fun getCustomerById(id: Long): Customer?

    @Query("SELECT * FROM customers WHERE name = :name LIMIT 1")
    suspend fun getCustomerByName(name: String): Customer?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCustomer(customer: Customer): Long

    @Update
    suspend fun updateCustomer(customer: Customer)

    @Delete
    suspend fun deleteCustomer(customer: Customer)

    @Query("UPDATE customers SET totalDebt = totalDebt + :amount WHERE id = :customerId")
    suspend fun addDebt(customerId: Long, amount: Double)

    @Query("UPDATE customers SET totalDebt = totalDebt - :amount WHERE id = :customerId")
    suspend fun payDebt(customerId: Long, amount: Double)

    @Query("UPDATE customers SET totalPurchases = totalPurchases + :amount, lastPurchaseDate = :date WHERE id = :customerId")
    suspend fun addPurchase(customerId: Long, amount: Double, date: Long)
}

// ===================== SALE DAO =====================
@Dao
interface SaleDao {
    @Query("SELECT * FROM sales ORDER BY createdAt DESC")
    fun getAllSales(): Flow<List<Sale>>

    @Query("SELECT * FROM sales WHERE DATE(createdAt/1000, 'unixepoch') = DATE('now') ORDER BY createdAt DESC")
    fun getTodaySales(): Flow<List<Sale>>

    @Query("SELECT * FROM sales WHERE customerId = :customerId ORDER BY createdAt DESC")
    fun getSalesByCustomer(customerId: Long): Flow<List<Sale>>

    @Query("SELECT SUM(totalAmount) FROM sales WHERE DATE(createdAt/1000, 'unixepoch') = DATE('now')")
    fun getTodayRevenue(): Flow<Double?>

    @Query("SELECT SUM(totalAmount) FROM sales WHERE strftime('%Y-%m', createdAt/1000, 'unixepoch') = strftime('%Y-%m', 'now')")
    fun getMonthRevenue(): Flow<Double?>

    @Query("""
        SELECT SUM((si.unitPrice - p.purchasePrice) * si.quantity) 
        FROM sale_items si 
        JOIN products p ON si.productId = p.id
        JOIN sales s ON si.saleId = s.id
        WHERE DATE(s.createdAt/1000, 'unixepoch') = DATE('now')
    """)
    fun getTodayProfit(): Flow<Double?>

    @Query("SELECT COUNT(*) FROM sales WHERE DATE(createdAt/1000, 'unixepoch') = DATE('now')")
    fun getTodaySalesCount(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSale(sale: Sale): Long

    @Query("SELECT * FROM sales WHERE id = :id")
    suspend fun getSaleById(id: Long): Sale?

    @Query("SELECT MAX(id) FROM sales")
    suspend fun getLastInvoiceId(): Long?
}

// ===================== SALE ITEM DAO =====================
@Dao
interface SaleItemDao {
    @Query("SELECT * FROM sale_items WHERE saleId = :saleId")
    fun getSaleItems(saleId: Long): Flow<List<SaleItem>>

    @Query("SELECT * FROM sale_items WHERE saleId = :saleId")
    suspend fun getSaleItemsSync(saleId: Long): List<SaleItem>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSaleItems(items: List<SaleItem>)

    @Query("""
        SELECT si.productId, si.productName, SUM(si.quantity) as totalQty, SUM(si.totalPrice) as totalRev
        FROM sale_items si
        GROUP BY si.productId
        ORDER BY totalQty DESC
        LIMIT 5
    """)
    fun getTopSellingProducts(): Flow<List<TopProduct>>
}

data class TopProduct(
    val productId: Long,
    val productName: String,
    val totalQty: Int,
    val totalRev: Double
)

// ===================== DEBT DAO =====================
@Dao
interface DebtDao {
    @Query("SELECT * FROM debts WHERE customerId = :customerId ORDER BY createdAt DESC")
    fun getDebtsByCustomer(customerId: Long): Flow<List<Debt>>

    @Query("SELECT SUM(amount - paidAmount) FROM debts WHERE isPaid = 0")
    fun getTotalUnpaidDebt(): Flow<Double?>

    @Query("SELECT SUM(amount - paidAmount) FROM debts WHERE customerId = :customerId AND isPaid = 0")
    fun getCustomerRemainingDebt(customerId: Long): Flow<Double?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDebt(debt: Debt): Long

    @Update
    suspend fun updateDebt(debt: Debt)

    @Query("UPDATE debts SET paidAmount = paidAmount + :amount, isPaid = (paidAmount + :amount >= amount) WHERE customerId = :customerId AND isPaid = 0")
    suspend fun applyPayment(customerId: Long, amount: Double)
}

// ===================== PAYMENT DAO =====================
@Dao
interface PaymentDao {
    @Query("SELECT * FROM payments WHERE customerId = :customerId ORDER BY createdAt DESC")
    fun getPaymentsByCustomer(customerId: Long): Flow<List<Payment>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPayment(payment: Payment): Long
}