package com.ochuko.tabsplit.data.api

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import android.util.Log


object ApiClient {
    @Volatile
    private var retrofit: Retrofit? = null

    @Volatile
    private var lastToken: String? = null

    private fun buildClient(token: String?): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor { chain ->
                val reqBuilder = chain.request().newBuilder()

                if (!token.isNullOrEmpty()) {
                    reqBuilder.addHeader("Authorization", "Bearer $token")
                }

                chain.proceed(reqBuilder.build())
            }.build()
    }

    private fun buildRetrofit(token: String?, baseUrl: String): Retrofit {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(buildClient(token))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    fun getRetrofit(token: String?, baseUrl: String): Retrofit {

        // If token changed or retrofit never built, rebuild Retrofit
        if (retrofit == null || token != lastToken) {
            synchronized(this) {
                if (retrofit == null || token != lastToken) {
                    lastToken = token
                    retrofit = buildRetrofit(token, baseUrl)

                    Log.d(
                        "ApiClient",
                        "Retrofit rebuilt (token updated = ${!token.isNullOrEmpty()}"
                    )
                }
            }
        }

        return retrofit!!
    }

    // Generic helper to create any API service
    inline fun <reified T> create(
        token: String? = null,
        baseUrl: String
    ): T {
        return getRetrofit(token, baseUrl).create(T::class.java)
    }
}
