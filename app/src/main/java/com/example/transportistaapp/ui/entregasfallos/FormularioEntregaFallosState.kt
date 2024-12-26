package com.example.transportistaapp.ui.entregasfallos


sealed class FormularioEntregaFallosState {
    data object Loading : FormularioEntregaFallosState()
    data class Error(val error : String) : FormularioEntregaFallosState()
    data object Success : FormularioEntregaFallosState()
}