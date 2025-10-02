package com.ochuko.tabsplit.utils

import android.content.Context
import com.ochuko.tabsplit.store.AuthStore
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient{
fun create(ctx: Context, baseUrl:String): Retrofit{
val authStore= AuthStore(ctx)

    val client = OkHttpClient.Builder()
        .addInterceptor (Interceptor { chain ->
                val req = chain.request().newBuilder()

                val token = runBlocking { authStore.getToken() }
                if (!token.isNullOrBlank()) {
                    req.addHeader("Authorization", "Bearer $token")
                }

                chain.proceed(req.build())

            }).build()

    return Retrofit.Builder()
        .baseUrl(baseUrl)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
}
}