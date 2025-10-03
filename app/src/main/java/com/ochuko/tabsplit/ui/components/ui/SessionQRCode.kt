package com.ochuko.tabsplit.ui.components.ui

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.*


@Composable
fun SessionQRCode(inviteUrl: String) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current

    // Get screen width for adaptive QR size
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    val qrSize = (screenWidth * 0.7f).coerceAtMost(300.dp) // max 300dp

    // Generate QR bitmap and cache it
    val qrBitmap by remember(inviteUrl) { mutableStateOf(generateQrCode(inviteUrl)) }

    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // QR Code
        qrBitmap?.let {
            Image(
                bitmap = it.asImageBitmap(),
                contentDescription = "Session QR Code",
                modifier = Modifier
                    .size(qrSize)
                    .padding(vertical = 16.dp)
            )
        }

        // Invite URL
        SelectionContainer {
            Text(
                text = inviteUrl,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(8.dp))
                    .padding(8.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Buttons Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Text(
                text = "Copy",
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier
                    .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(8.dp))
                    .padding(8.dp)
                    .clickable {
                        copyToClipboard(context, clipboardManager, inviteUrl)
                    }
            )

            Text(
                text = "Share",
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier
                    .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(8.dp))
                    .padding(8.dp)
                    .clickable {
                        shareInvite(context, inviteUrl)
                    }
            )
        }
    }
}

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

// Copy to clipboard
fun copyToClipboard(context: Context, clipboardManager: ClipboardManager, text: String) {
    clipboardManager.setPrimaryClip(ClipData.newPlainText("Invite Link", text))
    Toast.makeText(context, "Invite link copied!", Toast.LENGTH_SHORT).show()
}

// Overload to use Compose LocalClipboardManager
fun copyToClipboard(context: Context, clipboardManager: androidx.compose.ui.platform.ClipboardManager, text: String) {
    clipboardManager.setText(AnnotatedString(text))
    Toast.makeText(context, "Invite link copied!", Toast.LENGTH_SHORT).show()
}

// Share Invite via Intent
fun shareInvite(context: Context, text: String) {
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, "Join my TabSplit: $text")
    }
    context.startActivity(Intent.createChooser(intent, "Share via"))
}
