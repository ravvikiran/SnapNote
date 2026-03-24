package com.snapnote.presentation

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.snapnote.data.local.AppDatabase
import com.snapnote.data.local.ScreenshotNoteEntity
import com.snapnote.domain.usecases.ExtractTextUseCase
import com.snapnote.domain.usecases.SuggestTagsUseCase
import com.snapnote.utils.ScreenshotProvider
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

sealed class UiState {
    data object Loading : UiState()
    data class Success(val notes: List<ScreenshotNoteEntity>) : UiState()
    data class Error(val message: String) : UiState()
}

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val database = androidx.room.Room.databaseBuilder(
        application,
        AppDatabase::class.java, "snapnote-db"
    ).build()

    private val repository = database.screenshotNoteDao()
    private val extractTextUseCase = ExtractTextUseCase(application)
    private val suggestTagsUseCase = SuggestTagsUseCase()
    private val screenshotProvider = ScreenshotProvider(application)

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
    val uiState: StateFlow<UiState> = _searchQuery
        .debounce(300)
        .flatMapLatest { query ->
            if (query.isEmpty()) {
                repository.getAllNotes()
            } else {
                repository.searchNotes(query)
            }
        }
        .map { notes -> UiState.Success(notes) as UiState }
        .onStart { emit(UiState.Loading) }
        .catch { e -> emit(UiState.Error(e.message ?: "Unknown error")) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = UiState.Loading
        )

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    fun scanExistingScreenshots() {
        viewModelScope.launch {
            val uris = screenshotProvider.getRecentScreenshots()
            uris.forEach { uri ->
                processScreenshot(uri)
            }
        }
    }

    private suspend fun processScreenshot(uri: Uri) {
        val path = uri.toString()
        if (repository.getNoteByPath(path) == null) {
            val text = extractTextUseCase.execute(uri)
            if (text.isNotBlank()) {
                val tags = suggestTagsUseCase.execute(text)
                val note = ScreenshotNoteEntity(
                    imagePath = path,
                    extractedText = text,
                    tags = tags.joinToString(","),
                    category = "Uncategorized"
                )
                repository.insertNote(note)
            }
        }
    }
}
