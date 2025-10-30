package com.partum.tabsplit.ui.components.ui

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import com.partum.tabsplit.utils.createZcashUri
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.util.Log
import androidx.compose.ui.res.stringResource
import com.partum.tabsplit.R
import com.partum.tabsplit.ui.zec.ZecViewModel
import androidx.core.net.toUri
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun ZcashIntegration(
    visible: Boolean,
    onClose: () -> Unit,
    sessionId: String,
    balances: Map<String, Double>,
    recipientAddress: String,
    zecViewModel: ZecViewModel
) {
    if (!visible) return

    val context = LocalContext.current
    val clipboard = context.getSystemService(ClipboardManager::class.java)

    // compute amount owed
    val totalAmount = balances.values.filter { it > 0 }.sum()

    val zecUiState by zecViewModel.uiState.collectAsState()

    LaunchedEffect(totalAmount, zecUiState.usdRate) {
        zecViewModel.getUsdRate()
    }

    val amountInZec = totalAmount.div(zecUiState.usdRate)

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
                        Text(stringResource(R.string.waiting_for_recipient_address))
                        Spacer(Modifier.height(16.dp))
                        Button(onClick = onClose) { Text(stringResource(R.string.close)) }
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
                            stringResource(R.string.nothing_to_settle),
                            style = MaterialTheme.typography.titleMedium
                        )
                        Spacer(Modifier.height(16.dp))
                        Button(onClick = onClose) { Text(stringResource(R.string.close)) }
                    }
                }

                else -> {

                    val paymentUri = createZcashUri(
                        recipientAddress,
                        amountInZec,
                        stringResource(R.string.session_settlement, sessionId)
                    )

                    Column(
                        modifier = Modifier
                            .padding(24.dp)
                            .fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            stringResource(R.string.settle_with_zec),
                            style = MaterialTheme.typography.titleLarge
                        )

                        Spacer(Modifier.height(16.dp))

                        // Generate QR bitmap
                        val qrBitmap = remember(paymentUri) {
                            val size = 512
                            val writer = QRCodeWriter()
                            val bitMatrix =
                                writer.encode(paymentUri, BarcodeFormat.QR_CODE, size, size)
                            val bmp = android.graphics.Bitmap.createBitmap(
                                size,
                                size,
                                android.graphics.Bitmap.Config.RGB_565
                            )
                            for (x in 0 until size) {
                                for (y in 0 until size) {
                                    bmp.setPixel(
                                        x,
                                        y,
                                        if (bitMatrix[x, y]) android.graphics.Color.BLACK else android.graphics.Color.WHITE
                                    )
                                }
                            }
                            bmp.asImageBitmap()
                        }

                        Image(
                            bitmap = qrBitmap,
                            contentDescription = stringResource(R.string.zcash_payment_qr),
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
                                    ClipData.newPlainText(
                                        context.getString(R.string.zcash_payment_uri),
                                        paymentUri
                                    )
                                )
                                Toast.makeText(
                                    context,
                                    context.getString(R.string.copied_to_clipboard),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }) {
                                Text(stringResource(R.string.copy))
                            }

                            Button(onClick = {
                                // Share intent
                                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                    type = "text/plain"
                                    putExtra(
                                        Intent.EXTRA_TEXT,
                                        context.getString(R.string.join_my_tabsplit_txt, paymentUri)
                                    )
                                }
                                context.startActivity(
                                    Intent.createChooser(
                                        shareIntent,
                                        context.getString(R.string.share_via)
                                    )
                                )
                            }) {
                                Text(stringResource(R.string.share))
                            }
                        }

                        Spacer(Modifier.height(12.dp))

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Button(onClick = {
                                openZcashWallet(context, paymentUri)
                            }) {
                                Text(stringResource(R.string.open_in_wallet))
                            }
                            Button(
                                onClick = onClose,
                                colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                            ) {
                                Text(stringResource(R.string.close))
                            }
                        }

                        Spacer(Modifier.height(24.dp))

                        Text(
                            stringResource(R.string.if_your_wallet_asks_you_to_rescan_just_point_your_camera_at_this_qr_code),
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

private fun openZcashWallet(context: Context, paymentUri: String) {

    val uri = paymentUri.toUri()

    try {
        CoroutineScope(Dispatchers.Main).launch {
            delay(700)

            val intent =
                Intent(Intent.ACTION_VIEW, uri).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
                }

            try {
                context.startActivity(intent)
            } catch (e: Exception) {
                Log.e("ZecIntegration::openZcashWallet", e.message!!)

                Toast.makeText(
                    context,
                    context.getString(R.string.no_wallet_app_found),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    } catch (e: Exception) {
        Log.e("ZecIntegration::openZcashWallet", e.message!!)

        Toast.makeText(
            context,
            context.getString(R.string.no_wallet_app_found),
            Toast.LENGTH_SHORT
        ).show()
    }
}
