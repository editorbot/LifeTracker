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

    @Query("SELECT * FROM habits WHERE date = :date ORDER BY startTime ASC")
    fun getHabitsByDate(date: String): Flow<List<HabitEntity>>
    // Timeline view uses this — ordered by startTime for chronological display

    @Query("SELECT * FROM habits ORDER BY createdAt DESC")
    fun getAllHabits(): Flow<List<HabitEntity>>

    @Query("SELECT * FROM habits WHERE id = :id")
    suspend fun getHabitById(id: Int): HabitEntity?

    // For pie chart — time spent per category
    @Query("""
        SELECT category, COUNT(*) as count 
        FROM habits 
        WHERE isCompleted = 1 
        AND date BETWEEN :startDate AND :endDate
        GROUP BY category
    """)
    fun getCategoryStats(startDate: String, endDate: String): Flow<List<CategoryStat>>

    // For weekly summary
    @Query("""
        SELECT COUNT(*) FROM habits 
        WHERE date BETWEEN :startDate AND :endDate 
        AND isCompleted = 1
    """)
    fun getCompletedCountForWeek(startDate: String, endDate: String): Flow<Int>

    @Query("SELECT COUNT(*) FROM habits WHERE isCompleted = 1")
    fun getCompletedCount(): Flow<Int>

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