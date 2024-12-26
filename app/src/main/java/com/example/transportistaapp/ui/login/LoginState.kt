package com.example.transportistaapp.ui.login

sealed class LoginState {
    data object Idle : LoginState()
    data object Loading : LoginState()
    data object Success : LoginState()
    data class Failure(val error: String) : LoginState()
}