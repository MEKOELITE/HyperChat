package com.hyperchat.app.di

import android.content.Context
import androidx.room.Room
import com.hyperchat.app.data.local.HyperChatDatabase
import com.hyperchat.app.data.local.SettingsManager
import com.hyperchat.app.data.local.dao.ConversationDao
import com.hyperchat.app.data.local.dao.MessageDao
import com.hyperchat.app.data.local.dao.SuggestionDao
import com.hyperchat.app.data.remote.MiniMaxApiClient
import com.hyperchat.app.data.remote.MiniMaxApiService
import com.hyperchat.app.data.repository.*
import com.hyperchat.app.domain.repository.*
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): HyperChatDatabase {
        return Room.databaseBuilder(
            context,
            HyperChatDatabase::class.java,
            "hyperchat_database"
        ).build()
    }

    @Provides
    fun provideConversationDao(database: HyperChatDatabase): ConversationDao {
        return database.conversationDao()
    }

    @Provides
    fun provideMessageDao(database: HyperChatDatabase): MessageDao {
        return database.messageDao()
    }

    @Provides
    fun provideSuggestionDao(database: HyperChatDatabase): SuggestionDao {
        return database.suggestionDao()
    }
}

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideMiniMaxApiService(): MiniMaxApiService {
        return MiniMaxApiClient.create()
    }

    @Provides
    @Singleton
    fun provideSettingsManager(@ApplicationContext context: Context): SettingsManager {
        return SettingsManager(context)
    }
}

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindConversationRepository(
        impl: ConversationRepositoryImpl
    ): ConversationRepository

    @Binds
    @Singleton
    abstract fun bindMessageRepository(
        impl: MessageRepositoryImpl
    ): MessageRepository

    @Binds
    @Singleton
    abstract fun bindSuggestionRepository(
        impl: SuggestionRepositoryImpl
    ): SuggestionRepository

    @Binds
    @Singleton
    abstract fun bindAIChatRepository(
        impl: MiniMaxAIChatRepository
    ): AIChatRepository

    @Binds
    @Singleton
    abstract fun bindEQLibraryRepository(
        impl: MockEQLibraryRepository
    ): EQLibraryRepository
}
