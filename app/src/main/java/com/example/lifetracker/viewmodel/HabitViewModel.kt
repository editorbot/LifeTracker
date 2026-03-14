package com.example.lifetracker.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lifetracker.data.db.CategoryStat
import com.example.lifetracker.data.db.HabitDatabase
import com.example.lifetracker.data.db.HabitEntity
import com.example.lifetracker.data.db.getCurrentDate
import com.example.lifetracker.data.repository.HabitRepository
import com.example.lifetracker.model.Category
import com.example.lifetracker.model.Priority
import com.example.recommender.auth.AuthManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject
import kotlin.collections.mutableListOf

// viewmodel/HabitViewModel.kt
@HiltViewModel
class HabitViewModel @Inject constructor(
    private val repository: HabitRepository,
    private val authManager: AuthManager          // ← inject AuthManager
) : ViewModel() {

    // Today's date for timeline
    private val _selectedDate = MutableStateFlow(getCurrentDate())
    val selectedDate: StateFlow<String> = _selectedDate

    // Called once after login — seeds userId into repository
    fun initializeUser(onReady: () -> Unit) {
        authManager.getCurrentUserId(
            onSuccess = { userId ->
                repository.setUserId(userId)
                onReady()
            },
            onFailure = {
                // Handle unauthenticated state
            }
        )
    }
    // Habits for selected date (timeline view)
    val habitsForToday: StateFlow<List<HabitEntity>> = _selectedDate
        .flatMapLatest { date -> repository.getHabitsForDate(date) }
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
    // Category stats for pie chart
    val categoryStats: StateFlow<List<CategoryStat>> = flow {
        val (start, end) = getWeekDateRange()
        emitAll(repository.getCategoryStats(start, end))
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // Weekly completion for bar chart
    val weeklyHabits: StateFlow<List<HabitEntity>> = flow {
        val (start, end) = getWeekDateRange()
        emitAll(repository.getHabitsForDateRange(start, end))
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // Streak — consecutive days with at least one completed habit
    val currentStreak: StateFlow<Int> = repository.allHabits
        .map { habits -> calculateStreak(habits) }
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
    // Called from SettingsFragment logout button
    fun signOut(onComplete: () -> Unit) {
        viewModelScope.launch {
            // Don't wipe local data — just sign out
            // Data stays locally, scoped by userId
            // Next time same user logs in, their data is still there
            authManager.signOut {
                onComplete()
            }
        }
    }
    // Weekly date range — Mon to Sun of current week
    private fun getWeekDateRange(): Pair<String, String> {
        val calendar = Calendar.getInstance()
        // Roll back to Monday
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
        val startDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            .format(calendar.time)
        // Roll forward to Sunday
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY)
        calendar.add(Calendar.WEEK_OF_YEAR, 1)
        val endDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            .format(calendar.time)
        return Pair(startDate, endDate)
    }



    private fun calculateStreak(habits: List<HabitEntity>): Int {
        if (habits.isEmpty()) return 0

        // Group completed habits by date
        val completedDates = habits
            .filter { it.isCompleted }
            .map { it.date }
            .toSortedSet()
            .toList()
            .sortedDescending()

        if (completedDates.isEmpty()) return 0

        var streak = 0
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val calendar = Calendar.getInstance()

        for (dateStr in completedDates) {
            val expectedDate = sdf.format(calendar.time)
            if (dateStr == expectedDate) {
                streak++
                calendar.add(Calendar.DAY_OF_YEAR, -1)
            } else break
        }
        return streak
    }
}