package com.example.recommender.data


data class MediaItem(
    val id: Int,
    val title: String,
    val genres: List<String>,
    val rating: Float,
    val type: MediaType,       // MOVIE or BOOK
    val posterUrl: String = ""
)

enum class MediaType {
    MOVIE, BOOK
}