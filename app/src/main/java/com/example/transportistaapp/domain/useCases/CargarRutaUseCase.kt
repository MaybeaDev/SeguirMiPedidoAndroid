package com.example.transportistaapp.domain.useCases

import com.example.transportistaapp.domain.Repository
import javax.inject.Inject

class CargarRutaUseCase @Inject constructor(private val repository: Repository) {
    suspend operator fun invoke(rutaID : String ) {
        repository.cargarRuta(rutaID)
    }
}