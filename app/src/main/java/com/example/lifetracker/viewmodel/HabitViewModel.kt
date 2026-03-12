package com.example.lifetracker.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lifetracker.data.db.HabitDatabase
import com.example.lifetracker.data.db.HabitEntity
import com.example.lifetracker.data.db.getCurrentDate
import com.example.lifetracker.data.repository.HabitRepository
import com.example.lifetracker.model.Category
import com.example.lifetracker.model.Priority
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.collections.mutableListOf

// viewmodel/HabitViewModel.kt
@HiltViewModel
class HabitViewModel @Inject constructor(
    private val repository: HabitRepository
) : ViewModel() {

    // Today's date for timeline
    private val _selectedDate = MutableStateFlow(getCurrentDate())
    val selectedDate: StateFlow<String> = _selectedDate

    // Habits for selected date (timeline view)
    val habitsForToday: StateFlow<List<HabitEntity>> = _selectedDate
        .flatMapLatest { date ->
            repository.getHabitsForDate(date)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val completedCount: StateFlow<Int> = repository.completedCount
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0
        )

    fun addHabit(
        title: String,
        priority: Priority = Priority.MEDIUM,
        category: Category = Category.PERSONAL,
        startTime: Long? = null,
        endTime: Long? = null
    ) {
        viewModelScope.launch {
            repository.addHabit(
                HabitEntity(
                    title = title,
                    priority = priority,
                    category = category,
                    startTime = startTime,
                    endTime = endTime,
                    date = _selectedDate.value
                )
            )
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

    fun changeDate(date: String) {
        _selectedDate.value = date
    }
}