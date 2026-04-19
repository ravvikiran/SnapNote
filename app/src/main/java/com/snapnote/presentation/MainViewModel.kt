package com.snapnote.presentation

import android.app.Application
import android.net.Uri
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.snapnote.data.local.AppDatabase
import com.snapnote.data.local.ScreenshotNoteEntity
import com.snapnote.data.repository.ScreenshotNoteRepositoryImpl
import com.snapnote.domain.models.ScreenshotNote
import com.snapnote.domain.usecases.ExtractTextUseCase
import com.snapnote.domain.usecases.SuggestTagsUseCase
import com.snapnote.utils.ScreenshotScanner
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Semaphore

private const val TAG = "MainViewModel"

sealed class UiState {
    data object Loading : UiState()
    data class Success(val notes: List<ScreenshotNoteEntity>) : UiState()
    data class Error(val message: String) : UiState()
}

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getDatabase(application)
    private val repository = ScreenshotNoteRepositoryImpl(database.screenshotNoteDao())
    private val extractTextUseCase = ExtractTextUseCase(application)
    private val suggestTagsUseCase = SuggestTagsUseCase()
    private val screenshotScanner = ScreenshotScanner(application)
    
    // Limit concurrent screenshot processing to prevent OOM
    private val processingLimiter = Semaphore(4)

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategory = MutableStateFlow<String?>(null)
    val selectedCategory: StateFlow<String?> = _selectedCategory.asStateFlow()

    @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
    val uiState: StateFlow<UiState> = combine(_searchQuery, _selectedCategory) { query, category ->
            val noteFlow = if (query.isEmpty()) {
                if (category != null) {
                    repository.searchNotesByCategory(category)
                } else {
                    repository.getAllNotes()
                }
            } else {
                repository.searchNotes(query)
            }
            noteFlow
        }
        .debounce(300)
        .flatMapLatest { it }
        .map { notes -> 
            UiState.Success(notes.map { it.toEntity() }) as UiState 
        }
        .onStart { emit(UiState.Loading) }
        .catch { e -> emit(UiState.Error(e.message ?: "Unknown error")) }
        .flowOn(Dispatchers.IO)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = UiState.Loading
        )

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    fun selectCategory(category: String?) {
        _selectedCategory.value = category
    }

    fun scanExistingScreenshots() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val uris = screenshotScanner.getRecentScreenshots()
                uris.forEach { uri ->
                    processingLimiter.acquire()
                    try {
                        processScreenshot(uri)
                    } finally {
                        processingLimiter.release()
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error scanning screenshots", e)
            }
        }
    }

    private suspend fun processScreenshot(uri: Uri) {
        val path = uri.toString()
        if (repository.getNoteByPath(path) == null) {
            val text = extractTextUseCase.execute(uri)
            if (text.isNotBlank()) {
                val tags = suggestTagsUseCase.execute(text)
                
                val category = tags.firstOrNull()?.removePrefix("#")?.replaceFirstChar { it.uppercase() } ?: "Uncategorized"
                
                val note = ScreenshotNote(
                    imagePath = path,
                    extractedText = text,
                    tags = tags,
                    category = category,
                    dateAdded = System.currentTimeMillis()
                )
                repository.insertNote(note)
            }
        }
    }

    fun updateNote(note: ScreenshotNoteEntity) {
        val domainNote = ScreenshotNote(
            id = note.id,
            imagePath = note.imagePath,
            extractedText = note.extractedText,
            tags = note.tags.split(",").filter { it.isNotBlank() },
            category = note.category,
            dateAdded = note.dateAdded
        )
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertNote(domainNote)
        }
    }

    fun deleteNote(note: ScreenshotNoteEntity) {
        val domainNote = ScreenshotNote(
            id = note.id,
            imagePath = note.imagePath,
            extractedText = note.extractedText,
            tags = note.tags.split(",").filter { it.isNotBlank() },
            category = note.category,
            dateAdded = note.dateAdded
        )
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteNote(domainNote)
        }
    }

    override fun onCleared() {
        super.onCleared()
        // Note: extractTextUseCase no longer has a close() method
        // Recognizer is created fresh for each operation and properly closed in finally block
    }

    private fun ScreenshotNote.toEntity(): ScreenshotNoteEntity {
        return ScreenshotNoteEntity(
            id = id,
            imagePath = imagePath,
            extractedText = extractedText,
            tags = tags.joinToString(","),
            category = category,
            dateAdded = dateAdded
        )
    }
}
