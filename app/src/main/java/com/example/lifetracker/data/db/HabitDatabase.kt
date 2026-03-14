package com.example.lifetracker.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

// data/db/HabitDatabase.kt
@Database(
    entities = [HabitEntity::class],
    version = 3,                    // ← Bumped from 1
    exportSchema = false
)
abstract class HabitDatabase : RoomDatabase() {

    abstract fun habitDao(): HabitDao

    companion object {
        @Volatile
        private var INSTANCE: HabitDatabase? = null

        fun getDatabase(context: Context): HabitDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    HabitDatabase::class.java,
                    "habit_database"
                )
                    .fallbackToDestructiveMigration() // ← Wipes DB on version change
                    // Fine for development — in production you'd write migrations
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}