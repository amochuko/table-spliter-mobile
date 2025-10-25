package com.partum.tabsplit.di

import android.app.Application
import androidx.lifecycle.ViewModelProvider
import com.partum.tabsplit.data.api.ApiClient
import com.partum.tabsplit.data.api.SessionApi
import com.partum.tabsplit.data.repository.ExpenseRepository
import com.partum.tabsplit.data.repository.SessionRepository
import com.partum.tabsplit.ui.auth.AuthViewModel
import com.partum.tabsplit.ui.expense.ExpenseViewModel
import com.partum.tabsplit.ui.participant.ParticipantViewModel
import com.partum.tabsplit.utils.Config
import com.partum.tabsplit.data.api.ExpenseApi

interface IAppContainer {
    val sessionRepository: SessionRepository
    val expenseRepository: ExpenseRepository
}

class AppContainer(app: Application) : IAppContainer {

    // APIs
    private val sessionApi: SessionApi by lazy {
        ApiClient.create<SessionApi>(authViewModel.getToken(), Config.API_BASE_URL)
    }
    private val expenseApi: ExpenseApi by lazy {
        ApiClient.create<ExpenseApi>(null, Config.API_BASE_URL)
    }

    // --- Repository
    override val sessionRepository: SessionRepository by lazy {
        SessionRepository(sessionApi)
    }

    override val expenseRepository: ExpenseRepository by lazy {
        ExpenseRepository(expenseApi)
    }

    // --- ViewModels
    val authViewModel = AuthViewModel(app)
//    val expenseViewModel = ExpenseViewModel(
//        sessionRepository,
//        expenseRepository
//    )
//    val participantViewModel = ParticipantViewModel(sessionRepository)


    // --- ViewModel Factory
    val viewModelFactory: ViewModelProvider.Factory by lazy {
        AppViewModelFactory(
            sessionRepository,
            expenseRepository,
            authViewModel
        )
    }
}