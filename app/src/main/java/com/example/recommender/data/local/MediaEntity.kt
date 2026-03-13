package com.example.recommender.data.local


import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "media_items")
data class MediaEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val genres: String,          // stored as comma-separated e.g. "Action,Crime"
    val rating: Float,
    val type: String,            // "MOVIE" or "BOOK"
    val posterPath: String?,
    val authorOrDirector: String?,
    val summary: String?,
    val dateAdded: Long = System.currentTimeMillis()
)