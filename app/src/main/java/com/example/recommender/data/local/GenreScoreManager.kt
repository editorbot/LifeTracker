package com.example.recommender.data.local


import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class GenreScoreManager(private val context: Context) {

    companion object {
        // Separate keys for movies and books
        val MOVIE_GENRE_SCORES = stringPreferencesKey("movie_genre_scores")
        val BOOK_GENRE_SCORES  = stringPreferencesKey("book_genre_scores")
    }

    private val gson = Gson()

    // ── Read ─────────────────────────────────────────────────────────

    fun getMovieGenreScores(): Flow<Map<String, Int>> = context.dataStore.data
        .map { prefs ->
            val json = prefs[MOVIE_GENRE_SCORES] ?: return@map emptyMap()
            deserializeScores(json)
        }

    fun getBookGenreScores(): Flow<Map<String, Int>> = context.dataStore.data
        .map { prefs ->
            val json = prefs[BOOK_GENRE_SCORES] ?: return@map emptyMap()
            deserializeScores(json)
        }

    // ── Write — called every time user saves a title ──────────────────

    suspend fun addGenres(genres: List<String>, type: String) {
        if (genres.isEmpty()) return

        val key = if (type == "MOVIE") MOVIE_GENRE_SCORES else BOOK_GENRE_SCORES

        context.dataStore.edit { prefs ->
            val existing = deserializeScores(prefs[key] ?: "")
            val updated  = existing.toMutableMap()

            genres.forEach { genre ->
                val trimmed = genre.trim()
                if (trimmed.isNotBlank()) {
                    updated[trimmed] = (updated[trimmed] ?: 0) + 1
                }
            }

            prefs[key] = serializeScores(updated)
        }
    }

    // ── Query — used by ForYou tab to build AWS request ───────────────

    suspend fun getTopGenres(type: String, limit: Int = 3): List<String> {
        val key = if (type == "MOVIE") MOVIE_GENRE_SCORES else BOOK_GENRE_SCORES

        var scores: Map<String, Int> = emptyMap()
        context.dataStore.data.collect { prefs ->
            scores = deserializeScores(prefs[key] ?: "")
            return@collect   // only need first emission
        }

        return scores.entries
            .sortedByDescending { it.value }
            .take(limit)
            .map { it.key }
    }

    // ── Reset — useful for testing or user clearing history ───────────

    suspend fun clearScores(type: String) {
        val key = if (type == "MOVIE") MOVIE_GENRE_SCORES else BOOK_GENRE_SCORES
        context.dataStore.edit { prefs -> prefs.remove(key) }
    }

    // ── Serialization ─────────────────────────────────────────────────

    private fun serializeScores(scores: Map<String, Int>): String {
        return gson.toJson(scores)
    }

    private fun deserializeScores(json: String): Map<String, Int> {
        if (json.isBlank()) return emptyMap()
        return try {
            val type = object : TypeToken<Map<String, Int>>() {}.type
            gson.fromJson(json, type) ?: emptyMap()
        } catch (e: Exception) {
            emptyMap()   // corrupted data — start fresh
        }
    }
}