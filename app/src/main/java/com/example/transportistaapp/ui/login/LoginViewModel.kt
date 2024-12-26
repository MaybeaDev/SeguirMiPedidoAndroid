package com.example.transportistaapp.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.transportistaapp.data.FirebaseErrorUtils
import com.example.transportistaapp.domain.useCases.LoginTransportistaUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginTransportistaUseCase: LoginTransportistaUseCase
) : ViewModel() {

    private val _loginState = MutableStateFlow<LoginState>(LoginState.Idle)
    val loginState: StateFlow<LoginState> = _loginState

    fun login(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _loginState.value = LoginState.Failure("Completar todos los campos")
            return
        }

        viewModelScope.launch {
            _loginState.value = LoginState.Loading
            try {
                val user = loginTransportistaUseCase(email, password)
                _loginState.value = if (user != null) {
                    LoginState.Success
                } else {
                    LoginState.Failure("Usuario no encontrado")
                }
            } catch (e: Exception) {
                _loginState.value = LoginState.Failure(FirebaseErrorUtils.getErrorMessage(e))
            }
        }
    }
}

