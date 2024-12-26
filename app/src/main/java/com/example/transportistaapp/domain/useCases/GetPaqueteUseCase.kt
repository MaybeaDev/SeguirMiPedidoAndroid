package com.example.transportistaapp.domain.useCases

import com.example.transportistaapp.domain.Repository
import com.example.transportistaapp.domain.model.Paquete
import javax.inject.Inject

class GetPaqueteUseCase @Inject constructor(private val repository: Repository) {
    suspend operator fun invoke(id: String): Paquete {
        return repository.getPaquete(id)
    }
}
