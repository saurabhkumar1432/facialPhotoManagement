package com.example.facialrecognition.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "faces",
    foreignKeys = [
        ForeignKey(
            entity = Photo::class,
            parentColumns = ["id"],
            childColumns = ["photoId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Person::class,
            parentColumns = ["id"],
            childColumns = ["personId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [Index("photoId"), Index("personId")]
)
data class Face(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val photoId: Long,
    val personId: Long? = null,
    val embedding: FloatArray? = null, // The TFLite vector, stored as JSON string via Converter
    val boundingBoxJson: String? = null // Normalized rect logic or just JSON
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Face

        if (id != other.id) return false
        if (photoId != other.photoId) return false
        if (personId != other.personId) return false
        if (embedding != null) {
            if (other.embedding == null) return false
            if (!embedding.contentEquals(other.embedding)) return false
        } else if (other.embedding != null) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + photoId.hashCode()
        result = 31 * result + (personId?.hashCode() ?: 0)
        result = 31 * result + (embedding?.contentHashCode() ?: 0)
        return result
    }
}
