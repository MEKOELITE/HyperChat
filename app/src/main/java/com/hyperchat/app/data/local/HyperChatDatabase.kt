package com.hyperchat.app.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.hyperchat.app.data.local.dao.ConversationDao
import com.hyperchat.app.data.local.dao.MessageDao
import com.hyperchat.app.data.local.dao.SuggestionDao
import com.hyperchat.app.data.local.entity.ConversationEntity
import com.hyperchat.app.data.local.entity.MessageEntity
import com.hyperchat.app.data.local.entity.SuggestionEntity

@Database(
    entities = [
        ConversationEntity::class,
        MessageEntity::class,
        SuggestionEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class HyperChatDatabase : RoomDatabase() {
    abstract fun conversationDao(): ConversationDao
    abstract fun messageDao(): MessageDao
    abstract fun suggestionDao(): SuggestionDao
}
