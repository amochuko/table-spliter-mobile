package com.ochuko.tabsplit.di

import android.app.Application
import com.ochuko.tabsplit.data.api.ApiClient
import com.ochuko.tabsplit.data.api.SessionApi
import com.ochuko.tabsplit.data.repository.SessionRepository
import com.ochuko.tabsplit.ui.auth.AuthViewModel
import com.ochuko.tabsplit.viewModels.BASE_URL

class AppContainer(app: Application) {

    // ViewModels
    val authViewModel = AuthViewModel(app)

    // APIs
    private val sessionApi: SessionApi by lazy {
        ApiClient.create<SessionApi>(authViewModel.getToken(), BASE_URL)
    }

    // Repository
    val sessionRepo: SessionRepository by lazy {
        SessionRepository(sessionApi)
    }
}