package com.example.lifetracker.di

import android.content.Context
import androidx.room.Room
import com.example.lifetracker.data.db.HabitDao
import com.example.lifetracker.data.db.HabitDatabase
import com.example.lifetracker.data.preferences.UserPreferences
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

// di/DatabaseModule.kt
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): HabitDatabase {
        return Room.databaseBuilder(
            context,
            HabitDatabase::class.java,
            "habit_database"
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    @Singleton
    fun provideHabitDao(database: HabitDatabase): HabitDao {
        return database.habitDao()
    }

    @Provides
    @Singleton
    fun provideUserPreferences(@ApplicationContext context: Context): UserPreferences {
        return UserPreferences(context)
    }
}