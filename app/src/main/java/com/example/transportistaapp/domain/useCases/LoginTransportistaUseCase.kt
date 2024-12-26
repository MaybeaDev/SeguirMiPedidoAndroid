package com.example.transportistaapp.domain.useCases

import android.util.Log
import com.example.transportistaapp.domain.Repository
import com.google.firebase.auth.FirebaseUser
import javax.inject.Inject

class LoginTransportistaUseCase @Inject constructor(private val repository: Repository) {
    suspend operator fun invoke(user: String, password: String): FirebaseUser? {
        val userResponse = repository.loginTransportista(user, password)
        Log.d("Maybealog LoginTransportistaUseCase", userResponse.toString())
        if (userResponse != null) {
            repository.updateLocalPackages(userResponse.uid)
            return userResponse
        } else {
            return userResponse
        }
    }
}







