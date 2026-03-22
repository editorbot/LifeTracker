package com.example.lifetracker.data.repository

import android.util.Log
import com.example.lifetracker.data.db.CategoryStat
import com.example.lifetracker.data.db.HabitDao
import com.example.lifetracker.data.db.HabitEntity
import com.example.lifetracker.data.sync.SyncManager
import com.example.lifetracker.model.Category
import com.example.lifetracker.model.Priority
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import javax.inject.Inject

// repository/HabitRepository.kt
// repository/HabitRepository.kt
class HabitRepository @Inject constructor(
    private val dao: HabitDao,

    private val syncManager: SyncManager    // ← inject SyncManager
) {
    private val currentUserId= MutableStateFlow("")

    // Own scope — safer than GlobalScope, cancelled when needed
    private val repositoryScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    fun setUserId(userId: String) {
        Log.d("RepoDebug", "Setting userId: $userId")
        currentUserId.value = userId
        // Check what's in Room right now
        repositoryScope.launch {
            val count = dao.getAllHabits(userId).first().size
            Log.d("RepoDebug", "Habits in Room for this user: $count")
        }
    }

    fun getHabitsForDate(date: String): Flow<List<HabitEntity>> =
        currentUserId.flatMapLatest { userId ->
            if (userId.isEmpty()) flowOf(emptyList())
            else dao.getHabitsByDate(userId, date)
        }


    val allHabits: Flow<List<HabitEntity>>
        get() = currentUserId.flatMapLatest { userId ->
            Log.d("RepoDebug", "allHabits flatMapLatest triggered with userId: $userId")
            if (userId.isEmpty()) flowOf(emptyList())
            else dao.getAllHabits(userId)
        }

    val completedCount: Flow<Int>
        get() = currentUserId.flatMapLatest { userId ->
            if (userId.isEmpty()) flowOf(0)
            else dao.getCompletedCount(userId)
        }

    fun getCategoryStats(startDate: String, endDate: String): Flow<List<CategoryStat>> =
        currentUserId.flatMapLatest { userId ->
            Log.d("RepoDebug", "getCategoryStats userId=$userId start=$startDate end=$endDate")
            if (userId.isEmpty()) flowOf(emptyList())
            else dao.getCategoryStats(userId, startDate, endDate)
        }

    fun getHabitsForDateRange(startDate: String, endDate: String): Flow<List<HabitEntity>> =
        currentUserId.flatMapLatest { userId ->
            Log.d("RepoDebug", "getHabitsForDateRange userId=$userId start=$startDate end=$endDate")
            if (userId.isEmpty()) flowOf(emptyList())
            else dao.getHabitsForDateRange(userId, startDate, endDate)
        }


    suspend fun addHabit(entity: HabitEntity) {
        // 1. Save locally first — app feels instant
        val localEntity = entity.copy(userId = currentUserId.value)
        dao.insertHabit(localEntity)

        // 2. Push to DynamoDB in background
        syncManager.createHabit(
            habit = localEntity,
            onSuccess = { remoteId ->
                // Store remoteId so we can update/delete later
                repositoryScope.launch {
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
        val isNowCompleted = !habit.isCompleted
        // 1. Update locally
        val updated = habit.copy(
            isCompleted = !habit.isCompleted,
            completedAt = if (isNowCompleted) System.currentTimeMillis() else null
            // ↑ stamp time when checked, clear it when unchecked
            )
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
                                    userId = currentUserId.value,
                                    title = remote.title,
                                    priority = Priority.valueOf(remote.priority),
                                    category = Category.valueOf(remote.category),
                                    isCompleted = remote.isCompleted,
                                    date = remote.date,
                                    startTime = remote.startTime?.toLongOrNull(),
                                    endTime = remote.endTime?.toLongOrNull(),
                                    completedAt = remote.completedAt?.toLongOrNull(),  // ← Add this
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
        dao.deleteAllHabitsForUser(currentUserId.value)
    }
}