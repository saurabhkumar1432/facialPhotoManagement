package com.example.facialrecognition.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.facialrecognition.data.local.AppDatabase
import com.example.facialrecognition.data.repository.ImageRepository
import com.example.facialrecognition.domain.FaceProcessingManager

class ScanWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return try {
            val database = AppDatabase.getDatabase(applicationContext)
            val imageRepository = ImageRepository(
                applicationContext.contentResolver,
                database.photoDao()
            )
            val processingManager = FaceProcessingManager(
                applicationContext,
                database.photoDao(),
                database.faceDao(),
                database.personDao()
            )

            // 1. Scan for new images
            imageRepository.scanDeviceImages()

            // 2. Process unprocessed images (Detect & Cluster)
            processingManager.processUnprocessedPhotos()

            Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            Result.retry()
        }
    }
}
