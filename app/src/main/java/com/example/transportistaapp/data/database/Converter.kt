package com.example.transportistaapp.data.database

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.Date

class Converter {
    // Manejo de Date a Long y viceversa
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }
    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }
    // Manejo de Array<Number> a String (JSON) y viceversa
    @TypeConverter
    fun fromNumberArray(numbers: Array<Number>?): String? {
        return numbers?.let { Gson().toJson(it) }
    }
    @TypeConverter
    fun toNumberArray(numbersJson: String?): Array<Number>? {
        return numbersJson?.let { Gson().fromJson(it, Array<Number>::class.java) }
    }

    @TypeConverter
    fun fromDoubleList(numbers: List<Double>?): String? {
        return numbers?.let { Gson().toJson(it) }
    }
    @TypeConverter
    fun toDoubleList(numbersJson: String?): List<Double>? {
        return numbersJson?.let { Gson().fromJson(it, object : TypeToken<List<Double>>() {}.type) }
    }
}
