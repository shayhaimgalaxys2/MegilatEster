package com.mobilegiants.megila.viewmodels

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class MainViewModel : ViewModel() {

    data class UiState(
        val showSplash: Boolean = true,
        val isAutoScrollEnabled: Boolean = false,
        val scrollSpeed: Int = -1,
        val adState: AdState = AdState.Loading
    )

    sealed class AdState {
        data object Loading : AdState()
        data object Ready : AdState()
        data object Failed : AdState()
        data object Dismissed : AdState()
    }

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    fun onSplashFinished() {
        _uiState.value = _uiState.value.copy(showSplash = false)
    }

    fun onAdLoaded() {
        _uiState.value = _uiState.value.copy(adState = AdState.Ready)
    }

    fun onAdFailed() {
        _uiState.value = _uiState.value.copy(adState = AdState.Failed)
    }

    fun onAdDismissed() {
        _uiState.value = _uiState.value.copy(adState = AdState.Dismissed)
    }

    fun setAutoScroll(enabled: Boolean, speed: Int = -1) {
        _uiState.value = _uiState.value.copy(
            isAutoScrollEnabled = enabled,
            scrollSpeed = speed
        )
    }
}
