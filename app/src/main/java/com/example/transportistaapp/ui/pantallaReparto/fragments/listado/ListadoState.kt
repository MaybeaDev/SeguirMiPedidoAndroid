package com.example.transportistaapp.ui.pantallaReparto.fragments.listado

import com.example.transportistaapp.domain.model.Paquete

sealed class ListadoState{
    data object Loading : ListadoState()
    data object PaquetesEntregados : ListadoState()
    data object RutasTerminadas : ListadoState()
    data class Error(val error:String) : ListadoState()
    data class Success(val paquetes: List<Paquete>) : ListadoState()
}