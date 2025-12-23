package com.example.facialrecognition.ui.progress

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.facialrecognition.data.local.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

data class ProgressUiState(
    val totalPhotoCount: Int = 0,
    val processedPhotoCount: Int = 0,
    val faceCount: Int = 0,
    val peopleCount: Int = 0,
    val isScanning: Boolean = false
)

class ProgressViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getDatabase(application)
    private val photoDao = database.photoDao()
    private val faceDao = database.faceDao()
    private val personDao = database.personDao()

    private val _isScanning = kotlinx.coroutines.flow.MutableStateFlow(false)

    val uiState: StateFlow<ProgressUiState> = combine(
        photoDao.getPhotoCount(),
        photoDao.getProcessedPhotoCount(),
        faceDao.getFaceCount(),
        personDao.getPeopleCount(),
        _isScanning
    ) { photoCount, processedCount, faceCount, peopleCount, isScanning ->
        ProgressUiState(
            totalPhotoCount = photoCount,
            processedPhotoCount = processedCount,
            faceCount = faceCount,
            peopleCount = peopleCount,
            isScanning = isScanning
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ProgressUiState()
    )

    fun startManualScan() {
        viewModelScope.launch(Dispatchers.IO) {
            _isScanning.value = true
            try {
                // Manual trigger via Repository for immediate feedback
                val imageRepository = com.example.facialrecognition.data.repository.ImageRepository(
                    getApplication<Application>().contentResolver,
                    photoDao
                )
                imageRepository.scanDeviceImages()
                
                // Also trigger processing logic
                val processingManager = com.example.facialrecognition.domain.FaceProcessingManager(
                    getApplication(),
                    photoDao,
                    faceDao,
                    personDao
                )
                processingManager.processUnprocessedPhotos()
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isScanning.value = false
            }
        }
    }
}
