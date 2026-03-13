package com.hyperchat.app.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hyperchat.app.data.local.SettingsManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SettingsUiState(
    val apiKey: String = "",
    val apiGroup: String = "",
    val isLoading: Boolean = false,
    val isSaved: Boolean = false
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsManager: SettingsManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    fun loadSettings() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            val apiKey = settingsManager.apiKey.first()
            val apiGroup = settingsManager.apiGroup.first()

            _uiState.update {
                it.copy(
                    apiKey = apiKey,
                    apiGroup = apiGroup,
                    isLoading = false
                )
            }
        }
    }

    fun saveSettings(apiKey: String, apiGroup: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            settingsManager.setApiKey(apiKey)
            settingsManager.setApiGroup(apiGroup)

            _uiState.update {
                it.copy(
                    apiKey = apiKey,
                    apiGroup = apiGroup,
                    isLoading = false,
                    isSaved = true
                )
            }

            // Reset saved status after 2 seconds
            kotlinx.coroutines.delay(2000)
            _uiState.update { it.copy(isSaved = false) }
        }
    }
}
