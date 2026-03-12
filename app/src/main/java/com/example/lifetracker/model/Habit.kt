package com.example.lifetracker.model

// model/Habit.kt
enum class Priority(val label: String, val colorHex: String) {
    HIGH("High", "#FF6B6B"),
    MEDIUM("Medium", "#FFD93D"),
    LOW("Low", "#6BCB77")
}

enum class Category(val label: String, val iconRes: String) {
    WORK("Work", "💼"),
    STUDY("Study", "📚"),
    PERSONAL("Personal", "🌱"),
    LEISURE("Leisure", "🎮")
}