package com.example.fallahapplication.di

import android.content.Context
import androidx.room.Room
import com.example.fallahapplication. data. local. *
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): FallahDatabase {
        return Room.databaseBuilder(
            context,
            FallahDatabase::class.java,
            "fallah_database"
        ).build()
    }

    @Provides fun provideCategoryDao(db: FallahDatabase) = db.categoryDao()
    @Provides fun provideProductDao(db: FallahDatabase) = db.productDao()
    @Provides fun provideCustomerDao(db: FallahDatabase) = db.customerDao()
    @Provides fun provideSaleDao(db: FallahDatabase) = db.saleDao()
    @Provides fun provideSaleItemDao(db: FallahDatabase) = db.saleItemDao()
    @Provides fun provideDebtDao(db: FallahDatabase) = db.debtDao()
    @Provides fun providePaymentDao(db: FallahDatabase) = db.paymentDao()
}
