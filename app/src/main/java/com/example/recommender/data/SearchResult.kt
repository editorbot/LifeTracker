package com.example.recommender.data


// A unified model so the adapter doesn't care about TMDB vs Books
data class SearchResult(
    val externalId: String,
    val title: String,
    val authorOrDirector: String?,
    val summary: String?,
    val genres: List<String>,   // TMDB gives genre_ids → we map to names
    // Google Books gives categories directly
    val posterUrl: String?,
    val type: MediaType
)