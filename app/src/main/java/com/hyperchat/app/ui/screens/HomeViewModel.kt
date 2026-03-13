package com.hyperchat.app.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hyperchat.app.domain.model.ChatGoal
import com.hyperchat.app.domain.model.ChatStyle
import com.hyperchat.app.domain.model.ConversationInfo
import com.hyperchat.app.domain.model.ContactRole
import com.hyperchat.app.domain.repository.ConversationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val conversationRepository: ConversationRepository
) : ViewModel() {

    val conversations: StateFlow<List<ConversationInfo>> = conversationRepository
        .getAllConversations()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun createNewConversation(onCreated: (Long) -> Unit) {
        viewModelScope.launch {
            val newConversation = ConversationInfo(
                contactRole = ContactRole.FRIEND,
                familiarity = 5,
                relationTemperature = 5,
                hasHistoryConflict = false,
                chatGoal = ChatGoal.CLOSER_RELATION,
                chatStyle = ChatStyle.CASUAL
            )
            val id = conversationRepository.createConversation(newConversation)
            onCreated(id)
        }
    }
}
