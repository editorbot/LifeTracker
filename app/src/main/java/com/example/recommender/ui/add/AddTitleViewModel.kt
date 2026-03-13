package com.example.recommender.ui.add



import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recommender.data.MediaType
import com.example.recommender.data.SearchResult
import com.example.recommender.data.local.UserPreferencesDataStore
import com.example.recommender.data.repository.MediaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

// Sealed class = all possible UI states in one place
sealed class SearchUiState {
    object Idle : SearchUiState()
    object Loading : SearchUiState()
    data class Success(val results: List<SearchResult>) : SearchUiState()
    data class Error(val message: String) : SearchUiState()
}

sealed class SaveUiState {
    object Idle : SaveUiState()
    object Saved : SaveUiState()
    data class Error(val message: String) : SaveUiState()
}

class AddTitleViewModel(
    private val repository: MediaRepository,
    private val dataStore: UserPreferencesDataStore
) : ViewModel() {

    private val _searchState = MutableStateFlow<SearchUiState>(SearchUiState.Idle)
    val searchState: StateFlow<SearchUiState> = _searchState

    private val _saveState = MutableStateFlow<SaveUiState>(SaveUiState.Idle)
    val saveState: StateFlow<SaveUiState> = _saveState

    // This now persists across app restarts via DataStore
    private val _selectedType = MutableStateFlow(MediaType.MOVIE)
    val selectedType: StateFlow<MediaType> = _selectedType

    init {
        // Restore last selected type from DataStore on launch
        viewModelScope.launch {
            dataStore.selectedMediaType.collect { saved ->
                _selectedType.value =
                    if (saved == "BOOK") MediaType.BOOK else MediaType.MOVIE
            }
        }
    }

    fun setSelectedType(type: MediaType) {
        _selectedType.value = type
        viewModelScope.launch {
            dataStore.saveSelectedMediaType(type.name)
        }
    }

    fun search(query: String) {
        if (query.isBlank()) return
        viewModelScope.launch {
            _searchState.value = SearchUiState.Loading
            try {
                val results = if (_selectedType.value == MediaType.MOVIE) {
                    repository.searchMovies(query)
                } else {
                    repository.searchBooks(query)
                }
                _searchState.value = SearchUiState.Success(results)
            } catch (e: Exception) {
                _searchState.value = SearchUiState.Error(
                    e.message ?: "Search failed. Check your connection."
                )
            }
        }
    }

    fun saveItem(result: SearchResult, rating: Float) {
        viewModelScope.launch {
            try {
                repository.saveItem(result, rating)
                _saveState.value = SaveUiState.Saved
            } catch (e: Exception) {
                _saveState.value = SaveUiState.Error(e.message ?: "Save failed")
            }
        }
    }

    fun resetSaveState() { _saveState.value = SaveUiState.Idle }
}