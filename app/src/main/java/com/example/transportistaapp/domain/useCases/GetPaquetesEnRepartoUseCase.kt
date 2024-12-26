package com.example.transportistaapp.domain.useCases

import com.example.transportistaapp.domain.Repository
import com.example.transportistaapp.domain.model.Paquete
import javax.inject.Inject

class GetPaquetesEnRepartoUseCase  @Inject constructor(private val repository: Repository) {
    suspend operator fun invoke() : List<Paquete> {
        return repository.obtenerPaquetesEnReparto()
    }
}
