package com.example.facialrecognition.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "photos",
    indices = [Index(value = ["uri"], unique = true)]
)
data class Photo(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val uri: String,
    val dateAdded: Long,
    val isProcessed: Boolean = false // True if ML scanning is done
)

