package com.example.transportistaapp.domain.useCases

import com.example.transportistaapp.domain.Repository
import javax.inject.Inject

class CajaNoEntregadaUseCase @Inject constructor(private val repository: Repository) {
    suspend operator fun invoke(cajaId: String, motivo: String) {
        return repository.marcarCajaNoEntregada(cajaId, motivo)
    }
}
