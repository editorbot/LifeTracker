package com.example.recommender.ui.home



import androidx.lifecycle.ViewModel
import com.example.recommender.data.MediaItem
import com.example.recommender.data.repository.MediaRepository
import kotlinx.coroutines.flow.Flow

class HomeViewModel(private val repository: MediaRepository) : ViewModel() {

    // Fragment just observes this — never touches DB directly
    val savedItems: Flow<List<MediaItem>> = repository.getAllSavedItems()
}