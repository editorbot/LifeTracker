package com.example.recommender.auth

import com.amplifyframework.core.Amplify
import javax.inject.Inject

// data/auth/AuthManager.kt  ← new file, new package
class AuthManager @Inject constructor() {

    // Get current logged in userId from Cognito
    fun getCurrentUserId(
        onSuccess: (String) -> Unit,
        onFailure: () -> Unit
    ) {
        Amplify.Auth.getCurrentUser(
            { user -> onSuccess(user.userId) },
            { onFailure() }
        )
    }

    fun getCurrentUserEmail(
        onSuccess: (String) -> Unit,
        onFailure: () -> Unit
    ) {
        Amplify.Auth.getCurrentUser(
            { user -> onSuccess(user.username) },
            { onFailure() }
        )
    }

    fun signOut(onComplete: () -> Unit) {
        Amplify.Auth.signOut {
            onComplete()
        }
    }
}