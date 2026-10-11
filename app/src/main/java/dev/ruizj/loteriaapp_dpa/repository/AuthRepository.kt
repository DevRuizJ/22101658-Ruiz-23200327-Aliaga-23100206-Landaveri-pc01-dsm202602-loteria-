package dev.ruizj.loteriaapp_dpa.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class AuthRepository(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) {
    val currentUser: FirebaseUser?
        get() = auth.currentUser

    fun signIn(
        email: String,
        pass: String,
        onSuccess: (FirebaseUser) -> Unit,
        onError: (String) -> Unit
    ) {
        if (email.isBlank() || pass.isBlank()) {
            onError("Email and password cannot be empty.")
            return
        }
        auth.signInWithEmailAndPassword(email, pass)
            .addOnSuccessListener { authResult ->
                val user = authResult.user
                if (user != null) {
                    onSuccess(user)
                } else {
                    onError("Failed to retrieve authenticated user.")
                }
            }
            .addOnFailureListener { exception ->
                onError(exception.localizedMessage ?: "Authentication failed.")
            }
    }

    fun signUp(
        email: String,
        pass: String,
        onSuccess: (FirebaseUser) -> Unit,
        onError: (String) -> Unit
    ) {
        if (email.isBlank() || pass.isBlank()) {
            onError("Email and password cannot be empty.")
            return
        }
        auth.createUserWithEmailAndPassword(email, pass)
            .addOnSuccessListener { authResult ->
                val user = authResult.user
                if (user != null) {
                    onSuccess(user)
                } else {
                    onError("Failed to create user account.")
                }
            }
            .addOnFailureListener { exception ->
                onError(exception.localizedMessage ?: "Registration failed.")
            }
    }

    fun signOut() {
        auth.signOut()
    }
}
