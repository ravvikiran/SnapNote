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
import com.snapnote.util.Constants
import com.snapnote.util.ScreenshotNoteMapper
import com.snapnote.utils.ScreenshotScanner
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Semaphore
import java.util.concurrent.atomic.AtomicBoolean

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
    private val processingLimiter = Semaphore(Constants.MAX_CONCURRENT_PROCESSING)

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategory = MutableStateFlow<String?>(null)
    val selectedCategory: StateFlow<String?> = _selectedCategory.asStateFlow()

    private val _scanProgress = MutableStateFlow(0f)
    val scanProgress: StateFlow<Float> = _scanProgress.asStateFlow()

    private val isScanning = AtomicBoolean(false)

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
        .debounce(Constants.SEARCH_DEBOUNCE_MS)
        .flatMapLatest { it }
        .map { notes ->
            UiState.Success(notes.map { ScreenshotNoteMapper.domainToEntity(it) }) as UiState
        }
        .onStart { emit(UiState.Loading) }
        .catch { e -> emit(UiState.Error(e.message ?: "Unknown error")) }
        .flowOn(Dispatchers.IO)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(Constants.VIEWMODEL_TIMEOUT_MS),
            initialValue = UiState.Loading
        )

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    fun selectCategory(category: String?) {
        _selectedCategory.value = category
    }

    fun scanExistingScreenshots() {
        if (!isScanning.compareAndSet(false, true)) return
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _scanProgress.value = 0.01f
                val uris = screenshotScanner.getRecentScreenshots(Constants.MAX_SCREENSHOTS_TO_SCAN)
                if (uris.isEmpty()) {
                    _scanProgress.value = 0f
                    return@launch
                }

                uris.forEachIndexed { index, uri ->
                    try {
                        processingLimiter.acquire()
                        try {
                            processScreenshot(uri)
                        } catch (e: Exception) {
                            Log.e(TAG, "Error processing screenshot: $uri", e)
                        } finally {
                            processingLimiter.release()
                        }
                    } catch (e: Exception) {
                        Log.e(TAG, "Failed to acquire processing permit for: $uri", e)
                    }
                    _scanProgress.value = (index + 1).toFloat() / uris.size
                }

                _scanProgress.value = 0f
            } catch (e: Exception) {
                Log.e(TAG, "Error scanning screenshots", e)
                _scanProgress.value = 0f
            } finally {
                isScanning.set(false)
            }
        }
    }

    private suspend fun processScreenshot(uri: Uri) {
        val path = uri.toString()
        val text = extractTextUseCase.execute(uri)
        if (text.isNotBlank()) {
            val tags = suggestTagsUseCase.execute(text)
            val category = tags.firstOrNull()
                ?.removePrefix("#")
                ?.replaceFirstChar { it.uppercase() }
                ?: "Uncategorized"

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

    fun updateNote(note: ScreenshotNoteEntity) {
        val domainNote = ScreenshotNoteMapper.entityToDomain(note)
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertNote(domainNote)
        }
    }

    fun deleteNote(note: ScreenshotNoteEntity) {
        val domainNote = ScreenshotNoteMapper.entityToDomain(note)
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteNote(domainNote)
        }
    }
}
