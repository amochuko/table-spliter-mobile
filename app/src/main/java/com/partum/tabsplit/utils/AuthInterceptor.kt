package com.partum.tabsplit.utils

import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor: Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val reqBuilder=chain.request().newBuilder()

        AuthSessionManager.token?.let{ t->
            reqBuilder.addHeader("Authorization", "Bearer $t")
        }

        return chain.proceed(reqBuilder.build())
    }
}