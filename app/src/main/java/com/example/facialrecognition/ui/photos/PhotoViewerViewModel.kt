package com.example.facialrecognition.ui.photos

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.facialrecognition.data.local.AppDatabase
import com.example.facialrecognition.data.local.entity.Photo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

data class PhotoViewerUiState(
    val photos: List<Photo> = emptyList(),
    val initialIndex: Int = 0,
    val isLoading: Boolean = true
)

class PhotoViewerViewModel(
    application: Application,
    private val source: String,
    private val sourceId: Long,
    private val startPhotoId: Long
) : AndroidViewModel(application) {

    private val photoDao = AppDatabase.getDatabase(application).photoDao()

    private val _uiState = MutableStateFlow(PhotoViewerUiState())
    val uiState: StateFlow<PhotoViewerUiState> = _uiState.asStateFlow()

    init {
        loadPhotos()
    }

    private fun loadPhotos() {
        viewModelScope.launch {
            val photos = when (source) {
                "person" -> photoDao.getPhotosForPerson(sourceId)
                else -> photoDao.getAllPhotos().first() // Get a snapshot
            }

            val initialIndex = photos.indexOfFirst { it.id == startPhotoId }.coerceAtLeast(0)

            _uiState.value = PhotoViewerUiState(
                photos = photos,
                initialIndex = initialIndex,
                isLoading = false
            )
        }
    }

    class Factory(
        private val application: Application,
        private val source: String,
        private val sourceId: Long,
        private val startPhotoId: Long
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return PhotoViewerViewModel(application, source, sourceId, startPhotoId) as T
        }
    }
}
