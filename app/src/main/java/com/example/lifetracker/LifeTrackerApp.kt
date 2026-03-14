package com.example.lifetracker

import android.app.Application
import com.example.recommender.auth.AmplifyAuthManager
import dagger.hilt.android.HiltAndroidApp

// LifeTrackerApp.kt
@HiltAndroidApp
class LifeTrackerApp : Application(){
    override fun onCreate() {
        super.onCreate()
        AmplifyAuthManager.initialize(this)
    }
}