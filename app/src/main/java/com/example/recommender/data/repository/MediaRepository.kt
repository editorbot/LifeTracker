package com.example.recommender.data.repository



import com.example.recommender.data.MediaItem
import com.example.recommender.data.MediaType
import com.example.recommender.data.SearchResult
import com.example.recommender.data.local.GenreScoreManager
import com.example.recommender.data.local.MediaDao
import com.example.recommender.data.local.MediaEntity
import com.example.recommender.data.remote.aws.AwsApiService
import com.example.recommender.data.remote.aws.dto.AwsRecommendRequest
import com.example.recommender.data.remote.aws.dto.AwsRecommendation
import com.example.recommender.data.remote.books.BooksApiService
import com.example.recommender.data.remote.tmdb.TmdbApiService
import com.example.recommender.data.remote.tmdb.TmdbGenreMapper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class MediaRepository(
    private val dao: MediaDao,
    private val tmdbApi: TmdbApiService,
    private val booksApi: BooksApiService,
    private val genreScoreManager: GenreScoreManager,   // ← add this
    private val awsApi: AwsApiService              // ← add this
) {
    companion object {
        const val TMDB_API_KEY = "b1762c923486d421b25641d1f25f6fc3"
    }

    // ── Local ────────────────────────────────────────────────────────

    fun getAllSavedItems(): Flow<List<MediaItem>> {
        return dao.getAllItems().map { entities -> entities.map { it.toMediaItem() } }
    }

    suspend fun saveItem(result: SearchResult, rating: Float) {
        dao.insertItem(
            MediaEntity(
                title = result.title,
                genres = result.genres.joinToString(","),
                rating = rating,
                type = result.type.name,
                posterPath = result.posterUrl,
                authorOrDirector = result.authorOrDirector,
                summary = result.summary
            )
        )
        // 2. Silently update genre scores in DataStore
        // Higher rated items influence scores more
        val weight = when {
            rating >= 4.5f -> 3   // loved it  → count genres 3x
            rating >= 3.0f -> 2   // liked it   → count genres 2x
            else           -> 1   // meh        → count genres 1x
        }

        // Repeat genre list by weight before passing to manager
        val weightedGenres = List(weight) { result.genres }.flatten()
        genreScoreManager.addGenres(weightedGenres, result.type.name)
    }

    // ── Search via TMDB ──────────────────────────────────────────────

    suspend fun searchMovies(query: String): List<SearchResult> {
        val response = tmdbApi.searchMovies(TMDB_API_KEY, query)
        return response.results.map { movie ->
            SearchResult(
                externalId = movie.id.toString(),
                title = movie.title,
                authorOrDirector = null,
                summary = movie.overview,
                genres = TmdbGenreMapper.mapIds(movie.genreIds),
                posterUrl = movie.posterPath?.let {
                    "https://image.tmdb.org/t/p/w342$it"
                },
                type = MediaType.MOVIE
            )
        }
    }

    // ── Search via Google Books ──────────────────────────────────────

    suspend fun searchBooks(query: String): List<SearchResult> {
        val response = booksApi.searchBooks(query)
        return response.items?.map { book ->
            SearchResult(
                externalId = book.id,
                title = book.volumeInfo.title,
                authorOrDirector = book.volumeInfo.authors?.joinToString(", "),
                summary = book.volumeInfo.description,
                genres = book.volumeInfo.categories ?: emptyList(),
                posterUrl = book.volumeInfo.imageLinks?.thumbnail
                    ?.replace("http://", "https://"),  // Google Books returns http
                type = MediaType.BOOK
            )
        } ?: emptyList()
    }

    // ── Genre profile for AWS recommendations ───────────────────────
    // Phase 7 will call this to send to your Lambda

//    suspend fun getTopGenresForType(type: MediaType): List<String> {
//        val allGenreStrings = dao.getAllGenresForType(type.name)
//        val genreCount = mutableMapOf<String, Int>()
//
//        allGenreStrings.forEach { genreString ->
//            genreString.split(",")
//                .filter { it.isNotBlank() }
//                .forEach { genre ->
//                    genreCount[genre] = (genreCount[genre] ?: 0) + 1
//                }
//        }
//
//        // Return top 3 genres sorted by frequency
//        return genreCount.entries
//            .sortedByDescending { it.value }
//            .take(3)
//            .map { it.key }
//    }

    // ── Mappers ──────────────────────────────────────────────────────

    private fun MediaEntity.toMediaItem() = MediaItem(
        id = id,
        title = title,
        genres = genres.split(",").filter { it.isNotBlank() },
        rating = rating,
        type = if (type == "MOVIE") MediaType.MOVIE else MediaType.BOOK,
        posterUrl = posterPath ?: ""
    )
    suspend fun getItemById(id: Int): MediaItem? {
        return dao.getItemById(id)?.toMediaItem()
    }
    // ── For You tab uses this ─────────────────────────────────────────
    suspend fun getTopGenresForType(type: String, limit: Int = 3): List<String> {
        return genreScoreManager.getTopGenres(type, limit)
    }

    // ── For observing scores live in UI if needed ─────────────────────
    fun getMovieGenreScoresFlow(): Flow<Map<String, Int>> =
        genreScoreManager.getMovieGenreScores()

    fun getBookGenreScoresFlow(): Flow<Map<String, Int>> =
        genreScoreManager.getBookGenreScores()

    // ── AWS Recommendations ───────────────────────────────────────────

    suspend fun getMovieRecommendations(
        genres: List<String>,
        token: String
    ): List<AwsRecommendation> {
        val preferences = genres.joinToString(" ")
        return awsApi.getMovieRecommendations(
            token = "Bearer $token",
            body = AwsRecommendRequest(preferences)
        ).recommendations
    }

    suspend fun getBookRecommendations(
        genres: List<String>,
        token: String
    ): List<AwsRecommendation> {
        val preferences = genres.joinToString(" ")
        return awsApi.getBookRecommendations(
            token = "Bearer $token",
            body = AwsRecommendRequest(preferences)
        ).recommendations
    }
}