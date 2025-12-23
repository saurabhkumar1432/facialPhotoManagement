package com.example.facialrecognition.domain

import android.graphics.Bitmap
import android.graphics.Rect
import android.util.Log
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions
import kotlinx.coroutines.tasks.await
import kotlin.math.max

class FaceDetector {
    companion object {
        private const val TAG = "FaceDetector"
        // ML Kit works best with images around 480-1024px on the short side
        private const val MAX_IMAGE_DIMENSION = 1024
    }

    // Primary detector - accurate mode, no tracking (tracking is for video only)
    private val accurateOptions = FaceDetectorOptions.Builder()
        .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
        .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
        .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_NONE)
        .setContourMode(FaceDetectorOptions.CONTOUR_MODE_NONE)
        .setMinFaceSize(0.1f) // 10% of image width minimum
        .build()

    // Fallback detector - fast mode for potentially missed faces
    private val fastOptions = FaceDetectorOptions.Builder()
        .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
        .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_NONE)
        .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_NONE)
        .setContourMode(FaceDetectorOptions.CONTOUR_MODE_NONE)
        .setMinFaceSize(0.15f) // slightly larger for fast mode reliability
        .build()

    private val detectorAccurate = FaceDetection.getClient(accurateOptions)
    private val detectorFast = FaceDetection.getClient(fastOptions)

    suspend fun detectFaces(bitmap: Bitmap): List<Rect> {
        // Downscale large images for better ML Kit performance
        val (processedBitmap, scale) = prepareImage(bitmap)
        Log.d(TAG, "Original: ${bitmap.width}x${bitmap.height}, Processed: ${processedBitmap.width}x${processedBitmap.height}, scale=$scale")

        val image = InputImage.fromBitmap(processedBitmap, 0)
        
        // Try accurate detector first
        try {
            val facesAcc = detectorAccurate.process(image).await()
            Log.d(TAG, "Accurate detector returned ${facesAcc.size} faces")
            if (facesAcc.isNotEmpty()) {
                val boxes = facesAcc.map { face ->
                    // Scale bounding boxes back to original image coordinates
                    scaleRect(face.boundingBox, scale)
                }
                Log.d(TAG, "detectFaces accurate: ${boxes.size} faces, boxes=$boxes")
                return boxes
            }
        } catch (e: Exception) {
            Log.e(TAG, "detectFaces accurate failed", e)
        }

        // Fallback to FAST mode
        try {
            val facesFast = detectorFast.process(image).await()
            Log.d(TAG, "Fast detector returned ${facesFast.size} faces")
            val boxes = facesFast.map { face ->
                scaleRect(face.boundingBox, scale)
            }
            Log.d(TAG, "detectFaces fast fallback: ${boxes.size} faces")
            return boxes
        } catch (e: Exception) {
            Log.e(TAG, "detectFaces fast failed", e)
        }

        return emptyList()
    }

    private fun prepareImage(bitmap: Bitmap): Pair<Bitmap, Float> {
        val maxDim = max(bitmap.width, bitmap.height)
        if (maxDim <= MAX_IMAGE_DIMENSION) {
            return Pair(bitmap, 1f)
        }

        val scale = MAX_IMAGE_DIMENSION.toFloat() / maxDim
        val newWidth = (bitmap.width * scale).toInt()
        val newHeight = (bitmap.height * scale).toInt()

        val scaled = Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true)
        return Pair(scaled, 1f / scale) // Return inverse scale for mapping back
    }

    private fun scaleRect(rect: Rect, scale: Float): Rect {
        return Rect(
            (rect.left * scale).toInt(),
            (rect.top * scale).toInt(),
            (rect.right * scale).toInt(),
            (rect.bottom * scale).toInt()
        )
    }
}
