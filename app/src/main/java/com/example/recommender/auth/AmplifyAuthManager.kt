package com.example.recommender.auth


import android.content.Context
import android.util.Log
import com.amplifyframework.api.aws.AWSApiPlugin
import com.amplifyframework.auth.cognito.AWSCognitoAuthPlugin
import com.amplifyframework.auth.options.AuthSignOutOptions
import com.amplifyframework.core.Amplify
import com.amplifyframework.core.AmplifyConfiguration
import com.example.lifetracker.R
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

object AmplifyAuthManager {

    private var initialized = false

    fun initialize(context: Context) {
        if (initialized) return
        try {
            Amplify.addPlugin(AWSCognitoAuthPlugin())
            Amplify.addPlugin(AWSApiPlugin())        // ← Add this line
            val config = AmplifyConfiguration.fromConfigFile(
                context,
                R.raw.amplifyconfiguration   // the json file you put in res/raw
            )
            Amplify.configure(config, context)
            initialized = true
            Log.d("Amplify", "Initialized successfully")
        } catch (e: Exception) {
            Log.e("Amplify", "Init failed: ${e.message}")
        }
    }

    // ── Get ID token — attached to every AWS API request ─────────────

    suspend fun getIdToken(): String = suspendCancellableCoroutine { cont ->
        Amplify.Auth.fetchAuthSession(
            { session ->
                val cognitoSession =
                    session as? com.amplifyframework.auth.cognito.AWSCognitoAuthSession
                val token = cognitoSession
                    ?.userPoolTokensResult
                    ?.value
                    ?.idToken
                    ?.toString()

                if (token != null) {
                    cont.resume(token)
                } else {
                    cont.resumeWithException(Exception("No ID token available"))
                }
            },
            { error -> cont.resumeWithException(Exception(error.message)) }
        )
    }

    // ── Auth state check ──────────────────────────────────────────────

    suspend fun isSignedIn(): Boolean = suspendCancellableCoroutine { cont ->
        Amplify.Auth.fetchAuthSession(
            { session -> cont.resume(session.isSignedIn) },
            { cont.resume(false) }
        )
    }

    // ── Sign out ──────────────────────────────────────────────────────

    suspend fun signOut(): Unit = suspendCancellableCoroutine { cont ->
        Amplify.Auth.signOut(
            AuthSignOutOptions.builder().globalSignOut(true).build()
        ) { cont.resume(Unit) }
    }
    suspend fun getCurrentUserId(): String = suspendCancellableCoroutine { cont ->
        Amplify.Auth.getCurrentUser(
            { user -> cont.resume(user.userId) },
            { error -> cont.resumeWithException(Exception(error.message)) }
        )
    }
}