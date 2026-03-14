package com.example.lifetracker.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.lifetracker.model.Category
import kotlinx.coroutines.flow.Flow

@Dao
interface HabitDao {

    @Query("SELECT * FROM habits WHERE userId = :userId AND date = :date ORDER BY startTime ASC")
    fun getHabitsByDate(userId: String, date: String): Flow<List<HabitEntity>>

    @Query("SELECT * FROM habits WHERE userId = :userId ORDER BY createdAt DESC")
    fun getAllHabits(userId: String): Flow<List<HabitEntity>>

    @Query("SELECT * FROM habits WHERE userId = :userId AND id = :id")
    suspend fun getHabitById(userId: String, id: Int): HabitEntity?

    @Query("""
        SELECT category, COUNT(*) as count 
        FROM habits 
        WHERE userId = :userId
        AND isCompleted = 1 
        AND date BETWEEN :startDate AND :endDate
        GROUP BY category
    """)
    fun getCategoryStats(
        userId: String,
        startDate: String,
        endDate: String
    ): Flow<List<CategoryStat>>

    @Query("""
        SELECT * FROM habits 
        WHERE userId = :userId
        AND date BETWEEN :startDate AND :endDate
    """)
    fun getHabitsForDateRange(
        userId: String,
        startDate: String,
        endDate: String
    ): Flow<List<HabitEntity>>

    @Query("SELECT COUNT(*) FROM habits WHERE userId = :userId AND isCompleted = 1")
    fun getCompletedCount(userId: String): Flow<Int>

    @Query("SELECT COUNT(*) FROM habits WHERE userId = :userId AND isCompleted = 1 AND date BETWEEN :startDate AND :endDate")
    fun getCompletedCountForWeek(
        userId: String,
        startDate: String,
        endDate: String
    ): Flow<Int>

    @Query("DELETE FROM habits WHERE userId = :userId")
    suspend fun deleteAllHabitsForUser(userId: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHabit(habit: HabitEntity)

    @Update
    suspend fun updateHabit(habit: HabitEntity)

    @Delete
    suspend fun deleteHabit(habit: HabitEntity)

    @Query("UPDATE habits SET remoteId = :remoteId WHERE id = :localId")
    suspend fun updateRemoteId(localId: Int, remoteId: String)

    @Query("SELECT * FROM habits WHERE remoteId = :remoteId LIMIT 1")
    suspend fun getHabitByRemoteId(remoteId: String): HabitEntity?
}

data class CategoryStat(
    val category: Category,
    val count: Int
)