package com.example.transportistaapp.ui.pantallaReparto.fragments.mapa

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class MapaViewModel @Inject constructor() : ViewModel() {

    private var _state = MutableStateFlow<MapaState>(MapaState.Loading)
    val state: StateFlow<MapaState> = _state

    fun setCoordenadas(coordenadas: List<List<Double>>) {
        _state.value = MapaState.Success(coordenadas)
    }
}
