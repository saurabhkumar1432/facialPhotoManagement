package com.example.facialrecognition.domain

import com.example.facialrecognition.data.local.entity.Face
import com.example.facialrecognition.data.local.entity.Person
import kotlin.math.sqrt

class FaceClustering {
    // Threshold tuned for L2 distance on L2-normalized MobileFaceNet embeddings
    private val threshold = 1.05f

    // Returns the Person ID (existing or new)
    fun clusterFace(face: Face, existingPeoples: List<Pair<Person, List<Face>>>): Long? {
        val embedding = face.embedding ?: return null

        var bestMatchPersonId: Long? = null
        var minDistance = Float.MAX_VALUE

        // Prefer comparing against a centroid to avoid noisy single samples
        for ((person, faces) in existingPeoples) {
            val centroid = computeCentroid(faces)

            if (centroid != null) {
                val dist = l2Distance(embedding, centroid)
                if (dist < minDistance) {
                    minDistance = dist
                    bestMatchPersonId = person.id
                }
                continue
            }

            // Fallback: compare each face if no centroid is available yet
            for (pFace in faces) {
                val pEmbedding = pFace.embedding ?: continue
                val dist = l2Distance(embedding, pEmbedding)
                if (dist < minDistance) {
                    minDistance = dist
                    bestMatchPersonId = person.id
                }
            }
        }

        return if (minDistance < threshold) {
            bestMatchPersonId
        } else {
            null // Needs new person
        }
    }

    private fun l2Distance(v1: FloatArray, v2: FloatArray): Float {
        var sum = 0.0f
        for (i in v1.indices) {
            val diff = v1[i] - v2[i]
            sum += diff * diff
        }
        return sqrt(sum)
    }

    private fun computeCentroid(faces: List<Face>): FloatArray? {
        if (faces.isEmpty()) return null

        val firstSize = faces.first().embedding?.size ?: return null
        val accumulator = FloatArray(firstSize)
        var count = 0

        for (face in faces) {
            val emb = face.embedding ?: continue
            if (emb.size != firstSize) continue
            for (i in emb.indices) {
                accumulator[i] += emb[i]
            }
            count++
        }

        if (count == 0) return null

        val centroid = FloatArray(firstSize) { idx -> accumulator[idx] / count }
        return normalizeL2(centroid)
    }

    private fun normalizeL2(vector: FloatArray): FloatArray {
        var norm = 0f
        for (v in vector) {
            norm += v * v
        }
        norm = sqrt(norm.coerceAtLeast(1e-12f))
        return FloatArray(vector.size) { idx -> vector[idx] / norm }
    }
}
