package com.example.lifetracker.data.repository

import com.example.lifetracker.data.db.HabitDao
import com.example.lifetracker.data.db.HabitEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

// repository/HabitRepository.kt
class HabitRepository @Inject constructor(
    private val dao: HabitDao
) {
    // Today's habits for timeline view
    fun getHabitsForDate(date: String): Flow<List<HabitEntity>> =
        dao.getHabitsByDate(date)

    // All habits
    val allHabits: Flow<List<HabitEntity>> = dao.getAllHabits()

    val completedCount: Flow<Int> = dao.getCompletedCount()

    // Category breakdown for pie chart
    fun getCategoryStats(startDate: String, endDate: String) =
        dao.getCategoryStats(startDate, endDate)

    // Weekly summary
    fun getWeeklyCompleted(startDate: String, endDate: String) =
        dao.getCompletedCountForWeek(startDate, endDate)

    suspend fun addHabit(entity: HabitEntity) {
        dao.insertHabit(entity)
    }

    suspend fun toggleHabit(habit: HabitEntity) {
        dao.updateHabit(habit.copy(isCompleted = !habit.isCompleted))
    }

    suspend fun deleteHabit(habit: HabitEntity) {
        dao.deleteHabit(habit)
    }
    // repository/HabitRepository.kt — add this function
    fun getHabitsForDateRange(startDate: String, endDate: String): Flow<List<HabitEntity>> =
        dao.getHabitsForDateRange(startDate, endDate)
}