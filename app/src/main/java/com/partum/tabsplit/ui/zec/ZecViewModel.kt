package com.partum.tabsplit.ui.zec


import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.partum.tabsplit.data.repository.ZecRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ZecViewModel(
    private val zecRepo: ZecRepository
) : ViewModel() {

    var uiState = MutableStateFlow(ZecUiState())
        private set

    fun getUsdRate() = viewModelScope.launch {
        uiState.update {
            it.copy(loading = true)
        }

        try {
            val value = zecRepo.getZecUsdRate()

            uiState.update {
                it.copy(
                    usdRate = value!!,
                    loading = false,
                )
            }
        } catch (e: Exception) {
            Log.e("ZecViewModel", "getUsdRate: ${e.message}")
            uiState.update { it.copy(loading = false, error= "Failed to fetch USD rate") }
        }
    }
}