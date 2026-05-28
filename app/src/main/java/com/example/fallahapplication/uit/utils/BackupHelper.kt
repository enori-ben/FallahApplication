package com.example.fallahapplication.uit.utils

import android.content.Context
import android.widget.Toast
import com.example.fallahapplication.data.model.*
import com.example.fallahapplication.data.repository.FallahRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

class BackupHelper(
    private val context: Context,
    private val repository: FallahRepository
) {

    suspend fun exportData(): String? {
        return withContext(Dispatchers.IO) {
            try {
                val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
                val fileName = "fallah_backup_$timestamp.json"
                val file = File(context.getExternalFilesDir(null), fileName)

                val jsonObject = JSONObject()

                val categories = repository.getAllCategories().first()
                val categoriesArray = JSONArray()
                categories.forEach { category ->
                    val obj = JSONObject()
                    obj.put("name", category.name)
                    obj.put("icon", category.icon)
                    obj.put("color", category.color)
                    obj.put("description", category.description)
                    categoriesArray.put(obj)
                }
                jsonObject.put("categories", categoriesArray)

                val products = repository.getAllProducts().first()
                val productsArray = JSONArray()
                products.forEach { product ->
                    val obj = JSONObject()
                    obj.put("name", product.name)
                    obj.put("description", product.description)
                    obj.put("categoryId", product.categoryId)
                    obj.put("purchasePrice", product.purchasePrice)
                    obj.put("sellingPrice", product.sellingPrice)
                    obj.put("quantity", product.quantity)
                    obj.put("minQuantity", product.minQuantity)
                    obj.put("unit", product.unit)
                    productsArray.put(obj)
                }
                jsonObject.put("products", productsArray)

                val customers = repository.getAllCustomers().first()
                val customersArray = JSONArray()
                customers.forEach { customer ->
                    val obj = JSONObject()
                    obj.put("name", customer.name)
                    obj.put("phone", customer.phone)
                    obj.put("address", customer.address)
                    customersArray.put(obj)
                }
                jsonObject.put("customers", customersArray)

                FileOutputStream(file).use { outputStream ->
                    outputStream.write(jsonObject.toString().toByteArray())
                }

                return@withContext file.absolutePath
            } catch (e: Exception) {
                e.printStackTrace()
                return@withContext null
            }
        }
    }

    suspend fun importData(filePath: String): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val file = File(filePath)
                val jsonString = FileInputStream(file).bufferedReader().use { it.readText() }
                val jsonObject = JSONObject(jsonString)

                val categoriesArray = jsonObject.getJSONArray("categories")
                for (i in 0 until categoriesArray.length()) {
                    val obj = categoriesArray.getJSONObject(i)
                    val category = Category(
                        name = obj.getString("name"),
                        icon = obj.getString("icon"),
                        color = obj.getLong("color"),
                        description = obj.getString("description")
                    )
                    repository.insertCategory(category)
                }

                val productsArray = jsonObject.getJSONArray("products")
                for (i in 0 until productsArray.length()) {
                    val obj = productsArray.getJSONObject(i)
                    val product = Product(
                        name = obj.getString("name"),
                        description = obj.getString("description"),
                        categoryId = obj.getLong("categoryId"),
                        purchasePrice = obj.getDouble("purchasePrice"),
                        sellingPrice = obj.getDouble("sellingPrice"),
                        quantity = obj.getInt("quantity"),
                        minQuantity = obj.getInt("minQuantity"),
                        unit = obj.getString("unit")
                    )
                    repository.insertProduct(product)
                }

                val customersArray = jsonObject.getJSONArray("customers")
                for (i in 0 until customersArray.length()) {
                    val obj = customersArray.getJSONObject(i)
                    val customer = Customer(
                        name = obj.getString("name"),
                        phone = obj.getString("phone"),
                        address = obj.getString("address")
                    )
                    repository.insertCustomer(customer)
                }

                return@withContext true
            } catch (e: Exception) {
                e.printStackTrace()
                return@withContext false
            }
        }
    }
}