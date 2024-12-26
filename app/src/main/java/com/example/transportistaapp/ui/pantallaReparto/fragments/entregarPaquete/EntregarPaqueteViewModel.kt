package com.example.transportistaapp.ui.pantallaReparto.fragments.entregarPaquete

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.transportistaapp.domain.useCases.GetPaqueteUseCase
import com.example.transportistaapp.domain.useCases.RegistrarEntregaUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EntregarPaqueteViewModel @Inject constructor(
    private val getPaqueteUseCase: GetPaqueteUseCase,
    private val registrarEntregaUseCase: RegistrarEntregaUseCase,
) : ViewModel() {
    private var _state = MutableStateFlow<EntregarPaqueteState>(EntregarPaqueteState.Loading)
    val state: StateFlow<EntregarPaqueteState> = _state


    fun obtenerPaquete(id: String) {
        _state.value = EntregarPaqueteState.Loading
        viewModelScope.launch {
            try {
                val paquete = getPaqueteUseCase(id)
                _state.value = EntregarPaqueteState.Success(paquete)
            } catch (e: Exception) {
                _state.value = EntregarPaqueteState.Error("No se encontró el paquete: $e")
            }
        }
    }

    fun registrarEntrega(
        nombre: String,
        rut: String,
        telefono: String,
        paqueteID: String,
        recibeCheckBox: Boolean
    ) {
        if (recibeCheckBox) {
            if (rut.isEmpty()) {
                _state.value =
                    EntregarPaqueteState.FormularioInvalido("Debes ingresar el rut de quien recibe")
                return
            } else if (rut.length > 9 || rut.length < 8) {
                _state.value =
                    EntregarPaqueteState.FormularioInvalido("El rut debe tener entre 8 y 9 digitos!")
                return
            }
        } else {
            if (nombre.isEmpty() || rut.isEmpty() || telefono.isEmpty()) {
                _state.value =
                    EntregarPaqueteState.FormularioInvalido("Debes completar todos los campos")
                return
            } else if (rut.length > 9 || rut.length < 8) {
                _state.value =
                    EntregarPaqueteState.FormularioInvalido("El rut debe tener entre 8 y 9 digitos!")
                return
            } else if (telefono.length < 9) {
                _state.value =
                    EntregarPaqueteState.FormularioInvalido("El telefono debe contener al menos 9 digitos")
                return
            } else if (telefono.length > 12) {
                _state.value =
                    EntregarPaqueteState.FormularioInvalido("El telefono debe contener al 12 o menos digitos")
                return
            }
        }
        val data = mapOf(
            "nombre" to nombre,
            "rut" to rut,
            "telefono" to telefono
        )
        viewModelScope.launch {
            registrarEntregaUseCase(paqueteID, data)
            _state.value = EntregarPaqueteState.BackCajas
        }
    }
}