package com.ochuko.tabsplit.store

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ochuko.tabsplit.data.api.ApiClient
import com.ochuko.tabsplit.data.api.AuthApi
import com.ochuko.tabsplit.data.api.UserApi
import com.ochuko.tabsplit.data.repository.ZcashRepository
import com.ochuko.tabsplit.utils.Config

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class ZcashUiState(
    val currentZAddr: String? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val editMode: Boolean = false
)

class ZcashViewModel(ctx: Context, baseURL: String) : ViewModel() {
    private val userApi = ApiClient.create<UserApi>(token = null, baseUrl = baseURL)

    private val repo = ZcashRepository(userApi, ctx)

    private val _uiState = MutableStateFlow(
        ZcashUiState(currentZAddr = repo.loadSavedZAddr())
    )

        val uiState : StateFlow < ZcashUiState > = _uiState

    fun toggleEditMode(enabled: Boolean) {
        _uiState.value = _uiState.value.copy(editMode = enabled, error = null)
    }

    fun saveZAddr(zaddr: String) {
        if (!zaddr.startsWith("z") || zaddr.length < 30) {
            _uiState.value = _uiState.value.copy(error = "Invalid Zcash address")
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            val user = repo.updateZAddr(zaddr)
            if (user != null) {
                _uiState.value = _uiState.value.copy(
                    currentZAddr = user.zaddr,
                    editMode = false,
                    isLoading = false
                )
            } else {
                _uiState.value = _uiState.value.copy(
                    error = "Failed to save address",
                    isLoading = false
                )
            }
        }
    }

    fun deleteZAddr() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            val success = repo.deleteZAddr()
            if (success) {
                _uiState.value = _uiState.value.copy(
                    currentZAddr = null,
                    isLoading = false
                )
            } else {
                _uiState.value = _uiState.value.copy(
                    error = "Failed to delete ZAddr",
                    isLoading = false
                )
            }
        }
    }
}
