package com.example.transportistaapp.ui.homeTransportista.fragments.cargarRuta

import com.example.transportistaapp.domain.model.Paquete

sealed class CargarRutaState  {
    data object Loading : CargarRutaState()
    data class Success(val rutas: List<Paquete>) : CargarRutaState()
    data class Error(val error: String) : CargarRutaState()
}