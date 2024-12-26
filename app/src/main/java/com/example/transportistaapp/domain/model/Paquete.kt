package com.example.transportistaapp.domain.model

import java.util.Date


data class Paquete (
    val id : String,
    val direccion : String,
    val referencia : String,
    val ruta : String,
    var contacto : String,
    var receptor : String,
    var estado : String,
    var fecha : Date = Date(),
    var detalles : String = "",
    var validado : Boolean = false,
    val coordenadas : List<Double>
)

