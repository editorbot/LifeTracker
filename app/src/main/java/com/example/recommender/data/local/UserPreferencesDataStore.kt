package com.example.recommender.data.local



import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Extension on Context — single DataStore instance per app
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_prefs")

class UserPreferencesDataStore(private val context: Context) {

    companion object {
        val SELECTED_MEDIA_TYPE = stringPreferencesKey("selected_media_type")
    }

    // Emits "MOVIE" or "BOOK" — survives app restarts
    val selectedMediaType: Flow<String> = context.dataStore.data
        .map { preferences ->
            preferences[SELECTED_MEDIA_TYPE] ?: "MOVIE"  // default to movies
        }

    suspend fun saveSelectedMediaType(type: String) {
        context.dataStore.edit { preferences ->
            preferences[SELECTED_MEDIA_TYPE] = type
        }
    }
}