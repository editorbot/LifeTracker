package com.example.lifetracker.data.repository

import android.util.Log
import com.example.lifetracker.data.db.HabitDao
import com.example.lifetracker.data.db.HabitEntity
import com.example.lifetracker.data.sync.SyncManager
import com.example.lifetracker.model.Category
import com.example.lifetracker.model.Priority
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

// repository/HabitRepository.kt
// repository/HabitRepository.kt
class HabitRepository @Inject constructor(
    private val dao: HabitDao,

    private val syncManager: SyncManager    // ← inject SyncManager
) {
    private var currentUserId: String = ""

    // Own scope — safer than GlobalScope, cancelled when needed
    private val repositoryScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    fun setUserId(userId: String) {
        currentUserId = userId
    }

    fun getHabitsForDate(date: String): Flow<List<HabitEntity>> =
        dao.getHabitsByDate(currentUserId, date)

    val allHabits: Flow<List<HabitEntity>>
        get() = dao.getAllHabits(currentUserId)

    val completedCount: Flow<Int>
        get() = dao.getCompletedCount(currentUserId)

    fun getCategoryStats(startDate: String, endDate: String) =
        dao.getCategoryStats(currentUserId, startDate, endDate)

    fun getHabitsForDateRange(startDate: String, endDate: String) =
        dao.getHabitsForDateRange(currentUserId, startDate, endDate)

    suspend fun addHabit(entity: HabitEntity) {
        // 1. Save locally first — app feels instant
        val localEntity = entity.copy(userId = currentUserId)
        dao.insertHabit(localEntity)

        // 2. Push to DynamoDB in background
        syncManager.createHabit(
            habit = localEntity,
            onSuccess = { remoteId ->
                // Store remoteId so we can update/delete later
                kotlinx.coroutines.GlobalScope.launch {
                    dao.updateRemoteId(localEntity.id, remoteId)
                }
            },
            onFailure = { e ->
                Log.e("LifeTracker", "Sync failed for habit: ${e.message}")
                // Habit still saved locally — sync can retry later
            }
        )
    }

    suspend fun toggleHabit(habit: HabitEntity) {
        // 1. Update locally
        val updated = habit.copy(isCompleted = !habit.isCompleted)
        dao.updateHabit(updated)

        // 2. Sync to DynamoDB
        habit.remoteId?.let { remoteId ->
            syncManager.updateHabit(
                habit = updated,
                onSuccess = {},
                onFailure = { e ->
                    Log.e("LifeTracker", "Toggle sync failed: ${e.message}")
                }
            )
        }
    }

    suspend fun deleteHabit(habit: HabitEntity) {
        // 1. Delete locally
        dao.deleteHabit(habit)

        // 2. Delete from DynamoDB
        habit.remoteId?.let { remoteId ->
            syncManager.deleteHabit(
                remoteId = remoteId,
                onSuccess = {},
                onFailure = { e ->
                    Log.e("LifeTracker", "Delete sync failed: ${e.message}")
                }
            )
        }
    }

    // Called on login — pull remote habits into local Room
    suspend fun syncFromRemote(onComplete: () -> Unit) {
        syncManager.fetchHabits(
            onSuccess = { remoteHabits ->
                repositoryScope.launch {
                    remoteHabits.forEach { remote ->
                        // Only insert if not already in local DB
                        val exists = dao.getHabitByRemoteId(remote.id) != null
                        if (!exists) {
                            dao.insertHabit(
                                HabitEntity(
                                    remoteId = remote.id,
                                    userId = currentUserId,
                                    title = remote.title,
                                    priority = Priority.valueOf(remote.priority),
                                    category = Category.valueOf(remote.category),
                                    isCompleted = remote.isCompleted,
                                    date = remote.date,
                                    startTime = remote.startTime?.toLongOrNull(),
                                    endTime = remote.endTime?.toLongOrNull(),
                                    createdAt = remote.createdAt?.toLongOrNull()
                                        ?: System.currentTimeMillis()
                                )
                            )
                        }
                    }
                    onComplete()
                }
            },
            onFailure = { e ->
                Log.e("LifeTracker", "Remote sync failed: ${e.message}")
                onComplete() // Continue with local data
            }
        )
    }

    suspend fun clearUserData() {
        dao.deleteAllHabitsForUser(currentUserId)
    }
}