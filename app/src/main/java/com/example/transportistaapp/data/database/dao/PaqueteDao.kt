package com.example.transportistaapp.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.transportistaapp.data.database.entities.PaqueteEntity
import java.util.Date


@Dao
interface PaqueteDao {

    @Query("Select * FROM paquetes_table ORDER BY ruta DESC")
    suspend fun getAll():List<PaqueteEntity>

    @Query("Select * FROM paquetes_table WHERE ruta=:ruta")
    suspend fun obtenerPorRuta(ruta:String):List<PaqueteEntity>

    @Query("Select * FROM paquetes_table WHERE ruta=:ruta AND (estado = 1 OR estado = 4)")
    suspend fun obtenerPorRutaParaEntregar(ruta:String):List<PaqueteEntity>


    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun update(paquete: PaqueteEntity)

    @Query("Select * FROM paquetes_table WHERE id = :id LIMIT 1")
    suspend fun get(id: String): PaqueteEntity

    @Query("Select p.* FROM paquetes_table p JOIN rutas_table r ON r.id = p.ruta WHERE r.en_reparto = 1")
    suspend fun obtenerEnReparto() : List<PaqueteEntity>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(paquetes:List<PaqueteEntity>)

    @Query("UPDATE paquetes_table SET fechaEntrega=:fechaEntrega, estado=3 WHERE id=:paqueteId")
    suspend fun entregar(paqueteId:String, fechaEntrega:Date)

    @Query("UPDATE paquetes_table SET estado=4, detalles=:motivo WHERE id=:paqueteId")
    suspend fun entregaFallida(paqueteId:String, motivo:String)

    @Query("DELETE FROM paquetes_table")
    suspend fun deleteAll()
}

