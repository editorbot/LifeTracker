package com.example.lifetracker.di

import com.example.lifetracker.data.db.HabitDao
import com.example.lifetracker.data.repository.HabitRepository
import com.example.lifetracker.data.sync.SyncManager
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
    fun provideSyncManager(): SyncManager = SyncManager()

    @Provides
    @Singleton
    fun provideHabitRepository(
        dao: HabitDao,
        syncManager: SyncManager
    ): HabitRepository {
        return HabitRepository(dao, syncManager)
    }
}