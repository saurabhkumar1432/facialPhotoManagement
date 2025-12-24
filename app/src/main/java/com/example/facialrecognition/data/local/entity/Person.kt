package com.example.facialrecognition.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "people")
data class Person(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String = "Unknown",
    val coverPhotoId: Long? = null, // ID of the representative photo/face
    val avatarUri: String? = null // URI of the cropped face thumbnail
)
