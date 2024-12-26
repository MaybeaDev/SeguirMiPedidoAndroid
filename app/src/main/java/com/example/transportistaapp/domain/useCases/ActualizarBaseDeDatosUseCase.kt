package com.example.transportistaapp.domain.useCases

import com.example.transportistaapp.domain.Repository
import javax.inject.Inject

class ActualizarBaseDeDatosUseCase @Inject constructor(private val repository: Repository) {
    suspend operator fun invoke(user:String) {
        repository.updateLocalPackages(user)
    }
}
