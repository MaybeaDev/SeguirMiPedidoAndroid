package com.example.transportistaapp.data.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.transportistaapp.data.database.entities.RutaEntity


@Dao
interface RutaDao {

    @Query("Select * FROM rutas_table")
    suspend fun getAll(): List<RutaEntity>

    @Query("Select * FROM rutas_table WHERE cargado = 1")
    suspend fun getCargadas(): List<RutaEntity>

    @Query("Select * FROM rutas_table WHERE en_reparto=1")
    suspend fun rutasEnReparto(): List<RutaEntity>

    @Query("UPDATE rutas_table SET cargado=1 WHERE id=:ruta")
    suspend fun cargarRuta(ruta: String)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateRuta(rutaEntity: RutaEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(rutas: List<RutaEntity>)

    @Query("DELETE FROM rutas_table")
    suspend fun deleteAll()

    @Delete(entity = RutaEntity::class)
    suspend fun terminar(ruta: RutaEntity)
}