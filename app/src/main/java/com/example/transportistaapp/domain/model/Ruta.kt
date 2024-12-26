package com.example.transportistaapp.domain.model


data class Ruta(
    val id: String,
    val nombre: String,
    val enReparto: Boolean,  // Asegúrate de que esta propiedad esté definida
    val completado: Boolean,
    val cargado: Boolean,        // Cuando ya se entregan todos los paquetes, o se termina la ruta, esto cambia a true
    var paquetes : List<Paquete> = emptyList()
)
