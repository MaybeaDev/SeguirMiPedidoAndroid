package com.example.transportistaapp.ui.homeTransportista.fragments.verRutas

import com.example.transportistaapp.domain.model.Ruta

sealed class VerRutasState {
    data object Loading : VerRutasState()
    data object IrARepartir : VerRutasState()
    data class Success(val rutas: List<Ruta>) : VerRutasState()
    data class Error(val error: String) : VerRutasState()
}