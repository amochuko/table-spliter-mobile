package com.partum.tabsplit.utils

import android.graphics.Bitmap
import android.graphics.Color
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter

// Generate QR Bitmap
fun generateQrCode(text: String): Bitmap? = try {
    val writer = QRCodeWriter()
    val bitMatrix = writer.encode(text, BarcodeFormat.QR_CODE, 512, 512)
    val bmp = Bitmap.createBitmap(bitMatrix.width, bitMatrix.height, Bitmap.Config.RGB_565)

    for (x in 0 until bitMatrix.width) {
        for (y in 0 until bitMatrix.height) {
            bmp.setPixel(x, y, if (bitMatrix[x, y]) Color.BLACK else Color.WHITE)
        }
    }
    bmp
} catch (e: Exception) {
    null
}
