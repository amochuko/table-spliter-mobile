package com.partum.tabsplit.ui.components.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FileCopy
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.partum.tabsplit.R
import com.partum.tabsplit.utils.copyToClipboard
import com.partum.tabsplit.utils.generateQrCode
import com.partum.tabsplit.utils.shareInvite

@Composable
fun SessionQRCode(inviteUrl: String) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current

    // Get screen width for adaptive QR size
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    val qrSize = (screenWidth * 0.7f).coerceAtMost(480.dp) // max 480dp

    // Generate QR bitmap and cache it
    val qrBitmap by remember(inviteUrl) { mutableStateOf(generateQrCode(inviteUrl)) }

    Column(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // QR Code
        qrBitmap?.let {
            Image(
                bitmap = it.asImageBitmap(),
                contentDescription = stringResource(R.string.session_qr_code),
                modifier = Modifier
                    .size(qrSize)
                    .padding(vertical = 8.dp)
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
            IconButton(onClick = {
                copyToClipboard(context, clipboardManager, inviteUrl)
            }) {
                Icon(
                    imageVector = Icons.Default.FileCopy,
                    contentDescription = stringResource(R.string.copy_link)
                )
            }

            IconButton(onClick = {
                shareInvite(context, inviteUrl)
            }) {
                Icon(
                    imageVector = Icons.Default.Share,
                    contentDescription = stringResource(R.string.share_link)
                )
            }
        }
    }
}