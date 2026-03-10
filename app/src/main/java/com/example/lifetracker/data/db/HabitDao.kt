package com.example.lifetracker.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

// data/db/HabitDao.kt
@Dao
interface HabitDao {

    @Query("SELECT * FROM habits ORDER BY createdAt DESC")
    fun getAllHabits(): Flow<List<HabitEntity>>
    // Flow means — keep watching the DB and emit whenever data changes
    // This is the Room + Coroutines magic

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHabit(habit: HabitEntity)

    @Update
    suspend fun updateHabit(habit: HabitEntity)

    @Delete
    suspend fun deleteHabit(habit: HabitEntity)

    @Query("SELECT * FROM habits WHERE id = :habitId")
    suspend fun getHabitById(habitId: Int): HabitEntity?

    // You'll use this in StatsFragment
    @Query("SELECT COUNT(*) FROM habits WHERE isCompleted = 1")
    fun getCompletedCount(): Flow<Int>
}