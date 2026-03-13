package com.hyperchat.app.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hyperchat.app.domain.model.*
import com.hyperchat.app.domain.repository.ConversationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class NewConversationUiState(
    val contactRole: ContactRole = ContactRole.FRIEND,
    val familiarity: Int = 5,
    val relationTemperature: Int = 5,
    val hasHistoryConflict: Boolean = false,
    val chatGoal: ChatGoal = ChatGoal.CLOSER_RELATION,
    val chatStyle: ChatStyle = ChatStyle.CASUAL,
    val createdConversationId: Long? = null,
    val isLoading: Boolean = false
)

@HiltViewModel
class NewConversationViewModel @Inject constructor(
    private val conversationRepository: ConversationRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(NewConversationUiState())
    val uiState: StateFlow<NewConversationUiState> = _uiState.asStateFlow()

    fun setContactRole(role: ContactRole) {
        _uiState.update { it.copy(contactRole = role) }
    }

    fun setFamiliarity(value: Int) {
        _uiState.update { it.copy(familiarity = value) }
    }

    fun setRelationTemperature(value: Int) {
        _uiState.update { it.copy(relationTemperature = value) }
    }

    fun setHasHistoryConflict(value: Boolean) {
        _uiState.update { it.copy(hasHistoryConflict = value) }
    }

    fun setChatGoal(goal: ChatGoal) {
        _uiState.update { it.copy(chatGoal = goal) }
    }

    fun setChatStyle(style: ChatStyle) {
        _uiState.update { it.copy(chatStyle = style) }
    }

    fun createConversation() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            val state = _uiState.value
            val conversation = ConversationInfo(
                contactRole = state.contactRole,
                familiarity = state.familiarity,
                relationTemperature = state.relationTemperature,
                hasHistoryConflict = state.hasHistoryConflict,
                chatGoal = state.chatGoal,
                chatStyle = state.chatStyle
            )

            val id = conversationRepository.createConversation(conversation)
            _uiState.update { it.copy(createdConversationId = id, isLoading = false) }
        }
    }
}
