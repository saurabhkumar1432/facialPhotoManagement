package com.example.facialrecognition.ui.main

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.facialrecognition.data.local.AppDatabase
import com.example.facialrecognition.data.local.entity.Person
import com.example.facialrecognition.data.local.entity.Photo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

/**
 * UI State for the home screen dashboard
 */
data class HomeUiState(
    val photoCount: Int = 0,
    val processedPhotoCount: Int = 0,
    val peopleCount: Int = 0,
    val faceCount: Int = 0,
    val recentPhotos: List<Photo> = emptyList(),
    val people: List<Person> = emptyList(),
    val isScanning: Boolean = false,
    val hasStoragePermission: Boolean = false
) {
    val scanProgress: Float
        get() = if (photoCount > 0) processedPhotoCount.toFloat() / photoCount else 0f
    
    val scanProgressPercent: Int
        get() = (scanProgress * 100).toInt()
}

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getDatabase(application)
    private val personDao = database.personDao()
    private val photoDao = database.photoDao()
    private val faceDao = database.faceDao()

    private val _isScanning = MutableStateFlow(false)
    
    val uiState: StateFlow<HomeUiState> = combine(
        photoDao.getPhotoCount(),
        photoDao.getProcessedPhotoCount(),
        personDao.getPeopleCount(),
        faceDao.getFaceCount(),
        photoDao.getRecentPhotos(6),
        personDao.getAllPeople(),
        _isScanning
    ) { values ->
        @Suppress("UNCHECKED_CAST")
        HomeUiState(
            photoCount = values[0] as Int,
            processedPhotoCount = values[1] as Int,
            peopleCount = values[2] as Int,
            faceCount = values[3] as Int,
            recentPhotos = values[4] as List<Photo>,
            people = values[5] as List<Person>,
            isScanning = values[6] as Boolean
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = HomeUiState()
    )

    // For backwards compatibility
    val people: StateFlow<List<Person>> = personDao.getAllPeople()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun setScanningStatus(isScanning: Boolean) {
        _isScanning.value = isScanning
    }
}
