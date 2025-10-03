package com.ochuko.tabsplit.store


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ochuko.tabsplit.models.Session
import kotlinx.coroutines.launch


class SessionViewModel(private val store: AppStore) : ViewModel() {
    val sessions = store.sessions


    fun fetchSession(id: String) {
        viewModelScope.launch { store.fetchSession(id) }
    }

    fun addSession(session: Session) {
        viewModelScope.launch { store.addSession(session) }

    }

    suspend fun createSession(title: String, description: String): Session? {
       val sess = store.createSession(title, description)
        return sess
    }

}