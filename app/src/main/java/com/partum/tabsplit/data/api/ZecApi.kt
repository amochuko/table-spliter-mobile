package com.partum.tabsplit.data.api

import retrofit2.Response
import retrofit2.http.GET

data class USDRateResponse(
    val zecPriceUsd: Double
)

interface ZecApi {
    @GET("/zec/usd-rate")
    suspend fun fetchUSDRate(): Response<USDRateResponse>
}



