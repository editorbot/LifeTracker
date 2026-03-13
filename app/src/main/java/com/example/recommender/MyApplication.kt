package com.example.recommender



import android.app.Application
import com.example.recommender.auth.AmplifyAuthManager

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        AmplifyAuthManager.initialize(this)
    }
}