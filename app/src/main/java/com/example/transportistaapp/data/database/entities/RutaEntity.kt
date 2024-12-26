package com.example.transportistaapp.data.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.transportistaapp.domain.model.Ruta


@Entity(tableName = "rutas_table")
data class RutaEntity(
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo("id") val id : String,
    @ColumnInfo("alias") val alias: String,
    @ColumnInfo("en_reparto") var enReparto: Boolean,
    @ColumnInfo("cargado") val cargado: Boolean,
    @ColumnInfo("completado") val completado: Boolean,
)

fun RutaEntity.toDomain() : Ruta {
    return Ruta(
        id = id,
        nombre = alias,
        cargado = cargado,
        enReparto = enReparto,
        completado = completado
    )
}
fun Ruta.toRoom() :RutaEntity {
    return RutaEntity(
        id = id,
        alias = nombre,
        enReparto = enReparto,
        cargado = cargado,
        completado = completado
    )
}