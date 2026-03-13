package com.example.recommender.ui.foryou


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recommender.auth.AmplifyAuthManager
import com.example.recommender.data.MediaType
import com.example.recommender.data.remote.aws.dto.AwsRecommendation
import com.example.recommender.data.repository.MediaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

// Just the genre intelligence part for now
// Phase 7 adds the actual AWS call
sealed class RecommendUiState {
    object Idle : RecommendUiState()
    object Loading : RecommendUiState()
    data class Success(val results: List<AwsRecommendation>) : RecommendUiState()
    data class Error(val message: String) : RecommendUiState()
    object NotEnoughData : RecommendUiState()  // user hasn't saved enough titles yet
}

class ForYouViewModel(private val repository: MediaRepository) : ViewModel() {

    // ── Genre profile ─────────────────────────────────────────────────
    private val _topMovieGenres = MutableStateFlow<List<String>>(emptyList())
    val topMovieGenres: StateFlow<List<String>> = _topMovieGenres

    private val _topBookGenres = MutableStateFlow<List<String>>(emptyList())
    val topBookGenres: StateFlow<List<String>> = _topBookGenres

    // ── Manual chip selection ─────────────────────────────────────────
    private val _selectedMovieGenres = MutableStateFlow<Set<String>>(emptySet())
    val selectedMovieGenres: StateFlow<Set<String>> = _selectedMovieGenres

    private val _selectedBookGenres = MutableStateFlow<Set<String>>(emptySet())
    val selectedBookGenres: StateFlow<Set<String>> = _selectedBookGenres

    // ── Recommendation results ────────────────────────────────────────
    private val _movieRecsState = MutableStateFlow<RecommendUiState>(RecommendUiState.Idle)
    val movieRecsState: StateFlow<RecommendUiState> = _movieRecsState

    private val _bookRecsState = MutableStateFlow<RecommendUiState>(RecommendUiState.Idle)
    val bookRecsState: StateFlow<RecommendUiState> = _bookRecsState

    // Current tab — MOVIE or BOOK
    private val _selectedType = MutableStateFlow(MediaType.MOVIE)
    val selectedType: StateFlow<MediaType> = _selectedType

    init {
        loadGenreProfiles()
    }

    private fun loadGenreProfiles() {
        viewModelScope.launch {
            val movieGenres = repository.getTopGenresForType("MOVIE")
            val bookGenres  = repository.getTopGenresForType("BOOK")

            _topMovieGenres.value    = movieGenres
            _topBookGenres.value     = bookGenres

            // Pre-select top genres in chips
            _selectedMovieGenres.value = movieGenres.toSet()
            _selectedBookGenres.value  = bookGenres.toSet()

            // Auto-fetch as soon as we have genre data
            if (movieGenres.isNotEmpty()) fetchRecommendations(MediaType.MOVIE)
            if (bookGenres.isNotEmpty())  fetchRecommendations(MediaType.BOOK)
        }
    }

    fun setSelectedType(type: MediaType) {
        _selectedType.value = type
    }

    fun toggleMovieGenre(genre: String) {
        val current = _selectedMovieGenres.value.toMutableSet()
        if (current.contains(genre)) current.remove(genre) else current.add(genre)
        _selectedMovieGenres.value = current
    }

    fun toggleBookGenre(genre: String) {
        val current = _selectedBookGenres.value.toMutableSet()
        if (current.contains(genre)) current.remove(genre) else current.add(genre)
        _selectedBookGenres.value = current
    }

    fun fetchRecommendations(type: MediaType) {
        viewModelScope.launch {
            val genres = if (type == MediaType.MOVIE)
                _selectedMovieGenres.value.toList()
            else
                _selectedBookGenres.value.toList()

            if (genres.isEmpty()) {
                if (type == MediaType.MOVIE)
                    _movieRecsState.value = RecommendUiState.NotEnoughData
                else
                    _bookRecsState.value = RecommendUiState.NotEnoughData
                return@launch
            }

            // Set loading
            if (type == MediaType.MOVIE)
                _movieRecsState.value = RecommendUiState.Loading
            else
                _bookRecsState.value = RecommendUiState.Loading

            try {
                // Get fresh Cognito ID token
                val token = AmplifyAuthManager.getIdToken()

                val results = if (type == MediaType.MOVIE) {
                    repository.getMovieRecommendations(genres, token)
                } else {
                    repository.getBookRecommendations(genres, token)
                }

                val state = if (results.isEmpty())
                    RecommendUiState.NotEnoughData
                else
                    RecommendUiState.Success(results)

                if (type == MediaType.MOVIE) _movieRecsState.value = state
                else _bookRecsState.value = state

            } catch (e: Exception) {
                val error = RecommendUiState.Error(
                    e.message ?: "Failed to get recommendations"
                )
                if (type == MediaType.MOVIE) _movieRecsState.value = error
                else _bookRecsState.value = error
            }
        }
    }
}