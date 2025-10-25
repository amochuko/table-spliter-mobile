package com.partum.tabsplit.di

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.partum.tabsplit.data.repository.ExpenseRepository
import com.partum.tabsplit.data.repository.SessionRepository
import com.partum.tabsplit.ui.auth.AuthViewModel
import com.partum.tabsplit.ui.expense.ExpenseViewModel
import com.partum.tabsplit.ui.participant.ParticipantViewModel
import com.partum.tabsplit.ui.session.SessionViewModel

class AppViewModelFactory(
    private val sessionRepo: SessionRepository,
    private val expenseRepo: ExpenseRepository,
    private val authViewModel: AuthViewModel
) : ViewModelProvider.Factory {

    // Lazily initialized child view models that need to share state
    private val participantViewModel by lazy { ParticipantViewModel(sessionRepo) }
    private val expenseViewModel by lazy { ExpenseViewModel(sessionRepo, expenseRepo) }


    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        Log.d("AppViewModelFactory", "Creating ViewModel: ${modelClass.simpleName}")

        return when {

            modelClass.isAssignableFrom(SessionViewModel::class.java) -> {
                SessionViewModel(
                    sessionRepo,
                    authViewModel,
                    participantViewModel,
                    expenseViewModel
                ) as T
            }

            modelClass.isAssignableFrom(ParticipantViewModel::class.java) -> {
                participantViewModel as T
            }

            modelClass.isAssignableFrom(ExpenseViewModel::class.java) -> {
                ExpenseViewModel(sessionRepo, expenseRepo) as T
            }


            modelClass.isAssignableFrom(AuthViewModel::class.java) -> {
                authViewModel as T
            }

            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}