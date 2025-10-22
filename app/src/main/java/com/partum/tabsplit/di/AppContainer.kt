package com.partum.tabsplit.di

import android.app.Application
import com.partum.tabsplit.data.api.ApiClient
import com.partum.tabsplit.data.api.SessionApi
import com.partum.tabsplit.data.repository.SessionRepository
import com.partum.tabsplit.ui.auth.AuthViewModel
import com.partum.tabsplit.utils.Config

class AppContainer(app: Application) {

    // ViewModels
    val authViewModel = AuthViewModel(app)

    // APIs
    private val sessionApi: SessionApi by lazy {
        ApiClient.create<SessionApi>(authViewModel.getToken(), Config.API_BASE_URL)
    }

    // Repository
    val sessionRepo: SessionRepository by lazy {
        SessionRepository(sessionApi)
    }
}