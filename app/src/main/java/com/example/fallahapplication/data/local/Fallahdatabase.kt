package com.example.fallahapplication.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.fallahapplication.data.model.*

@Database(
    entities = [
        Category::class,
        Product::class,
        Customer::class,
        Sale::class,
        SaleItem::class,
        Debt::class,
        Payment::class
    ],
    version = 1,
    exportSchema = false
)
abstract class FallahDatabase : RoomDatabase() {
    abstract fun categoryDao(): CategoryDao
    abstract fun productDao(): ProductDao
    abstract fun customerDao(): CustomerDao
    abstract fun saleDao(): SaleDao
    abstract fun saleItemDao(): SaleItemDao
    abstract fun debtDao(): DebtDao
    abstract fun paymentDao(): PaymentDao
}