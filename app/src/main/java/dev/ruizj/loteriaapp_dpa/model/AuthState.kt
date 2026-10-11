package dev.ruizj.loteriaapp_dpa.model

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    data class Authenticated(val userId: String, val email: String) : AuthState()
    data class Error(val message: String) : AuthState()
}
