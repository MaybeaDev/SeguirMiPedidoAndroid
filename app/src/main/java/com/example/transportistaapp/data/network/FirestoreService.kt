package com.example.transportistaapp.data.network

import android.content.ContentValues.TAG
import android.util.Log
import com.example.transportistaapp.domain.model.Paquete
import com.example.transportistaapp.domain.model.Ruta
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.WriteBatch
import kotlinx.coroutines.tasks.await
import java.util.Date
import javax.inject.Inject

class FirestoreService @Inject constructor(private val db: FirebaseFirestore) {
    suspend fun getRutasPorTransportista(uid: String): List<Ruta> {
        val rutasSnapshot = db.collection("Rutas")
            .whereEqualTo("transportista", uid)
            .whereEqualTo("activa", true)
            .get()
            .await()
        val rutas = rutasSnapshot.documents.map { d ->
            Ruta(
                id = d.id,
                nombre = d.getString("alias") ?: "",
                cargado = d.getBoolean("cargado") ?: false,
                completado = d.getBoolean("validado") ?: false,
                enReparto = d.getBoolean("en_reparto") ?: false
            )
        }
        val rutasIds = rutas.map { it.id }
        val rutasChunks = rutasIds.chunked(10)
        val paquetes = mutableListOf<Paquete>()
        for (chunk in rutasChunks) {
            val paquetesSnapshot = db.collection("Paquetes")
                .whereIn("ruta", chunk)
                .whereNotEqualTo("estado", 3)
                .get()
                .await()
            paquetes.addAll(paquetesSnapshot.documents.map { p ->
                val estado = when (p.getLong("estado") ?: 0L) {
                    0L -> "Salió del centro de distribución"
                    1L -> "Recepcionado por empresa transportista"
                    2L -> "En reparto"
                    3L -> "Entregado:"
                    4L -> "Falla en entrega, devuelto a empresa transportista"
                    5L -> "Falla en entrega, devuelto al vendedor"
                    else -> "Estado desconocido"
                }
                val paq = Paquete(
                    id = p.id,
                    contacto = p.getString("contacto") ?: "",
                    direccion = p.getString("direccion") ?: "",
                    receptor = p.getString("receptor") ?: "",
                    ruta = p.getString("ruta") ?: "",
                    estado = estado,
                    coordenadas = (p.get("coordenadas") as? List<*>)?.mapNotNull { it as? Double }
                        ?: emptyList(),
                    referencia = p.getString("referencia") ?: ""
                )
                paq
            })
        }
        val paquetesPorRuta = paquetes.groupBy { it.ruta }
        rutas.forEach { ruta ->
            ruta.paquetes = paquetesPorRuta[ruta.id] ?: emptyList()
        }
        return rutas
    }
    suspend fun esTransportista(user: FirebaseUser): Boolean {
        return try {
            val document = db.collection("Usuarios").document(user.uid).get().await()
            val tipo = document.get("tipo") as? Long
            db.collection("Usuarios").document(user.uid)
                .update("ultimaConexion", Timestamp(Date()))

            tipo == 0L
        } catch (e: Exception) {
            Log.d(TAG, "Error fetching document: ", e)
            false
        }
    }
    suspend fun terminarRutas(rutas: List<Ruta>) {
        rutas.forEach {
            val data = hashMapOf(
                "en_reparto" to false,
                "activa" to false,
                "cargado" to false,
                "completado" to true,
            )
            db.collection("Rutas").document(it.id)
                .set(data, SetOptions.merge()).await()
        }
    }
    suspend fun cargarRuta(rutaID: String) {
        db.collection("Rutas").document(rutaID)
            .set(hashMapOf("cargado" to true), SetOptions.merge()).await()
    }
    suspend fun comenzarEntregas(rutas: List<String>, paquetes: List<String>) {
        val batch: WriteBatch = db.batch()
        rutas.forEach { rutaID ->
            val docRef = db.collection("Rutas").document(rutaID)
            batch.update(docRef, "en_reparto", true)
        }
        val fecha = Timestamp(Date())
        paquetes.forEach { paqueteID ->
            val docRef = db.collection("Paquetes").document(paqueteID)
            val historial = hashMapOf(
                "detalles" to "Saliendo a repartir tu pedido!",
                "estado" to 2,
                "fecha" to fecha
            )
            batch.update(docRef, "estado", 2, "historial", FieldValue.arrayUnion(historial))
        }
        batch.commit().await()
    }
    suspend fun entregarPaquete(paqueteID: String, data: Map<String, Any>) {
        val nombre = data["nombre"]
        val rut = data["rut"]
        var tel = data["telefono"]
        if (tel != "") {
            tel = ", telefono: $tel"
        }
        val historial = hashMapOf(
            "detalles" to "Entregado a $nombre$tel, RUT:$rut",
            "estado" to 3,
            "fecha" to Timestamp(Date())
        )
        db.collection("Paquetes").document(paqueteID)
            .update("estado", 3, "historial", FieldValue.arrayUnion(historial))
    }
    suspend fun entregaFallidaPaquete(paqueteID:String, motivo:String) {
        val historial = hashMapOf(
            "detalles" to motivo,
            "estado" to 4,
            "fecha" to Timestamp(Date())
        )

        db.collection("Paquetes").document(paqueteID)
            .update("estado", 4, "ruta", "", "historial", FieldValue.arrayUnion(historial))
    }
}