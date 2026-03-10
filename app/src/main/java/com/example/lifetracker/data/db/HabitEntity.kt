package com.example.lifetracker.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

// data/db/HabitEntity.kt
@Entity(tableName = "habits")
data class HabitEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val isCompleted: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()  // timestamp
)