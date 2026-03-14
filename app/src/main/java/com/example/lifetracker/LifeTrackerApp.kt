package com.example.lifetracker

import android.app.Application
import android.util.Log
import com.amplifyframework.AmplifyException
import dagger.hilt.android.HiltAndroidApp
import com.amplifyframework.core.Amplify
import com.amplifyframework.auth.cognito.AWSCognitoAuthPlugin
import com.amplifyframework.api.aws.AWSApiPlugin
import com.example.recommender.auth.AmplifyAuthManager

// LifeTrackerApp.kt
@HiltAndroidApp
class LifeTrackerApp : Application() {
    override fun onCreate() {
        super.onCreate()
        AmplifyAuthManager.initialize(this)
    }
}