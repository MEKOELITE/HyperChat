package com.hyperchat.app.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hyperchat.app.domain.model.*
import com.hyperchat.app.domain.repository.AIChatRepository
import com.hyperchat.app.domain.repository.ConversationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ReviewUiState(
    val report: ReviewReport? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class ReviewViewModel @Inject constructor(
    private val aiChatRepository: AIChatRepository,
    private val conversationRepository: ConversationRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ReviewUiState())
    val uiState: StateFlow<ReviewUiState> = _uiState.asStateFlow()

    fun analyzeChat(chatContent: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            try {
                // Parse chat messages
                val messages = chatContent.split("\n")
                    .filter { it.isNotBlank() }
                    .map { it.trim() }

                // Get or create a conversation for context
                val conversations = conversationRepository.getAllConversations().first()
                val conversationInfo = conversations.firstOrNull() ?: ConversationInfo()

                // Generate review report
                val report = aiChatRepository.getChatReviewReport(
                    conversationInfo = conversationInfo,
                    messages = messages
                )

                _uiState.update { it.copy(report = report, isLoading = false) }

            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message, isLoading = false) }
            }
        }
    }
}
