package com.example.facialrecognition.data.repository

import android.content.ContentResolver
import android.content.ContentUris
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import com.example.facialrecognition.data.local.dao.PhotoDao
import com.example.facialrecognition.data.local.entity.Photo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Date

class ImageRepository(
    private val contentResolver: ContentResolver,
    private val photoDao: PhotoDao
) {
    // Determine how far back to look or just scan everything?
    // For efficiency, we scan everything but only insert what's missing.
    // Optimization: In a real app, query MediaStore filtering by DATE_ADDED > max(dateAdded in DB)
    
    suspend fun scanDeviceImages(): Int {
        return withContext(Dispatchers.IO) {
            val projection = arrayOf(
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.DATE_ADDED
            )
            
            val sortOrder = "${MediaStore.Images.Media.DATE_ADDED} DESC"
            
            val cursor: Cursor? = contentResolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                projection,
                null,
                null,
                sortOrder
            )
            
            android.util.Log.d("ImageRepository", "Scanning started. Cursor null? ${cursor == null}. Count: ${cursor?.count}")
            
            var newPhotosCount = 0
            
            cursor?.use {
                val idColumn = it.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
                val dateColumn = it.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_ADDED)
                
                while (it.moveToNext()) {
                    val id = it.getLong(idColumn)
                    val dateAdded = it.getLong(dateColumn) * 1000 // Convert to ms
                    
                    val contentUri: Uri = ContentUris.withAppendedId(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        id
                    )
                    val uriString = contentUri.toString()
                    
                    // Check if exists
                    // Note: Doing this one by one is slow if DB is empty. 
                    // Better to batch check or rely on unique constraint (but we have auto-inc ID, so checks are needed on URI)
                    if (photoDao.getPhotoByUri(uriString) == null) {
                        try {
                            val photo = Photo(
                                uri = uriString,
                                dateAdded = dateAdded,
                                isProcessed = false
                            )
                            photoDao.insert(photo)
                            newPhotosCount++
                            if (newPhotosCount % 10 == 0) {
                                android.util.Log.d("ImageRepository", "Inserted $newPhotosCount photos so far")
                            }
                        } catch (e: Exception) {
                            android.util.Log.e("ImageRepository", "Error inserting photo: $uriString", e)
                        }
                    }
                }
            }
            android.util.Log.d("ImageRepository", "Scan complete. Found $newPhotosCount new photos.")
            newPhotosCount
        }
    }
}
