package com.example.recommender.data.remote.tmdb


object TmdbGenreMapper {

    // TMDB's official genre ID → name mapping
    private val genreMap = mapOf(
        28 to "Action",
        12 to "Adventure",
        16 to "Animation",
        35 to "Comedy",
        80 to "Crime",
        99 to "Documentary",
        18 to "Drama",
        10751 to "Family",
        14 to "Fantasy",
        36 to "History",
        27 to "Horror",
        10402 to "Music",
        9648 to "Mystery",
        10749 to "Romance",
        878 to "Science Fiction",
        53 to "Thriller",
        10752 to "War",
        37 to "Western"
    )

    fun mapIds(ids: List<Int>?): List<String> {
        return ids?.mapNotNull { genreMap[it] } ?: emptyList()
    }
}