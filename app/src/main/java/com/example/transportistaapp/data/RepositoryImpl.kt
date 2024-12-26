package com.example.transportistaapp.data

import android.util.Log
import com.example.transportistaapp.data.database.dao.LastLoginDao
import com.example.transportistaapp.data.database.dao.PaqueteDao
import com.example.transportistaapp.data.database.dao.RutaDao
import com.example.transportistaapp.data.database.entities.PaqueteEntity
import com.example.transportistaapp.data.database.entities.toDomain
import com.example.transportistaapp.data.database.entities.toRoom
import com.example.transportistaapp.data.network.FirestoreService
import com.example.transportistaapp.domain.Repository
import com.example.transportistaapp.domain.model.Paquete
import com.example.transportistaapp.domain.model.Ruta
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.tasks.await
import java.util.Date
import javax.inject.Inject

class RepositoryImpl @Inject constructor(
    private val firestoreService: FirestoreService,
    private val firebaseAuth: FirebaseAuth,
    private val rutaDao: RutaDao,
    private val paqueteDao: PaqueteDao,
    private val lastLogin: LastLoginDao,

    ) : Repository {
    override suspend fun loginTransportista(user: String, password: String): FirebaseUser? {
        try {
            val result = firebaseAuth.signInWithEmailAndPassword(user, password).await()
            if (result.user != null) {
                if (firestoreService.esTransportista(result.user!!)) {
                    return result.user
                }
            }
            return null
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun updateLocalPackages(transportista: String) {
        if (transportista == lastLogin.get()?.uid && rutaDao.rutasEnReparto().isNotEmpty()) {
            return
        } else {
            val cargadas = rutaDao.getCargadas()
            val paquetesCargados = mutableListOf<PaqueteEntity>()
            if (cargadas.isNotEmpty()) {
                cargadas.forEach {
                    paquetesCargados.addAll(paqueteDao.obtenerPorRuta(it.id))
                }
            }
            paqueteDao.deleteAll()
            rutaDao.deleteAll()
            val rutasList = firestoreService.getRutasPorTransportista(transportista)
            rutaDao.insertAll(rutasList.map { it.toRoom() })
            rutasList.forEach { ruta ->
                if (ruta.id !in cargadas.map { it.id }) {
                    val paquetesEntityList = ruta.paquetes.map { it.toRoom() }
                    paqueteDao.insertAll(paquetesEntityList)
                }
            }
            paqueteDao.insertAll(paquetesCargados)
        }
    }

    override suspend fun obtenerPaquetesEnReparto(): List<Paquete> {
        return paqueteDao.obtenerEnReparto().map { it.toDomain() }
    }

    override suspend fun getRutasActivas(): List<Ruta> {
        val rutasLocal = rutaDao.getAll().map { it.toDomain() }
        val rutas = rutasLocal.map { ruta ->
            val paquetes = paqueteDao.obtenerPorRuta(ruta.id)
            ruta.paquetes = paquetes.map { it.toDomain() }
            ruta
        }
        val rutasConPaquetes = rutas.filter { it.paquetes.isNotEmpty() }
        return rutasConPaquetes
    }

    override suspend fun marcarCajaEntregada(cajaId: String, fechaEntrega: Date) {
        paqueteDao.entregar(cajaId, fechaEntrega)
    }

    override suspend fun marcarCajaNoEntregada(cajaId: String, motivo: String) {
        paqueteDao.entregaFallida(cajaId, motivo)
        firestoreService.entregaFallidaPaquete(cajaId, motivo)
    }

    override suspend fun terminarEntrega() {
        val rutas = rutaDao.rutasEnReparto()
        rutas.forEach {
            rutaDao.terminar(it)
        }
        firestoreService.terminarRutas(rutas.map { it.toDomain() })
    }

    override suspend fun getPaquetesByRoute(routeId: String): List<Paquete> {
        val paquetes = paqueteDao.obtenerPorRutaParaEntregar(routeId)
        return paquetes.map { it.toDomain() }
    }

    override suspend fun cargarRuta(rutaID: String) {
        rutaDao.cargarRuta(rutaID)
        firestoreService.cargarRuta(rutaID)
    }

    override suspend fun comenzarEntregas() {
        val idRutas = mutableListOf<String>()
        val idPaquetes = mutableListOf<String>()

        rutaDao.getAll().map {
            if (it.cargado) {
                idRutas.add(it.id)
                it.enReparto = true
            }
            it
        }.forEach { rutaEntity ->
            rutaDao.updateRuta(rutaEntity)
            if (rutaEntity.enReparto) {
                paqueteDao.obtenerPorRuta(rutaEntity.id).filter { it.estado != 3 }
                    .forEach { paqueteEntity ->
                        idPaquetes.add(paqueteEntity.id)
                        paqueteEntity.estado = 2
                        paqueteDao.update(paqueteEntity)
                        Log.d("MaybeaLog", paqueteDao.get(paqueteEntity.id).toString())
                    }
            }
        }
        firestoreService.comenzarEntregas(idRutas, idPaquetes)
    }

    override suspend fun getPaquete(id: String): Paquete {
        return paqueteDao.get(id).toDomain()
    }

    override suspend fun registrarEntrega(paqueteID: String, data: Map<String, Any>) {
        paqueteDao.entregar(paqueteID, Date())
        firestoreService.entregarPaquete(paqueteID, data)
    }
}