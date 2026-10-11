package dev.ruizj.loteriaapp_dpa.viewmodel

import androidx.lifecycle.ViewModel
import dev.ruizj.loteriaapp_dpa.model.AuthState
import dev.ruizj.loteriaapp_dpa.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AuthViewModel(
    private val authRepository: AuthRepository = AuthRepository()
) : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    init {
        checkCurrentUser()
    }

    fun checkCurrentUser() {
        val user = authRepository.currentUser
        if (user != null) {
            _authState.value = AuthState.Authenticated(
                userId = user.uid,
                email = user.email ?: ""
            )
        } else {
            _authState.value = AuthState.Idle
        }
    }

    fun signIn(email: String, pass: String) {
        if (email.isBlank() || pass.isBlank()) {
            _authState.value = AuthState.Error("Please fill in all fields.")
            return
        }

        _authState.value = AuthState.Loading
        authRepository.signIn(
            email = email.trim(),
            pass = pass.trim(),
            onSuccess = { user ->
                _authState.value = AuthState.Authenticated(
                    userId = user.uid,
                    email = user.email ?: ""
                )
            },
            onError = { error ->
                _authState.value = AuthState.Error(error)
            }
        )
    }

    fun signUp(email: String, pass: String, confirmPass: String) {
        if (email.isBlank() || pass.isBlank() || confirmPass.isBlank()) {
            _authState.value = AuthState.Error("Please fill in all fields.")
            return
        }

        if (pass != confirmPass) {
            _authState.value = AuthState.Error("Passwords do not match.")
            return
        }

        if (pass.length < 6) {
            _authState.value = AuthState.Error("Password must be at least 6 characters.")
            return
        }

        _authState.value = AuthState.Loading
        authRepository.signUp(
            email = email.trim(),
            pass = pass.trim(),
            onSuccess = { user ->
                _authState.value = AuthState.Authenticated(
                    userId = user.uid,
                    email = user.email ?: ""
                )
            },
            onError = { error ->
                _authState.value = AuthState.Error(error)
            }
        )
    }

    fun signOut() {
        authRepository.signOut()
        _authState.value = AuthState.Idle
    }

    fun clearError() {
        if (_authState.value is AuthState.Error) {
            _authState.value = AuthState.Idle
        }
    }
}
