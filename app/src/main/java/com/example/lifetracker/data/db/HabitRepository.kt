package com.example.lifetracker.data.db

import kotlinx.coroutines.flow.Flow

// repository/HabitRepository.kt
class HabitRepository(private val dao: HabitDao) {

    // Exposed directly as Flow — ViewModel just collects this
    val allHabits: Flow<List<HabitEntity>> = dao.getAllHabits()
    val completedCount: Flow<Int> = dao.getCompletedCount()

    suspend fun addHabit(name: String) {
        dao.insertHabit(HabitEntity(name = name))
    }

    suspend fun toggleHabit(habit: HabitEntity) {
        dao.updateHabit(habit.copy(isCompleted = !habit.isCompleted))
    }

    suspend fun deleteHabit(habit: HabitEntity) {
        dao.deleteHabit(habit)
    }
}