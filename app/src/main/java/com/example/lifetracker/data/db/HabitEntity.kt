package com.example.lifetracker.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.lifetracker.model.Category
import com.example.lifetracker.model.Priority
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// data/db/HabitEntity.kt
@Entity(tableName = "habits")
data class HabitEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val startTime: Long? = null,        // nullable — not all habits need time
    val endTime: Long? = null,
    val priority: Priority = Priority.MEDIUM,
    val category: Category = Category.PERSONAL,
    val isCompleted: Boolean = false,
    val date: String = getCurrentDate(), // "2024-03-12"
    val createdAt: Long = System.currentTimeMillis()
)

// Companion to get today's date consistently
fun getCurrentDate(): String {
    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    return sdf.format(Date())
}