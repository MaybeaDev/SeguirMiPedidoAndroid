package com.example.transportistaapp.domain.useCases

import com.example.transportistaapp.domain.Repository
import com.example.transportistaapp.domain.model.Ruta
import javax.inject.Inject
class GetRutasActivasUseCase @Inject constructor(private val repository: Repository) {
    suspend operator fun invoke(): List<Ruta> {
        return repository.getRutasActivas()
    }
}
