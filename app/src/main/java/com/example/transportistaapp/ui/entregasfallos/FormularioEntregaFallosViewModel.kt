package com.example.transportistaapp.ui.entregasfallos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.transportistaapp.domain.useCases.CajaNoEntregadaUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FormularioEntregaFallosViewModel @Inject constructor(
    private val cajaNoEntregadaUseCase : CajaNoEntregadaUseCase
) : ViewModel() {

    private val _state = MutableStateFlow<FormularioEntregaFallosState>(FormularioEntregaFallosState.Loading)
    val state: StateFlow<FormularioEntregaFallosState> get() = _state


    fun registrarFallo(paquete : String, motivo: String) {
        _state.value = FormularioEntregaFallosState.Loading
        viewModelScope.launch {
            try {
                cajaNoEntregadaUseCase(paquete, motivo)
                _state.value = FormularioEntregaFallosState.Success
            } catch (e: Exception) {
                _state.value = FormularioEntregaFallosState.Error(e.toString())
            }
        }
    }
}
