package com.partum.tabsplit.utils

import android.content.Context
import android.widget.Toast
import androidx.compose.ui.text.AnnotatedString
import com.partum.tabsplit.R

// Overload to use Compose LocalClipboardManager
fun copyToClipboard(
    context: Context,
    clipboardManager: androidx.compose.ui.platform.ClipboardManager,
    text: String
) {
    clipboardManager.setText(AnnotatedString(text))
    Toast.makeText(context, context.getString(R.string.invite_link_copied), Toast.LENGTH_SHORT).show()
}
