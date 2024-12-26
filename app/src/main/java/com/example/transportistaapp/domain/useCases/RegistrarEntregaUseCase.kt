package com.example.transportistaapp.domain.useCases

import com.example.transportistaapp.domain.Repository
import javax.inject.Inject

class RegistrarEntregaUseCase @Inject constructor(private val repository: Repository) {
    suspend operator fun invoke(paqueteID: String, data: Map<String, Any>) {
        repository.registrarEntrega(paqueteID, data)
    }
}
