package com.example.transportistaapp.data.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "lastLoginTable")
data class LastLogin(
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo("uid") val uid: String,
)