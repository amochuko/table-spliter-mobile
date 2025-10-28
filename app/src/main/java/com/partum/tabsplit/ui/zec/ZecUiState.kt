package com.partum.tabsplit.ui.zec

 data class ZecUiState(
     val loading: Boolean  = false,
     val error: String? = null,
     val usdRate: Double = 0.0
 )
