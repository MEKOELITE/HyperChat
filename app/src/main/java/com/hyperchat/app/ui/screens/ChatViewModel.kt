package com.hyperchat.app.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hyperchat.app.domain.model.*
import com.hyperchat.app.domain.repository.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ChatUiState(
    val conversationId: Long = -1,
    val conversationInfo: ConversationInfo? = null,
    val currentStrategy: ConversationStrategy? = null,
    val suggestions: List<ReplySuggestion> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val conversationRepository: ConversationRepository,
    private val messageRepository: MessageRepository,
    private val aiChatRepository: AIChatRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

    fun loadConversation(conversationId: Long) {
        viewModelScope.launch {
            val info = conversationRepository.getConversationById(conversationId)
            _uiState.update { it.copy(conversationId = conversationId, conversationInfo = info) }
            loadStrategy()
        }
    }

    fun createNewConversation() {
        viewModelScope.launch {
            try {
                val newConversation = ConversationInfo(
                    contactRole = ContactRole.FRIEND,
                    familiarity = 5,
                    relationTemperature = 5,
                    hasHistoryConflict = false,
                    chatGoal = ChatGoal.CLOSER_RELATION,
                    chatStyle = ChatStyle.CASUAL
                )
                val id = conversationRepository.createConversation(newConversation)
                val info = conversationRepository.getConversationById(id)
                _uiState.update { it.copy(conversationId = id, conversationInfo = info) }
                loadStrategy()
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }

    fun loadStrategy() {
        val info = _uiState.value.conversationInfo ?: return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val strategy = aiChatRepository.getConversationStrategy(info, 1)
                _uiState.update { it.copy(currentStrategy = strategy, isLoading = false) }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message, isLoading = false) }
            }
        }
    }

    fun getSuggestions(theirMessage: String) {
        val info = _uiState.value.conversationInfo ?: return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            // Save their message
            messageRepository.addMessage(
                conversationId = _uiState.value.conversationId,
                content = theirMessage,
                isFromUser = false
            )

            try {
                val suggestions = aiChatRepository.getReplySuggestions(
                    conversationInfo = info,
                    theirMessage = theirMessage,
                    chatHistory = emptyList()
                )

                // Save suggestions
                val messageId = messageRepository.addMessage(
                    conversationId = _uiState.value.conversationId,
                    content = theirMessage,
                    isFromUser = false
                )

                _uiState.update { it.copy(suggestions = suggestions, isLoading = false) }

                // Update strategy phase
                val newPhase = (_uiState.value.currentStrategy?.currentPhase ?: 1) + 1
                val newStrategy = aiChatRepository.getConversationStrategy(info, newPhase)
                _uiState.update { it.copy(currentStrategy = newStrategy) }

            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message, isLoading = false) }
            }
        }
    }
}
