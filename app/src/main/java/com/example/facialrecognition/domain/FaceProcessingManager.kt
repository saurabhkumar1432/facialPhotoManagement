package com.example.facialrecognition.domain

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Rect
import android.util.Log
import com.example.facialrecognition.data.local.dao.FaceDao
import com.example.facialrecognition.data.local.dao.PersonDao
import com.example.facialrecognition.data.local.dao.PhotoDao
import com.example.facialrecognition.data.local.entity.Face
import com.example.facialrecognition.data.local.entity.Person
import com.example.facialrecognition.data.local.entity.Photo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.math.max

// Manager class
class FaceProcessingManager(
    private val context: Context,
    private val photoDao: PhotoDao,
    private val faceDao: FaceDao,
    private val personDao: PersonDao
) {
    private val detector = FaceDetector()
    private val embedder = FaceEmbedder(context)
    private val clustering = FaceClustering()
    private val tag = "FaceProcessingManager"

    suspend fun processUnprocessedPhotos() = withContext(Dispatchers.IO) {
        val photos = photoDao.getUnprocessedPhotos()
        Log.d(tag, "Processing ${photos.size} unprocessed photos")
        
        // Load all existing clusters
        val allPeople = personDao.getAllPeopleList()
        val existingClusters = mutableListOf<Pair<Person, MutableList<Face>>>()
        
        for (person in allPeople) {
            val faces = faceDao.getFacesForPerson(person.id).toMutableList()
            existingClusters.add(person to faces)
        }
        
        photos.forEach { photo ->
            try {
                processPhoto(photo, existingClusters)
            } catch (e: Exception) {
                Log.e(tag, "Error processing photo ${photo.id}", e)
                e.printStackTrace()
            }
        }
    }

    private suspend fun processPhoto(photo: Photo, clusters: MutableList<Pair<Person, MutableList<Face>>>) {
        val bitmap = loadBitmap(context, photo.uri) ?: return
        Log.d(tag, "Photo ${photo.id} uri=${photo.uri} size=${bitmap.width}x${bitmap.height}")

        val facesRects = detector.detectFaces(bitmap)
        Log.d(tag, "Photo ${photo.id}: detected ${facesRects.size} faces")
        
        for (rect in facesRects) {
            Log.d(tag, "Photo ${photo.id}: processing face at ${rect.toShortString()}")
            // Crop face
            val faceBitmap = cropFaceSquare(bitmap, rect)
            if (faceBitmap == null) {
                Log.w(tag, "Photo ${photo.id}: cropFaceSquare returned null for rect ${rect.toShortString()}")
                continue
            }
            Log.d(tag, "Photo ${photo.id}: cropped face ${faceBitmap.width}x${faceBitmap.height}")
            
            // Get embedding
            val embedding = embedder.getEmbedding(faceBitmap)
            if (embedding == null) {
                Log.w(tag, "Photo ${photo.id}: embedding null for rect ${rect.toShortString()}")
                continue
            }
            
            // Cluster
            // We pass the current state of clusters
            val personId = clustering.clusterFace(
                Face(
                    photoId = photo.id,
                    embedding = embedding,
                    boundingBoxJson = rectToNormalizedJson(rect, bitmap)
                ), // Temporary face for matching
                clusters
            )
            Log.d(tag, "Photo ${photo.id}: face clustered to person=${personId ?: "NEW"}")
            
            val finalPersonId = if (personId != null) {
                personId
            } else {
                // Create new person
                val newPerson = Person(name = "Person ${clusters.size + 1}")
                val newId = personDao.insert(newPerson)
                val p = newPerson.copy(id = newId)
                clusters.add(p to mutableListOf())
                newId
            }
            
            // Save Face
            val face = Face(
                photoId = photo.id,
                personId = finalPersonId,
                embedding = embedding,
                boundingBoxJson = rectToNormalizedJson(rect, bitmap)
            )
            val faceId = faceDao.insert(face)
            val persistedFace = face.copy(id = faceId)
            Log.d(tag, "Photo ${photo.id}: saved faceId=$faceId personId=$finalPersonId")
            
            // Update in-memory clusters for subsequent faces in this batch
            val clusterIndex = clusters.indexOfFirst { it.first.id == finalPersonId }
            if (clusterIndex != -1) {
                clusters[clusterIndex].second.add(persistedFace)
            }
            
            // Set cover photo if none
            val personIndex = clusters.indexOfFirst { it.first.id == finalPersonId }
            if (personIndex != -1 && clusters[personIndex].first.coverPhotoId == null) {
                val existingPerson = clusters[personIndex].first
                val updatedPerson = existingPerson.copy(coverPhotoId = faceId)
                personDao.update(updatedPerson)
                clusters[personIndex] = updatedPerson to clusters[personIndex].second
            }
        }
        
        // Mark photo as processed
        photoDao.update(photo.copy(isProcessed = true))
    }

    private fun loadBitmap(context: Context, uriString: String): android.graphics.Bitmap? {
        return try {
            val uri = android.net.Uri.parse(uriString)
            val inputStream = context.contentResolver.openInputStream(uri)
            val originalBitmap = BitmapFactory.decodeStream(inputStream)
            inputStream?.close()

            if (originalBitmap == null) return null

            // Handle Rotation
            val inputStreamForExif = context.contentResolver.openInputStream(uri)
            val exif = inputStreamForExif?.let { androidx.exifinterface.media.ExifInterface(it) }
            val orientation = exif?.getAttributeInt(
                androidx.exifinterface.media.ExifInterface.TAG_ORIENTATION,
                androidx.exifinterface.media.ExifInterface.ORIENTATION_NORMAL
            ) ?: androidx.exifinterface.media.ExifInterface.ORIENTATION_NORMAL
            inputStreamForExif?.close()

            val matrix = android.graphics.Matrix()
            when (orientation) {
                androidx.exifinterface.media.ExifInterface.ORIENTATION_ROTATE_90 -> matrix.postRotate(90f)
                androidx.exifinterface.media.ExifInterface.ORIENTATION_ROTATE_180 -> matrix.postRotate(180f)
                androidx.exifinterface.media.ExifInterface.ORIENTATION_ROTATE_270 -> matrix.postRotate(270f)
            }

            // Only create new bitmap if rotation is needed
            if (orientation != androidx.exifinterface.media.ExifInterface.ORIENTATION_NORMAL) {
                Bitmap.createBitmap(
                    originalBitmap, 0, 0, originalBitmap.width, originalBitmap.height, matrix, true
                )
            } else {
                originalBitmap
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    
    private fun cropFaceSquare(bitmap: Bitmap, rect: Rect): Bitmap? {
        // Expand slightly and force a square crop to keep aspect consistent for the embedder
        val marginScale = 0.2f
        val halfSize = (max(rect.width(), rect.height()) * (0.5f + marginScale)).toInt()
        val cx = rect.centerX().toFloat()
        val cy = rect.centerY().toFloat()

        val left = (cx - halfSize).toInt().coerceAtLeast(0)
        val top = (cy - halfSize).toInt().coerceAtLeast(0)
        val right = (cx + halfSize).toInt().coerceAtMost(bitmap.width)
        val bottom = (cy + halfSize).toInt().coerceAtMost(bitmap.height)

        if (right <= left || bottom <= top) return null
        return Bitmap.createBitmap(bitmap, left, top, right - left, bottom - top)
    }

    private fun rectToNormalizedJson(rect: Rect, bitmap: Bitmap): String {
        val width = bitmap.width.toFloat().coerceAtLeast(1f)
        val height = bitmap.height.toFloat().coerceAtLeast(1f)

        val l = rect.left / width
        val t = rect.top / height
        val r = rect.right / width
        val b = rect.bottom / height

        return "{\"l\":$l,\"t\":$t,\"r\":$r,\"b\":$b}"
    }
}
