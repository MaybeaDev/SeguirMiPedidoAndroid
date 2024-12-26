package com.example.transportistaapp.domain

import com.example.transportistaapp.domain.model.Paquete
import com.example.transportistaapp.domain.model.Ruta
import com.google.firebase.auth.FirebaseUser
import java.util.Date

interface Repository {
    suspend fun loginTransportista(user: String, password: String): FirebaseUser?
    suspend fun updateLocalPackages(transportista: String)
    suspend fun getRutasActivas(): List<Ruta>
    suspend fun marcarCajaEntregada(cajaId: String, fechaEntrega: Date)
    suspend fun marcarCajaNoEntregada(cajaId: String, motivo: String)
    suspend fun obtenerPaquetesEnReparto(): List<Paquete>
    suspend fun terminarEntrega()
    suspend fun getPaquetesByRoute(routeId: String): List<Paquete>
    suspend fun cargarRuta(rutaID:String)
    suspend fun comenzarEntregas()
    suspend fun getPaquete(id: String): Paquete
    suspend fun registrarEntrega(paqueteID: String, data: Map<String, Any>)
}