package com.example.lifetracker.di

import com.example.lifetracker.data.db.HabitDao
import com.example.lifetracker.data.repository.HabitRepository
import com.example.recommender.auth.AuthManager
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
    fun provideAuthManager(): AuthManager {
        return AuthManager()
    }

    @Provides
    @Singleton
    fun provideHabitRepository(
        dao: HabitDao,
        authManager: AuthManager
    ): HabitRepository {
        return HabitRepository(dao, authManager)
    }
}