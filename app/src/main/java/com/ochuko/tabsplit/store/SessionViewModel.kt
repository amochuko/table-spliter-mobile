package com.ochuko.tabsplit.store


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ochuko.tabsplit.models.Session
import kotlinx.coroutines.launch


class SessionViewModel(private val appStore: AppStore) : ViewModel() {
    val sessions = appStore.sessions


    fun fetchSession(id: String) {
        viewModelScope.launch { appStore.fetchSession(id) }
    }

    fun addSession(session: Session) {
        viewModelScope.launch { appStore.addSession(session) }

    }

    suspend fun createSession(title: String, description: String): Session? {
       val sess = appStore.createSession(title, description)
        return sess
    }

}