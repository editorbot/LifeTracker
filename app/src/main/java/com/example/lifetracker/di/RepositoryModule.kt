package com.example.lifetracker.di

import com.example.lifetracker.data.db.HabitDao
import com.example.lifetracker.data.repository.HabitRepository

// di/RepositoryModule.kt
@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideHabitRepository(dao: HabitDao): HabitRepository {
        return HabitRepository(dao)
    }
}