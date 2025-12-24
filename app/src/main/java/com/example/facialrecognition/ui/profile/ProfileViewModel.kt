package com.example.facialrecognition.ui.profile

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.facialrecognition.data.local.AppDatabase
import com.example.facialrecognition.data.local.AppPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers

data class ProfileUiState(
    val photoCount: Int = 0,
    val processedCount: Int = 0,
    val peopleCount: Int = 0,
    val faceCount: Int = 0
)

class ProfileViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getDatabase(application)
    private val photoDao = database.photoDao()
    private val personDao = database.personDao()
    private val faceDao = database.faceDao()
    // Prefs removed as they are no longer needed for UI state

    val uiState: StateFlow<ProfileUiState> = combine(
        photoDao.getPhotoCount(),
        photoDao.getProcessedPhotoCount(),
        personDao.getPeopleCount(),
        faceDao.getFaceCount()
    ) { photoCount, processedCount, peopleCount, faceCount ->
        ProfileUiState(
            photoCount = photoCount,
            processedCount = processedCount,
            peopleCount = peopleCount,
            faceCount = faceCount
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ProfileUiState()
    )

    fun clearFaceCache() {
        viewModelScope.launch(Dispatchers.IO) {
            faceDao.deleteAllFaces()
            // Reset processed status on photos? Maybe not, just faces.
            // If we delete faces, we should probably set isProcessed=false on photos to allow rescan
            photoDao.resetProcessedStatus() 
        }
    }

    fun deleteAllData() {
        viewModelScope.launch(Dispatchers.IO) {
            database.clearAllTables()
        }
    }
}
