package com.example.lifetracker.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.lifetracker.model.Category
import kotlinx.coroutines.flow.Flow

// data/db/HabitDao.kt
@Dao
interface HabitDao {

    // Every query now scoped to userId
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

    // Called on logout — wipe only THIS user's local data
    @Query("DELETE FROM habits WHERE userId = :userId")
    suspend fun deleteAllHabitsForUser(userId: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHabit(habit: HabitEntity)

    @Update
    suspend fun updateHabit(habit: HabitEntity)

    @Delete
    suspend fun deleteHabit(habit: HabitEntity)
}
// Data class for category query result
data class CategoryStat(
    val category: Category,
    val count: Int
)