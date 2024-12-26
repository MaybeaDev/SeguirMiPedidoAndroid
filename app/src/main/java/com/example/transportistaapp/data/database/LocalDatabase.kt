package com.example.transportistaapp.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.transportistaapp.data.database.dao.LastLoginDao
import com.example.transportistaapp.data.database.dao.PaqueteDao
import com.example.transportistaapp.data.database.dao.RutaDao
import com.example.transportistaapp.data.database.entities.LastLogin
import com.example.transportistaapp.data.database.entities.PaqueteEntity
import com.example.transportistaapp.data.database.entities.RutaEntity


@Database(
    entities = [
        PaqueteEntity::class,
        RutaEntity::class,
        LastLogin::class,
    ],
    version = 6,
    exportSchema = true,

)
@TypeConverters(Converter::class)
abstract class LocalDatabase : RoomDatabase() {

    abstract fun getPaqueteDao():PaqueteDao

    abstract fun getRutaDao():RutaDao

    abstract fun getLastLoginDao(): LastLoginDao
}