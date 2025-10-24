package com.partum.tabsplit.utils

import android.content.Context
import android.content.Intent
import com.partum.tabsplit.R

// Share Invite via Intent
fun shareInvite(context: Context, text: String) {
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, context.getString(R.string.join_my_tabsplit, text))
    }
    context.startActivity(Intent.createChooser(intent, context.getString(R.string.share_via)))
}
