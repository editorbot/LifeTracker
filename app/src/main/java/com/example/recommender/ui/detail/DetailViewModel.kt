package com.example.recommender.ui.detail


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recommender.data.MediaItem
import com.example.recommender.data.repository.MediaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class DetailViewModel(private val repository: MediaRepository) : ViewModel() {

    private val _item = MutableStateFlow<MediaItem?>(null)
    val item: StateFlow<MediaItem?> = _item

    fun loadItem(id: Int) {
        viewModelScope.launch {
            _item.value = repository.getItemById(id)
        }
    }
}