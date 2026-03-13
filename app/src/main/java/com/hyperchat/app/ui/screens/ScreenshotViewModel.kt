package com.hyperchat.app.ui.screens

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hyperchat.app.domain.model.ScreenshotAnalysis
import com.hyperchat.app.domain.repository.AIChatRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import javax.inject.Inject

data class ScreenshotUiState(
    val selectedImageUri: Uri? = null,
    val analysis: ScreenshotAnalysis? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class ScreenshotViewModel @Inject constructor(
    private val aiChatRepository: AIChatRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ScreenshotUiState())
    val uiState: StateFlow<ScreenshotUiState> = _uiState.asStateFlow()

    fun analyzeImage(context: Context, uri: Uri) {
        viewModelScope.launch {
            _uiState.update { it.copy(selectedImageUri = uri, isLoading = true) }

            try {
                // Read image bytes
                val inputStream = context.contentResolver.openInputStream(uri)
                val bytes = inputStream?.readBytes() ?: ByteArray(0)
                inputStream?.close()

                // Analyze with AI
                val analysis = aiChatRepository.analyzeScreenshot(bytes)
                _uiState.update { it.copy(analysis = analysis, isLoading = false) }

            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message, isLoading = false) }
            }
        }
    }
}
