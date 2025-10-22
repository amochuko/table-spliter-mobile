package com.partum.tabsplit.di

import android.app.Application
import androidx.lifecycle.ViewModelProvider
import com.partum.tabsplit.data.api.ApiClient
import com.partum.tabsplit.data.api.SessionApi
import com.partum.tabsplit.data.repository.SessionRepository
import com.partum.tabsplit.ui.auth.AuthViewModel
import com.partum.tabsplit.ui.expense.ExpenseViewModel
import com.partum.tabsplit.ui.participant.ParticipantViewModel
import com.partum.tabsplit.utils.Config

interface IAppContainer {
    val sessionRepository: SessionRepository
}

class AppContainer(app: Application) : IAppContainer {

    // APIs
    private val sessionApi: SessionApi by lazy {
        ApiClient.create<SessionApi>(authViewModel.getToken(), Config.API_BASE_URL)
    }

    // --- Repository
    override val sessionRepository: SessionRepository by lazy {
        SessionRepository(sessionApi)
    }

    // --- ViewModels
    val authViewModel = AuthViewModel(app)
//    val expenseViewModel = ExpenseViewModel(sessionRepository)
//    val participantViewModel = ParticipantViewModel(sessionRepository)


    // --- ViewModel Factory
    val viewModelFactory: ViewModelProvider.Factory by lazy {
        AppViewModelFactory(
            sessionRepository,
            authViewModel
        )
    }
}