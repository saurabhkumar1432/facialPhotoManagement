package com.example.facialrecognition.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.facialrecognition.data.local.entity.Photo
import kotlinx.coroutines.flow.Flow

@Dao
interface PhotoDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(photo: Photo): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(photos: List<Photo>)

    @Update
    suspend fun update(photo: Photo)

    @Query("SELECT * FROM photos ORDER BY dateAdded DESC")
    fun getAllPhotos(): Flow<List<Photo>>

    @Query("SELECT * FROM photos WHERE isProcessed = 0")
    suspend fun getUnprocessedPhotos(): List<Photo>
    
    @Query("SELECT * FROM photos WHERE uri = :uri LIMIT 1")
    suspend fun getPhotoByUri(uri: String): Photo?

    @Query("SELECT photos.* FROM photos INNER JOIN faces ON photos.id = faces.photoId WHERE faces.personId = :personId ORDER BY photos.dateAdded DESC")
    suspend fun getPhotosForPerson(personId: Long): List<Photo>

    @Query("SELECT COUNT(*) FROM photos")
    fun getPhotoCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM photos WHERE isProcessed = 1")
    fun getProcessedPhotoCount(): Flow<Int>

    @Query("SELECT * FROM photos ORDER BY dateAdded DESC LIMIT :limit")
    fun getRecentPhotos(limit: Int): Flow<List<Photo>>

    @Query("UPDATE photos SET isProcessed = 0")
    suspend fun resetProcessedStatus()
}
