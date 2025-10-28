package com.partum.tabsplit.data.repository

import android.util.Log
import com.partum.tabsplit.data.api.ZecApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


class ZecRepository(private val zecApi: ZecApi) {
    suspend fun getZecUsdRate(): Double? = withContext(Dispatchers.IO) {
        val res = zecApi.fetchUSDRate()

        if (res.isSuccessful) {

            val usdRate = res.body()?.zecPriceUsd
            usdRate
        } else {
            null
        }
    }
}