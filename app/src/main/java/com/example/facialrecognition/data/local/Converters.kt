package com.example.facialrecognition.data.local

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.Date

class Converters {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }

    @TypeConverter
    fun fromFloatArray(value: String?): FloatArray? {
        return value?.let {
            Gson().fromJson(it, FloatArray::class.java)
        }
    }

    @TypeConverter
    fun floatArrayToString(value: FloatArray?): String? {
        return value?.let {
            Gson().toJson(it)
        }
    }
}
