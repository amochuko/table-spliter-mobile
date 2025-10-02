package com.ochuko.tabsplit.store

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ochuko.tabsplit.models.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AppStore: ViewModel() {

    private  val _token = MutableStateFlow<String?>(null);
    val token: StateFlow<String?> = _token

    private  val  _user = MutableStateFlow<User?>(null)
    val user : StateFlow<User?> = _user

    private val _sessions = MutableStateFlow<List<Session>>(emptyList())
    val sessions: StateFlow<List<Session>> = _sessions

    private  val  _participants = MutableStateFlow<Map<String,List<Participant>>>(emptyMap())
    val participants: StateFlow<Map<String, List<Participant>>> = _participants

    private  val  _expenses = MutableStateFlow<Map<String,List<Expense>>>(emptyMap())
    val expenses: StateFlow<Map<String, List<Expense>>> = _expenses


    private  val  _pendingInviteCode = MutableStateFlow<String?>(null)
    val pendingInviteCode: StateFlow<String?> = _pendingInviteCode

    fun setPendingInviteCode(code: String?){
        _pendingInviteCode.value = code
    }

    fun setUser(user: User, token:String){
        _user.value = user
        _token.value = token
    }

    fun setSession(session:List<Session>){
        _sessions.value = session

    fun addSession(session: Session){
        _sessions.value = _sessions.value + session
    }


        fun deleteSession(sessionId:String){
        _sessions.value = _sessions.value.filterNot { it.id == sessionId }
        }

        fun addExpense(sessionId: String,  expense: Expense){
            val current = _expenses.value.toMutableMap()
            val updatedList = current[sessionId].orEmpty() + expense
            current[sessionId] = updatedList
            _expenses.value = current
        }

        fun deleteExpense(sessionId: String,  expenseId: String)
        {
            val current = _expenses.value.toMutableMap();
            val updatedList = current[sessionId].orEmpty().filterNot { it.id == expenseId }
            current[sessionId] = updatedList
            _expenses.value = current
        }

        // -- Async operation (stubs for now)
        fun loadSession() = viewModelScope.launch {
            // TODO: fetch session from API
        }

        fun fetchSession(sessionId:String) = viewModelScope.launch {
            // TODO: fetch single sesssion details
        }

        fun createSession(title: String,  description: String,  currency: String = "ZEC") =
            viewModelScope.launch {
                // TODO: call API
                // return session + participant
            }

        fun joinByInvite(inviteCode:String) = viewModelScope.launch {
            // TODO: call API
        }
    }
}