package com.example.transportistaapp.ui.pantallaReparto.fragments.listado

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.transportistaapp.domain.model.Paquete
import com.example.transportistaapp.domain.useCases.GetRutasActivasUseCase
import com.example.transportistaapp.domain.useCases.TerminarEntregaUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class ListadoViewModel @Inject constructor(
    private val getRutasActivasUseCase: GetRutasActivasUseCase,
    private val terminarEntregaUseCase: TerminarEntregaUseCase
) : ViewModel() {

    private val _paquetes = MutableLiveData<List<Paquete>>()
    val paquetes: LiveData<List<Paquete>> = _paquetes
    private var _state = MutableStateFlow<ListadoState>(ListadoState.Loading)
    val state: StateFlow<ListadoState> = _state


    fun cargarRutas() {
        viewModelScope.launch {
            _state.value = ListadoState.Loading
            val rutas = withContext(Dispatchers.IO) { getRutasActivasUseCase() }
            val paquetes = mutableListOf<Paquete>()
            rutas.forEach {
                paquetes.addAll(it.paquetes)
            }

            Log.d("Maybealog ListadoVM", paquetes.toString())
            if (paquetes.isEmpty()) {
                _state.value =
                    ListadoState.Error("Ha ocurrido un error, getStockUseCase() -> null")
            } else {
                _paquetes.value = paquetes
                Log.d(
                    "MaybeaLog ListadoVM 47",
                    paquetes.find { it.estado == "En reparto" }.toString()
                )
                if (paquetes.find { it.estado == "En reparto" } == null) {
                    _state.value = ListadoState.PaquetesEntregados
                } else {
                    _state.value =
                        ListadoState.Success(paquetes = paquetes.filter { it.estado == "En reparto" })
                }
            }

        }
    }

    fun terminarEntrega() {
        viewModelScope.launch {
            terminarEntregaUseCase()
            _state.value = ListadoState.RutasTerminadas
        }
    }
}
