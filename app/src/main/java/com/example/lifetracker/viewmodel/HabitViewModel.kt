package com.example.lifetracker.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lifetracker.data.db.HabitDatabase
import com.example.lifetracker.data.db.HabitEntity
import com.example.lifetracker.data.db.HabitRepository
import com.example.lifetracker.model.Habit
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.collections.mutableListOf

// viewmodel/HabitViewModel.kt
class HabitViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: HabitRepository

    // Collect Flow as StateFlow for UI
    val habits: StateFlow<List<HabitEntity>>

    init {
        val dao = HabitDatabase.getDatabase(application).habitDao()
        repository = HabitRepository(dao)

        habits = repository.allHabits
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )
    }

    fun addHabit(name: String) {
        viewModelScope.launch {
            repository.addHabit(name)
        }
    }

    fun toggleHabit(habit: HabitEntity) {
        viewModelScope.launch {
            repository.toggleHabit(habit)
        }
    }

    fun deleteHabit(habit: HabitEntity) {
        viewModelScope.launch {
            repository.deleteHabit(habit)
        }
    }
}