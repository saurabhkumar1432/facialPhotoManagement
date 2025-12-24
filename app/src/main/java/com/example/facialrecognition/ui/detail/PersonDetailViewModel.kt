package com.example.facialrecognition.ui.detail

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.facialrecognition.data.local.AppDatabase
import com.example.facialrecognition.data.local.entity.Person
import com.example.facialrecognition.data.local.entity.Photo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PersonDetailViewModel(
    application: Application,
    private val personId: Long
) : AndroidViewModel(application) {
    private val database = AppDatabase.getDatabase(application)
    private val personDao = database.personDao()
    private val photoDao = database.photoDao()

    private val _uiState = MutableStateFlow(PersonDetailUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            val person = personDao.getPersonById(personId)
            val photos = photoDao.getPhotosForPerson(personId)
            _uiState.update { 
                it.copy(
                    person = person,
                    photos = photos,
                    isLoading = false
                ) 
            }
        }
    }
    
    // Sharing logic will go here (expose selection state)
    fun toggleSelection(photoId: Long) {
        _uiState.update { state ->
            val newSelection = state.selectedPhotoIds.toMutableSet()
            if (newSelection.contains(photoId)) {
                newSelection.remove(photoId)
            } else {
                newSelection.add(photoId)
            }
            state.copy(selectedPhotoIds = newSelection)
        }
    }
    
    fun selectAll() {
        _uiState.update { state ->
            state.copy(selectedPhotoIds = state.photos.map { it.id }.toSet())
        }
    }
    
    fun deselectAll() {
        _uiState.update { it.copy(selectedPhotoIds = emptySet()) }
    }
    
    fun clearSelection() {
        _uiState.update { it.copy(selectedPhotoIds = emptySet()) }
    }

    fun deletePerson(onDeleted: () -> Unit) {
        val currentPerson = uiState.value.person ?: return
        viewModelScope.launch {
            personDao.delete(currentPerson)
            onDeleted()
        }
    }

    fun renamePerson(newName: String) {
        val currentPerson = uiState.value.person ?: return
        if (newName.isBlank()) return
        
        viewModelScope.launch {
            val updatedPerson = currentPerson.copy(name = newName)
            personDao.update(updatedPerson)
            // Update local state immediately
            _uiState.update { it.copy(person = updatedPerson) }
        }
    }
    
    class Factory(private val app: Application, private val personId: Long) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return PersonDetailViewModel(app, personId) as T
        }
    }
}

data class PersonDetailUiState(
    val person: Person? = null,
    val photos: List<Photo> = emptyList(),
    val isLoading: Boolean = true,
    val selectedPhotoIds: Set<Long> = emptySet()
)
