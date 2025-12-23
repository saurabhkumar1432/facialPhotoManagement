package com.example.facialrecognition.domain

import android.content.Context
import android.graphics.Bitmap
import org.tensorflow.lite.DataType
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.support.common.FileUtil
import org.tensorflow.lite.support.common.ops.NormalizeOp
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.image.ops.ResizeOp
import kotlin.math.sqrt

class FaceEmbedder(context: Context) {
    private var interpreter: Interpreter? = null
    private val modelName = "mobile_facenet.tflite" // User must provide this
    private val inputSize = 112 // MobileFaceNet standard
    private val outputSize = 192 // MobileFaceNet embedding size

    init {
        try {
            android.util.Log.d("FaceEmbedder", "Initializing FaceEmbedder with model: $modelName")
            val model = FileUtil.loadMappedFile(context, modelName)
            val options = Interpreter.Options().apply {
                // Two threads keeps things responsive without overloading the device
                setNumThreads(2)
            }
            interpreter = Interpreter(model, options)
            
            // Log model input/output details
            val inputTensor = interpreter?.getInputTensor(0)
            val outputTensor = interpreter?.getOutputTensor(0)
            android.util.Log.d("FaceEmbedder", "Input tensor: shape=${inputTensor?.shape()?.contentToString()}, type=${inputTensor?.dataType()}, bytes=${inputTensor?.numBytes()}")
            android.util.Log.d("FaceEmbedder", "Output tensor: shape=${outputTensor?.shape()?.contentToString()}, type=${outputTensor?.dataType()}, bytes=${outputTensor?.numBytes()}")
            
            android.util.Log.d("FaceEmbedder", "FaceEmbedder initialized successfully")
        } catch (e: Exception) {
            android.util.Log.e("FaceEmbedder", "Error initializing FaceEmbedder", e)
            e.printStackTrace()
            // Model not found or invalid
        }
    }

    fun getEmbedding(faceBitmap: Bitmap): FloatArray? {
        if (interpreter == null) {
            android.util.Log.e("FaceEmbedder", "getEmbedding: interpreter is null")
            return null
        }
        
        android.util.Log.d("FaceEmbedder", "getEmbedding: face bitmap ${faceBitmap.width}x${faceBitmap.height}")

        try {
            // Resize face to model input size
            val resizedBitmap = Bitmap.createScaledBitmap(faceBitmap, inputSize, inputSize, true)
            
            // Model expects batch size 2: shape [2, 112, 112, 3]
            // We'll duplicate the same face for both batch entries
            val batchSize = 2
            val inputBuffer = java.nio.ByteBuffer.allocateDirect(batchSize * inputSize * inputSize * 3 * 4)
            inputBuffer.order(java.nio.ByteOrder.nativeOrder())
            
            val pixels = IntArray(inputSize * inputSize)
            resizedBitmap.getPixels(pixels, 0, inputSize, 0, 0, inputSize, inputSize)
            
            // Fill both batch entries with the same face data
            repeat(batchSize) {
                for (pixel in pixels) {
                    // Extract RGB and normalize to [-1, 1]
                    val r = ((pixel shr 16) and 0xFF) / 127.5f - 1f
                    val g = ((pixel shr 8) and 0xFF) / 127.5f - 1f
                    val b = (pixel and 0xFF) / 127.5f - 1f
                    inputBuffer.putFloat(r)
                    inputBuffer.putFloat(g)
                    inputBuffer.putFloat(b)
                }
            }
            inputBuffer.rewind()
            
            android.util.Log.d("FaceEmbedder", "getEmbedding: input buffer capacity=${inputBuffer.capacity()}")
            
            // Output shape is [2, 192] - we'll take the first embedding
            val outputArray = Array(batchSize) { FloatArray(outputSize) }
            interpreter?.run(inputBuffer, outputArray)
            val raw = outputArray[0]  // Take first embedding
            android.util.Log.d("FaceEmbedder", "getEmbedding: success, first values=${raw.take(3)}")
            return normalizeL2(raw)
        } catch (e: Exception) {
            android.util.Log.e("FaceEmbedder", "getEmbedding: inference failed", e)
            e.printStackTrace()
        }
        return null
    }

    // Keep embeddings on the unit sphere so distance comparisons remain stable
    private fun normalizeL2(vector: FloatArray): FloatArray {
        var norm = 0f
        for (v in vector) {
            norm += v * v
        }
        norm = sqrt(norm.coerceAtLeast(1e-12f))
        return FloatArray(vector.size) { idx -> vector[idx] / norm }
    }

    fun close() {
        interpreter?.close()
    }
}
