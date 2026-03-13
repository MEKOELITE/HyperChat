package com.hyperchat.app.data.repository

import com.hyperchat.app.data.local.dao.ConversationDao
import com.hyperchat.app.data.local.dao.MessageDao
import com.hyperchat.app.data.local.dao.SuggestionDao
import com.hyperchat.app.data.local.entity.ConversationEntity
import com.hyperchat.app.data.local.entity.MessageEntity
import com.hyperchat.app.data.local.entity.SuggestionEntity
import com.hyperchat.app.domain.model.*
import com.hyperchat.app.domain.repository.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ConversationRepositoryImpl @Inject constructor(
    private val conversationDao: ConversationDao
) : ConversationRepository {

    override fun getAllConversations(): Flow<List<ConversationInfo>> {
        return try {
            conversationDao.getAllConversations().map { entities ->
                entities.mapNotNull { entity ->
                    try {
                        entity.toDomainSafe()
                    } catch (e: Exception) {
                        null
                    }
                }
            }
        } catch (e: Exception) {
            flow { emit(emptyList()) }
        }
    }

    override suspend fun getConversationById(id: Long): ConversationInfo? {
        return try {
            conversationDao.getConversationById(id)?.toDomainSafe()
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun createConversation(info: ConversationInfo): Long {
        return try {
            conversationDao.insertConversation(info.toEntity())
        } catch (e: Exception) {
            -1L
        }
    }

    override suspend fun updateConversation(info: ConversationInfo) {
        try {
            conversationDao.updateConversation(info.toEntity())
        } catch (e: Exception) {
            // ignore
        }
    }

    override suspend fun deleteConversation(id: Long) {
        try {
            conversationDao.deleteConversationById(id)
        } catch (e: Exception) {
            // ignore
        }
    }

    private fun ConversationEntity.toDomainSafe(): ConversationInfo {
        return ConversationInfo(
            id = id,
            contactRole = try { ContactRole.valueOf(contactRole) } catch (e: Exception) { ContactRole.FRIEND },
            familiarity = familiarity,
            relationTemperature = relationTemperature,
            hasHistoryConflict = hasHistoryConflict,
            chatGoal = try { ChatGoal.valueOf(chatGoal) } catch (e: Exception) { ChatGoal.CLOSER_RELATION },
            chatStyle = try { ChatStyle.valueOf(chatStyle) } catch (e: Exception) { ChatStyle.CASUAL },
            createdAt = createdAt
        )
    }

    private fun ConversationEntity.toDomain() = ConversationInfo(
        id = id,
        contactRole = ContactRole.valueOf(contactRole),
        familiarity = familiarity,
        relationTemperature = relationTemperature,
        hasHistoryConflict = hasHistoryConflict,
        chatGoal = ChatGoal.valueOf(chatGoal),
        chatStyle = ChatStyle.valueOf(chatStyle),
        createdAt = createdAt
    )

    private fun ConversationInfo.toEntity() = ConversationEntity(
        id = id,
        contactRole = contactRole.name,
        familiarity = familiarity,
        relationTemperature = relationTemperature,
        hasHistoryConflict = hasHistoryConflict,
        chatGoal = chatGoal.name,
        chatStyle = chatStyle.name,
        createdAt = createdAt
    )
}

@Singleton
class MessageRepositoryImpl @Inject constructor(
    private val messageDao: MessageDao
) : MessageRepository {

    override fun getMessagesForConversation(conversationId: Long): Flow<List<TheirMessage>> {
        return try {
            messageDao.getMessagesForConversation(conversationId).map { entities ->
                entities.filter { !it.isFromUser }.map { it.toDomain() }
            }
        } catch (e: Exception) {
            flow { emit(emptyList()) }
        }
    }

    override suspend fun addMessage(conversationId: Long, content: String, isFromUser: Boolean): Long {
        return try {
            messageDao.insertMessage(
                MessageEntity(
                    conversationId = conversationId,
                    content = content,
                    isFromUser = isFromUser,
                    timestamp = System.currentTimeMillis()
                )
            )
        } catch (e: Exception) {
            -1L
        }
    }

    override suspend fun deleteMessagesForConversation(conversationId: Long) {
        try {
            messageDao.deleteMessagesForConversation(conversationId)
        } catch (e: Exception) {
            // ignore
        }
    }

    private fun MessageEntity.toDomain() = TheirMessage(
        id = id,
        conversationId = conversationId,
        content = content,
        timestamp = timestamp
    )
}

@Singleton
class SuggestionRepositoryImpl @Inject constructor(
    private val suggestionDao: SuggestionDao
) : SuggestionRepository {

    override fun getSuggestionsForConversation(conversationId: Long): Flow<List<ReplySuggestion>> {
        return try {
            suggestionDao.getSuggestionsForConversation(conversationId).map { entities ->
                entities.map { it.toDomain() }
            }
        } catch (e: Exception) {
            flow { emit(emptyList()) }
        }
    }

    override suspend fun saveSuggestions(conversationId: Long, messageId: Long, suggestions: List<ReplySuggestion>) {
        try {
            suggestionDao.insertSuggestions(suggestions.map { it.toEntity(conversationId, messageId) })
        } catch (e: Exception) {
            // ignore
        }
    }

    override suspend fun deleteSuggestionsForConversation(conversationId: Long) {
        try {
            suggestionDao.deleteSuggestionsForConversation(conversationId)
        } catch (e: Exception) {
            // ignore
        }
    }

    private fun SuggestionEntity.toDomain() = ReplySuggestion(
        id = id,
        conversationId = conversationId,
        type = type,
        content = content,
        reason = reason
    )

    private fun ReplySuggestion.toEntity(conversationId: Long, messageId: Long) = SuggestionEntity(
        id = id,
        conversationId = conversationId,
        messageId = messageId,
        type = type,
        content = content,
        reason = reason
    )
}
