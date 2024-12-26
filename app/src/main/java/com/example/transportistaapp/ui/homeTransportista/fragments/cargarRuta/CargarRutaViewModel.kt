package com.example.transportistaapp.ui.homeTransportista.fragments.cargarRuta

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.transportistaapp.domain.model.Paquete
import com.example.transportistaapp.domain.useCases.GetPaquetesByRouteUseCase
import com.example.transportistaapp.domain.useCases.CargarRutaUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CargarRutaViewModel @Inject constructor(
    private val getPaquetesByRouteUseCase: GetPaquetesByRouteUseCase,  // Dependencia del repositorio
    private val cargarRutaUseCase: CargarRutaUseCase

) : ViewModel() {
    private val _state = MutableStateFlow<CargarRutaState>(CargarRutaState.Loading)
    val state: StateFlow<CargarRutaState> = _state
    private val _paquetes = MutableStateFlow<List<Paquete>>(emptyList())
    val paquetes: StateFlow<List<Paquete>> = _paquetes


    fun obtenerPaquetes(rutaID: String) {
        viewModelScope.launch {
            _state.value = CargarRutaState.Loading
            try {
                _paquetes.value = getPaquetesByRouteUseCase(rutaID)
                _state.value = CargarRutaState.Success(_paquetes.value)
            } catch (e: Exception) {
                _state.value = CargarRutaState.Error(e.toString())
            }
        }
    }

    fun validarCodigo(codigo: String): List<Boolean> {
        val paquete = _paquetes.value.find { it.id == codigo && !it.validado }
        if (paquete == null) {
            return listOf(false, _paquetes.value.none { !it.validado })
        }
        val paquetesActualizados = _paquetes.value.map {
            if (it.id == codigo && !it.validado) {
                it.copy(validado = true) // Crear una copia con el campo 'validado' cambiado
            } else {
                it
            }
        }
        _paquetes.value = paquetesActualizados
        _state.value = CargarRutaState.Success(_paquetes.value.filter { !it.validado })
        return listOf(true, _paquetes.value.none { !it.validado })
    }

    fun validarRuta(rutaID: String) {
        viewModelScope.launch {
            cargarRutaUseCase(rutaID)
        }
    }
}