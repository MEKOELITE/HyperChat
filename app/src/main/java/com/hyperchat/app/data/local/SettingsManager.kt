package com.hyperchat.app.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

@Singleton
class SettingsManager @Inject constructor(
    private val context: Context
) {
    private val apiKeyKey = stringPreferencesKey("minimax_api_key")
    private val apiGroupKey = stringPreferencesKey("minimax_api_group")

    val apiKey: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[apiKeyKey] ?: ""
    }

    val apiGroup: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[apiGroupKey] ?: ""
    }

    suspend fun setApiKey(key: String) {
        context.dataStore.edit { preferences ->
            preferences[apiKeyKey] = key
        }
    }

    suspend fun setApiGroup(group: String) {
        context.dataStore.edit { preferences ->
            preferences[apiGroupKey] = group
        }
    }
}
