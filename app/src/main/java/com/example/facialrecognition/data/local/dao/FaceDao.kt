package com.example.facialrecognition.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.facialrecognition.data.local.entity.Face
import kotlinx.coroutines.flow.Flow

@Dao
interface FaceDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(face: Face): Long

    @Update
    suspend fun update(face: Face)

    @Query("SELECT * FROM faces WHERE personId = :personId")
    suspend fun getFacesForPerson(personId: Long): List<Face>
    
    @Query("SELECT * FROM faces")
    suspend fun getAllFaces(): List<Face>

    @Query("SELECT * FROM faces WHERE photoId = :photoId")
    suspend fun getFacesForPhoto(photoId: Long): List<Face>

    @Query("SELECT COUNT(*) FROM faces")
    fun getFaceCount(): Flow<Int>
}
