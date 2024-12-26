package com.example.transportistaapp.di

import android.content.Context
import androidx.room.Room
import com.example.transportistaapp.data.database.LocalDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object RoomModule {
    private const val DATABASE_NAME = "LocalDatabase"

    @Singleton
    @Provides
    fun proveerRoom(@ApplicationContext context: Context) =
        Room.databaseBuilder(context, LocalDatabase::class.java, DATABASE_NAME)
            .fallbackToDestructiveMigration().build()

    @Singleton
    @Provides
    fun proveerPaqueteDao(db: LocalDatabase) = db.getPaqueteDao()

    @Singleton
    @Provides
    fun proveerRutaDao(db: LocalDatabase) = db.getRutaDao()

    @Singleton
    @Provides
    fun proveerLastLoginDao(db: LocalDatabase) = db.getLastLoginDao()

}