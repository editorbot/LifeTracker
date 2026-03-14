package com.example.lifetracker.data.repository

import com.example.lifetracker.data.db.HabitDao
import com.example.lifetracker.data.db.HabitEntity
import com.example.recommender.auth.AuthManager
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

// repository/HabitRepository.kt
class HabitRepository @Inject constructor(
    private val dao: HabitDao,
    private val authManager: AuthManager          // ← inject AuthManager
) {
    // Get userId once and hold it for this session
    private var currentUserId: String = ""

    fun setUserId(userId: String) {
        currentUserId = userId
    }
    // Today's habits for timeline view
    fun getHabitsForDate(date: String): Flow<List<HabitEntity>> =
        dao.getHabitsByDate(currentUserId,date)

    // All habits
    val allHabits: Flow<List<HabitEntity>> = dao.getAllHabits(currentUserId)

    val completedCount: Flow<Int> = dao.getCompletedCount(currentUserId)

    // Category breakdown for pie chart
    fun getCategoryStats(startDate: String, endDate: String) =
        dao.getCategoryStats(currentUserId,startDate, endDate)

    fun getHabitsForDateRange(startDate: String, endDate: String) =
        dao.getHabitsForDateRange(currentUserId, startDate, endDate)

    suspend fun addHabit(entity: HabitEntity) {
        // Always stamp userId before inserting
        dao.insertHabit(entity.copy(userId = currentUserId))
        dao.insertHabit(entity)
    }

    suspend fun toggleHabit(habit: HabitEntity) {
        dao.updateHabit(habit.copy(isCompleted = !habit.isCompleted))
    }

    suspend fun deleteHabit(habit: HabitEntity) {
        dao.deleteHabit(habit)
    }
    suspend fun clearUserData() {
        dao.deleteAllHabitsForUser(currentUserId)
    }
}