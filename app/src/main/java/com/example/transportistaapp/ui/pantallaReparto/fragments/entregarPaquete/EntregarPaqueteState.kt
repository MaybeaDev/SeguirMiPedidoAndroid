package com.example.transportistaapp.ui.pantallaReparto.fragments.entregarPaquete

import com.example.transportistaapp.domain.model.Paquete

sealed class EntregarPaqueteState {
    data object Loading : EntregarPaqueteState()
    data object BackCajas : EntregarPaqueteState()
    data class FormularioInvalido(val error: String) : EntregarPaqueteState()
    data class Error(val error: String) : EntregarPaqueteState()
    data class Success(val paquete: Paquete) : EntregarPaqueteState()
}