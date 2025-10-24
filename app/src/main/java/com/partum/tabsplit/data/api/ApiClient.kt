package com.partum.tabsplit.data.api

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.partum.tabsplit.utils.AuthInterceptor

object ApiClient {
    @Volatile
    private var retrofit: Retrofit? = null

    private fun buildClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor()).build()
    }

    fun buildRetrofit(baseUrl: String): Retrofit {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(buildClient())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    fun <T> create(clazz: Class<T>, baseUrl: String): T {
        if (retrofit == null) {
            synchronized(this) {
                if (retrofit == null) retrofit = buildRetrofit(baseUrl)
            }
        }

        return retrofit!!.create(clazz)
    }

    // Generic helper to create any API service
    inline fun <reified T> create(
        token: String? = null,
        baseUrl: String
    ): T = create(T::class.java, baseUrl)
}

