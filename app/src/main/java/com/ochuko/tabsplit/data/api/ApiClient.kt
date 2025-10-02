package com.ochuko.tabsplit.data.api

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

import android.content.Context
import com.ochuko.tabsplit.store.AuthStore
import okhttp3.Interceptor
import kotlinx.coroutines.runBlocking


object ApiClient {
    private var retrofit: Retrofit? = null

    fun getRetrofit(context: Context, baseUrl: String ): Retrofit {
        if (retrofit == null) {
            val authStore = AuthStore(context) // replace with your actual AuthStore

            val client = OkHttpClient.Builder()
                .addInterceptor(Interceptor { chain ->
                    val reqBuilder = chain.request().newBuilder()
                    val token = runBlocking { authStore.getToken() }

                    if (!token.isNullOrEmpty()) {
                        reqBuilder.addHeader("Authorization", "Bearer $token")
                    }
                    chain.proceed(reqBuilder.build())
                }).build()

            retrofit = Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }
        return retrofit!!
    }

    // Generic helper to create any API service
    inline fun <reified T> create(context: Context, baseUrl: String): T {
        return getRetrofit(context, baseUrl).create(T::class.java)
    }
}
