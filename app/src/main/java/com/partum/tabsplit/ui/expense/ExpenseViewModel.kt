package com.partum.tabsplit.ui.expense

import androidx.lifecycle.ViewModel
import com.partum.tabsplit.data.model.AddExpenseRequest
import com.partum.tabsplit.data.model.Expense
import com.partum.tabsplit.data.repository.SessionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class ExpenseViewModel(
    private val sessionRep: SessionRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(ExpenseUiState())
    val uiState: StateFlow<ExpenseUiState> = _uiState.asStateFlow()


    suspend fun addExpense(sessionId: String, memo: String, amount: Double) {
        try {
            val res = sessionRep.addExpenses(sessionId, AddExpenseRequest(memo, amount))
            res?.let {
                val updated = _uiState.value.expenses.toMutableMap()
                val newList = updated[it.sessionId].orEmpty() + it.expenses

                updated[it.sessionId] = newList
                _uiState.update { s -> s.copy(expenses = updated) }
            }
        } catch (e: Exception) {
            _uiState.update { it.copy(error = e.message) }
        }
    }

    fun deleteExpense(sessionId: String, expenseId: String) {
        val updated = _uiState.value.expenses.toMutableMap()
        updated[sessionId] = updated[sessionId].orEmpty().filterNot { it.id == expenseId }

        _uiState.update { it.copy(expenses = updated) }
    }

    fun updateExpenses(sessionId: String, expenses: List<Expense>) {
        _uiState.update {
            it.copy(
                expenses = it.expenses + (sessionId to expenses)
            )
        }
    }
}