package com.example.fallahapplication.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.Date

// ===================== CATEGORY =====================
@Entity(tableName = "categories")
data class Category(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val icon: String,
    val color: Long, // ARGB color stored as Long
    val description: String = ""
)

// ===================== PRODUCT =====================
@Entity(
    tableName = "products",
    foreignKeys = [ForeignKey(
        entity = Category::class,
        parentColumns = ["id"],
        childColumns = ["categoryId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("categoryId")]
)
data class Product(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val description: String = "",
    val categoryId: Long,
    val purchasePrice: Double,
    val sellingPrice: Double,
    val quantity: Int,
    val minQuantity: Int = 5, // تنبيه نقص المخزون
    val unit: String = "وحدة",
    val imagePath: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
) {
    val profit: Double get() = sellingPrice - purchasePrice
    val profitPercent: Double get() = if (purchasePrice > 0) (profit / purchasePrice) * 100 else 0.0
    val isLowStock: Boolean get() = quantity <= minQuantity
}

// ===================== CUSTOMER =====================
@Entity(tableName = "customers")
data class Customer(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val phone: String = "",
    val address: String = "",
    val totalDebt: Double = 0.0,
    val totalPurchases: Double = 0.0,
    val lastPurchaseDate: Long? = null,
    val createdAt: Long = System.currentTimeMillis()
)

// ===================== SALE =====================
@Entity(
    tableName = "sales",
    foreignKeys = [ForeignKey(
        entity = Customer::class,
        parentColumns = ["id"],
        childColumns = ["customerId"],
        onDelete = ForeignKey.SET_NULL
    )],
    indices = [Index("customerId")]
)
data class Sale(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val invoiceNumber: String,
    val customerId: Long? = null,
    val customerName: String = "",
    val totalAmount: Double,
    val paidAmount: Double,
    val paymentType: PaymentType,
    val notes: String = "",
    val createdAt: Long = System.currentTimeMillis()
) {
    val remainingAmount: Double get() = totalAmount - paidAmount
}

// ===================== SALE ITEM =====================
@Entity(
    tableName = "sale_items",
    foreignKeys = [
        ForeignKey(
            entity = Sale::class,
            parentColumns = ["id"],
            childColumns = ["saleId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Product::class,
            parentColumns = ["id"],
            childColumns = ["productId"],
            onDelete = ForeignKey.RESTRICT
        )
    ],
    indices = [Index("saleId"), Index("productId")]
)
data class SaleItem(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val saleId: Long,
    val productId: Long,
    val productName: String,
    val quantity: Int,
    val unitPrice: Double,
    val totalPrice: Double = quantity * unitPrice
)

// ===================== DEBT =====================
@Entity(
    tableName = "debts",
    foreignKeys = [
        ForeignKey(
            entity = Customer::class,
            parentColumns = ["id"],
            childColumns = ["customerId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Sale::class,
            parentColumns = ["id"],
            childColumns = ["saleId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("customerId"), Index("saleId")]
)
data class Debt(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val customerId: Long,
    val saleId: Long,
    val amount: Double,
    val paidAmount: Double = 0.0,
    val isPaid: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val paidAt: Long? = null
) {
    val remainingAmount: Double get() = amount - paidAmount
}

// ===================== PAYMENT =====================
@Entity(
    tableName = "payments",
    foreignKeys = [ForeignKey(
        entity = Customer::class,
        parentColumns = ["id"],
        childColumns = ["customerId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("customerId")]
)
data class Payment(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val customerId: Long,
    val amount: Double,
    val notes: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

// ===================== ENUMS =====================
enum class PaymentType(val label: String) {
    CASH("نقدًا"),
    DEBT("دين"),
    PARTIAL("دفع جزئي")
}

// ===================== CART ITEM (UI Model) =====================
data class CartItem(
    val product: Product,
    val quantity: Int,
    val unitPrice: Double = product.sellingPrice
) {
    val total: Double get() = quantity * unitPrice
}




