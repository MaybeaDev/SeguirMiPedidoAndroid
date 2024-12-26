package com.example.transportistaapp.ui.pantallaReparto.fragments.mapa

sealed class MapaState {
    data object Loading : MapaState()
    data class Success(val coordenadas: List<List<Double>>) : MapaState()
    data class Error(val error: String) : MapaState()
}
