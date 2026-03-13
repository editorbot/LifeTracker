package com.example.recommender.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.amplifyframework.auth.AuthException
import com.amplifyframework.core.Amplify
import com.amplifyframework.ui.authenticator.ui.Authenticator
import com.example.lifetracker.R
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

//class LoginFragment : Fragment() {
//
//    override fun onCreateView(
//        inflater: LayoutInflater,
//        container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View {
//        return inflater.inflate(R.layout.fragment_login, container, false)
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//        val etEmail    = view.findViewById<EditText>(R.id.et_email)
//        val etPassword = view.findViewById<EditText>(R.id.et_password)
//        val btnSignIn  = view.findViewById<Button>(R.id.btn_sign_in)
//        val btnSignUp  = view.findViewById<Button>(R.id.btn_sign_up)
//        val tvError    = view.findViewById<TextView>(R.id.tv_login_error)
//        val progress   = view.findViewById<ProgressBar>(R.id.progress_login)
//
//        btnSignIn.setOnClickListener {
//            val email    = etEmail.text.toString().trim()
//            val password = etPassword.text.toString()
//            if (email.isBlank() || password.isBlank()) {
//                tvError.visibility = View.VISIBLE
//                tvError.text = "Please enter email and password"
//                return@setOnClickListener
//            }
//
//            progress.visibility = View.VISIBLE
//            tvError.visibility  = View.GONE
//
//            lifecycleScope.launch {
//                try {
//                    signIn(email, password)
//                    findNavController().navigate(R.id.homeFragment)
//                } catch (e: Exception) {
//                    progress.visibility = View.GONE
//                    tvError.visibility  = View.VISIBLE
//                    tvError.text        = e.message ?: "Sign in failed"
//                }
//            }
//        }
//
//        btnSignUp.setOnClickListener {
//            findNavController().navigate(R.id.signUpFragment)
//        }
//    }
//
//    private suspend fun signIn(email: String, password: String) =
//        suspendCancellableCoroutine { cont ->
//            Amplify.Auth.signIn(email, password,
//                { result ->
//                    if (result.isSignedIn) cont.resume(Unit)
//                    else cont.resumeWithException(Exception("Sign in incomplete"))
//                },
//                { error -> cont.resumeWithException(Exception(error.message)) }
//            )
//        }
//}
class LoginFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // ComposeView is the bridge between old Fragments and Jetpack Compose
        return ComposeView(requireContext()).apply {
            setContent {
                // Now Authenticator works perfectly here
                Authenticator {
                    // This runs when sign in is successful

                        findNavController().navigate(R.id.homeFragment)
                }
            }
        }
    }
}