package com.example.transportistaapp.data.database.dao

import androidx.room.Dao
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.transportistaapp.data.database.entities.LastLogin

@Dao
interface LastLoginDao {

    @Query("Select uid FROM lastLoginTable LIMIT 1")
    suspend fun get(): LastLogin?

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun set(uid: LastLogin)
}