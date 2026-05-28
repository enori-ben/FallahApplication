package com.example.fallahapplication.uit.utils

import android.content.Context
import android.content.Intent
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.os.Environment
import androidx.core.content.FileProvider
import com.example.fallahapplication.data.model.CartItem
import com.example.fallahapplication.data.model.PaymentType
import com.example.fallahapplication.uit.components.toLocaleString
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

class PrintHelper(private val context: Context) {

    fun printInvoice(
        invoiceNumber: String,
        customerName: String,
        customerPhone: String,
        items: List<CartItem>,
        totalAmount: Double,
        paidAmount: Double,
        paymentType: PaymentType,
        date: Long
    ) {
        try {
            val pdfDocument = PdfDocument()
            val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create() // A4 size
            val page = pdfDocument.startPage(pageInfo)
            val canvas = page.canvas
            val paint = Paint()
            var y = 50f

            paint.textSize = 18f
            paint.isFakeBoldText = true
            canvas.drawText("🌿 محل الأمل للأدوية الفلاحية", 50f, y, paint)
            y += 30f

            paint.textSize = 12f
            paint.isFakeBoldText = false
            canvas.drawText("الهاتف: 05XX XX XX XX", 50f, y, paint)
            y += 20f
            canvas.drawText("البريد: contact@alah.com", 50f, y, paint)
            y += 30f

            paint.strokeWidth = 2f
            canvas.drawLine(50f, y, 545f, y, paint)
            y += 20f

            paint.textSize = 14f
            paint.isFakeBoldText = true
            canvas.drawText("فاتورة رقم: $invoiceNumber", 50f, y, paint)
            y += 25f

            paint.textSize = 12f
            paint.isFakeBoldText = false
            val dateStr = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date(date))
            canvas.drawText("التاريخ: $dateStr", 50f, y, paint)
            y += 25f
            canvas.drawText("الزبون: $customerName", 50f, y, paint)
            y += 20f
            if (customerPhone.isNotBlank()) {
                canvas.drawText("الهاتف: $customerPhone", 50f, y, paint)
                y += 20f
            }
            y += 10f

            canvas.drawLine(50f, y, 545f, y, paint)
            y += 20f

            paint.textSize = 12f
            paint.isFakeBoldText = true
            canvas.drawText("المنتج", 50f, y, paint)
            canvas.drawText("الكمية", 350f, y, paint)
            canvas.drawText("السعر", 450f, y, paint)
            y += 20f
            canvas.drawLine(50f, y, 545f, y, paint)
            y += 15f

            paint.isFakeBoldText = false
            items.forEach { cartItem ->
                val productName = cartItem.product.name
                val quantity = cartItem.quantity
                val total = cartItem.total

                canvas.drawText(productName.take(30), 50f, y, paint)
                canvas.drawText("×$quantity", 360f, y, paint)
                canvas.drawText("${total.toInt().toLocaleString()} دج", 440f, y, paint)
                y += 20f
                if (y > 750) {
                    pdfDocument.finishPage(page)
                    val newPage = pdfDocument.startPage(pageInfo)
                    canvas.drawText("...", 50f, 50f, paint)
                    y = 70f
                }
            }

            y += 10f
            canvas.drawLine(50f, y, 545f, y, paint)
            y += 20f

            paint.textSize = 14f
            paint.isFakeBoldText = true
            canvas.drawText("الإجمالي: ${totalAmount.toInt().toLocaleString()} دج", 350f, y, paint)
            y += 25f

            when (paymentType) {
                PaymentType.CASH -> canvas.drawText("طريقة الدفع: نقداً ✓", 350f, y, paint)
                PaymentType.DEBT -> {
                    canvas.drawText("طريقة الدفع: دين", 350f, y, paint)
                    y += 20f
                    canvas.drawText("المتبقي: ${totalAmount.toInt().toLocaleString()} دج", 350f, y, paint)
                }
                PaymentType.PARTIAL -> {
                    canvas.drawText("طريقة الدفع: دفع جزئي", 350f, y, paint)
                    y += 20f
                    canvas.drawText("المدفوع: ${paidAmount.toInt().toLocaleString()} دج", 350f, y, paint)
                    y += 20f
                    canvas.drawText("المتبقي: ${(totalAmount - paidAmount).toInt().toLocaleString()} دج", 350f, y, paint)
                }
            }
            y += 30f

            paint.textSize = 11f
            paint.isFakeBoldText = false
            canvas.drawText("شكراً لثقتكم 🌿 نتمنى لكم موسمًا مباركًا", 150f, y, paint)

            pdfDocument.finishPage(page)

            val fileName = "invoice_$invoiceNumber.pdf"
            val documentsDir = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
            if (documentsDir != null && !documentsDir.exists()) {
                documentsDir.mkdirs()
            }
            val file = File(documentsDir, fileName)
            pdfDocument.writeTo(FileOutputStream(file))
            pdfDocument.close()

            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )

            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(uri, "application/pdf")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            context.startActivity(Intent.createChooser(intent, "فتح الفاتورة"))

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}