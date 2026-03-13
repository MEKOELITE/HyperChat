package com.hyperchat.app.domain.repository

import com.hyperchat.app.domain.model.*
import kotlinx.coroutines.flow.Flow

interface ConversationRepository {
    fun getAllConversations(): Flow<List<ConversationInfo>>
    suspend fun getConversationById(id: Long): ConversationInfo?
    suspend fun createConversation(info: ConversationInfo): Long
    suspend fun updateConversation(info: ConversationInfo)
    suspend fun deleteConversation(id: Long)
}

interface MessageRepository {
    fun getMessagesForConversation(conversationId: Long): Flow<List<TheirMessage>>
    suspend fun addMessage(conversationId: Long, content: String, isFromUser: Boolean): Long
    suspend fun deleteMessagesForConversation(conversationId: Long)
}

interface SuggestionRepository {
    fun getSuggestionsForConversation(conversationId: Long): Flow<List<ReplySuggestion>>
    suspend fun saveSuggestions(conversationId: Long, messageId: Long, suggestions: List<ReplySuggestion>)
    suspend fun deleteSuggestionsForConversation(conversationId: Long)
}

interface AIChatRepository {
    suspend fun getReplySuggestions(
        conversationInfo: ConversationInfo,
        theirMessage: String,
        chatHistory: List<String>
    ): List<ReplySuggestion>

    suspend fun analyzeScreenshot(imageBytes: ByteArray): ScreenshotAnalysis

    suspend fun getConversationStrategy(
        conversationInfo: ConversationInfo,
        currentPhase: Int
    ): ConversationStrategy

    suspend fun getChatReviewReport(
        conversationInfo: ConversationInfo,
        messages: List<String>
    ): ReviewReport
}

interface EQLibraryRepository {
    fun getExamplesByScenario(scenario: EQScenario): List<EQExample>
    fun getAllScenarios(): List<EQScenario>
}
