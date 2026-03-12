package com.example.lifetracker.di

import com.example.lifetracker.data.db.HabitDao
import com.example.lifetracker.data.repository.HabitRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

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