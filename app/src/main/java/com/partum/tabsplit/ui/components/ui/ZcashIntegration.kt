package com.ochuko.tabsplit.ui.components.ui

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.core.content.ContextCompat.startActivity
import androidx.core.content.FileProvider
import androidx.core.content.ContextCompat
import androidx.core.content.pm.ShortcutManagerCompat
import androidx.core.app.ShareCompat
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import com.ochuko.tabsplit.utils.createZcashUri
import android.content.ClipData
import android.content.ClipboardManager

@Composable
fun ZcashIntegration(
    visible: Boolean,
    onClose: () -> Unit,
    sessionId: String,
    balances: Map<String, Double>,
    recipientAddress: String
) {
    if (!visible) return

    val context = LocalContext.current
    val clipboard = context.getSystemService(ClipboardManager::class.java)

    // compute amount owed
    val totalAmount = balances.values.filter { it > 0 }.sum()

    Dialog(onDismissRequest = onClose) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = MaterialTheme.shapes.medium,
            tonalElevation = 4.dp
        ) {
            when {
                recipientAddress.isEmpty() -> {
                    Column(
                        modifier = Modifier
                            .padding(24.dp)
                            .fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("Waiting for recipient address...")
                        Spacer(Modifier.height(16.dp))
                        Button(onClick = onClose) { Text("Close") }
                    }
                }

                totalAmount <= 0 -> {
                    Column(
                        modifier = Modifier
                            .padding(24.dp)
                            .fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            "Nothing to settle",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Spacer(Modifier.height(16.dp))
                        Button(onClick = onClose) { Text("Close") }
                    }
                }

                else -> {
                    val paymentUri = createZcashUri(
                        recipientAddress,
                        totalAmount,
                        "Session $sessionId settlement"
                    )

                    Column(
                        modifier = Modifier
                            .padding(24.dp)
                            .fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            "Settle with ZEC",
                            style = MaterialTheme.typography.titleLarge
                        )

                        Spacer(Modifier.height(16.dp))

                        // Generate QR bitmap
                        val qrBitmap = remember(paymentUri) {
                            val size = 512
                            val writer = QRCodeWriter()
                            val bitMatrix = writer.encode(paymentUri, BarcodeFormat.QR_CODE, size, size)
                            val bmp = android.graphics.Bitmap.createBitmap(size, size, android.graphics.Bitmap.Config.RGB_565)
                            for (x in 0 until size) {
                                for (y in 0 until size) {
                                    bmp.setPixel(x, y, if (bitMatrix[x, y]) android.graphics.Color.BLACK else android.graphics.Color.WHITE)
                                }
                            }
                            bmp.asImageBitmap()
                        }

                        Image(
                            bitmap = qrBitmap,
                            contentDescription = "Zcash Payment QR",
                            modifier = Modifier.size(200.dp)
                        )

                        Spacer(Modifier.height(12.dp))

                        Text(
                            text = paymentUri,
                            modifier = Modifier
                                .background(Color(0xFFEFEFEF))
                                .padding(8.dp),
                            color = Color.DarkGray,
                            textAlign = TextAlign.Center
                        )

                        Spacer(Modifier.height(16.dp))

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Button(onClick = {
                                // Copy to clipboard
                                clipboard.setPrimaryClip(
                                    ClipData.newPlainText("Zcash Payment URI", paymentUri)
                                )
                                Toast.makeText(context, "Copied to clipboard", Toast.LENGTH_SHORT).show()
                            }) {
                                Text("Copy")
                            }

                            Button(onClick = {
                                // Share intent
                                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                    type = "text/plain"
                                    putExtra(Intent.EXTRA_TEXT, "Join my TabSplit: $paymentUri")
                                }
                                context.startActivity(Intent.createChooser(shareIntent, "Share via"))
                            }) {
                                Text("Share")
                            }
                        }

                        Spacer(Modifier.height(12.dp))

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Button(onClick = {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(paymentUri))
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                try {
                                    context.startActivity(intent)
                                } catch (_: Exception) {
                                    Toast.makeText(context, "No wallet app found", Toast.LENGTH_SHORT).show()
                                }
                            }) {
                                Text("Open in Wallet")
                            }
                            Button(
                                onClick = onClose,
                                colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                            ) {
                                Text("Close")
                            }
                        }

                        Spacer(Modifier.height(24.dp))

                        Text(
                            "If your Wallet asks you to rescan, just point your camera at this QR code.",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}
