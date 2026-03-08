package model

data class Habit(
    val id: Int,
    val name: String,
    var isCompleted: Boolean = false
)