package com.example.facialrecognition.ui.search

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
import kotlinx.coroutines.flow.update

data class SearchUiState(
    val query: String = "",
    val people: List<Person> = emptyList(),
    val filteredPeople: List<Person> = emptyList(),
    val isSearching: Boolean = false,
    val unlabelledFaceCount: Int = 0
)

class SearchViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getDatabase(application)
    private val personDao = database.personDao()
    private val faceDao = database.faceDao()

    private val _query = MutableStateFlow("")

    val uiState: StateFlow<SearchUiState> = combine(
        _query,
        personDao.getAllPeople(),
        faceDao.getUnlabelledFaceCount()
    ) { query, people, unlabelledCount ->
        val filtered = if (query.isBlank()) {
            people
        } else {
            people.filter { it.name.contains(query, ignoreCase = true) }
        }
        SearchUiState(
            query = query,
            people = people,
            filteredPeople = filtered,
            isSearching = query.isNotBlank(),
            unlabelledFaceCount = unlabelledCount
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = SearchUiState()
    )

    fun onQueryChange(newQuery: String) {
        _query.value = newQuery
    }

    fun clearSearch() {
        _query.value = ""
    }
}
