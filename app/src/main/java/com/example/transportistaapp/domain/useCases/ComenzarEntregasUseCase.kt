package com.example.transportistaapp.domain.useCases

import com.example.transportistaapp.domain.Repository
import javax.inject.Inject

class ComenzarEntregasUseCase@Inject constructor(private val repository: Repository) {
    suspend operator fun invoke() {
        repository.comenzarEntregas()
    }
}