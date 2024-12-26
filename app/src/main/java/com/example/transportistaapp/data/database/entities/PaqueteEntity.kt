package com.example.transportistaapp.data.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.transportistaapp.domain.model.Paquete
import java.util.Date


@Entity(
    tableName = "paquetes_table",
    foreignKeys = [ForeignKey(
        entity = RutaEntity::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("ruta"),
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index(value = ["ruta"])] // Agrega un índice para la columna 'ruta'
)
data class PaqueteEntity(
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "id") val id: String,
    @ColumnInfo(name = "receptor") val receptor: String,
    @ColumnInfo(name = "fono") val fono: String,
    @ColumnInfo(name = "direccion") val direccion: String,
    @ColumnInfo(name = "referencia") val referencia: String,
    @ColumnInfo(name = "estado") var estado: Int,
    @ColumnInfo(name = "ruta") val ruta: String,
    @ColumnInfo(name = "fechaEntrega") val fecha: Date = Date(),
    @ColumnInfo(name = "detalles") val detalles: String = "",
    @ColumnInfo(name = "coordenadas") val coordenadas: List<Double>,
)

fun PaqueteEntity.toDomain(): Paquete {
    return Paquete(
        id = id,
        contacto = fono,
        direccion = direccion,
        receptor = receptor,
        ruta = ruta,
        estado = when (estado) {
            0 -> "Salió del centro de distribución"
            1 -> "Recepcionado por empresa transportista"
            2 -> "En reparto"
            3 -> "Entregado:"
            4 -> "Falla en entrega, devuelto a empresa transportista"
            5 -> "Falla en entrega, devuelto al vendedor"
            else -> "Estado desconocido? no deberías estar viendo esto..."
        },
        detalles = detalles,
        coordenadas = coordenadas,
        referencia = referencia
    )
}

fun Paquete.toRoom(): PaqueteEntity {
    return PaqueteEntity(
        id = id,
        receptor = receptor,
        fono = contacto,
        direccion = direccion,
        estado = when (estado) {
            "Salió del centro de distribución" -> 0
            "Recepcionado por empresa transportista" -> 1
            "En reparto" -> 2
            "Entregado:" -> 3
            "Falla en entrega, devuelto a empresa transportista" -> 4
            "Falla en entrega, devuelto al vendedor" -> 5
            else -> 999999999
        },
        ruta = ruta,
        fecha = fecha,
        detalles = detalles,
        coordenadas = coordenadas,
        referencia = referencia
    )
}