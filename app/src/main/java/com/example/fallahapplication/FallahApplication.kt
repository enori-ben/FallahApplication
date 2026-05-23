package com.example.fallahapplication

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class FallahApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // تهيئة إضافية إذا لزم الأمر
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            println("Uncaught exception: ${throwable.message}")
            throwable.printStackTrace()
        }
    }
}