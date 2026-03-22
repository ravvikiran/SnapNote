package com.example.snapnote.presentation

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.snapnote.data.local.AppDatabase
import com.example.snapnote.data.local.ScreenshotNoteEntity
import com.example.snapnote.domain.usecases.ExtractTextUseCase
import com.example.snapnote.utils.ScreenshotProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class UiState {
    object Loading : UiState()
    data class Success(val notes: List<ScreenshotNoteEntity>) : UiState()
    data class Error(val message: String) : UiState()
}

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val db = AppDatabase.getDatabase(application)
    private val dao = db.screenshotNoteDao()
    private val extractTextUseCase = ExtractTextUseCase(application)
    private val screenshotProvider = ScreenshotProvider(application)

    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    init {
        loadNotes()
    }

    fun loadNotes() {
        viewModelScope.launch {
            dao.getAllNotes().collect { notes ->
                _uiState.value = UiState.Success(notes)
            }
        }
    }

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
        viewModelScope.launch {
            if (query.isEmpty()) {
                loadNotes()
            } else {
                dao.searchNotes(query).collect { notes ->
                    _uiState.value = UiState.Success(notes)
                }
            }
        }
    }

    fun scanExistingScreenshots() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            val localScreenshots = screenshotProvider.fetchScreenshots()
            
            localScreenshots.forEach { local ->
                // Check if already in DB
                // For simplicity in this demo, we'll just process if not present
                // A better way would be checking imagePath
                
                val uri = Uri.parse(local.uri)
                val text = extractTextUseCase(uri)
                if (text.isNotBlank()) {
                    val tags = suggestTags(text)
                    val category = suggestCategory(text)
                    val entity = ScreenshotNoteEntity(
                        imagePath = local.uri,
                        extractedText = text,
                        tags = tags.joinToString(","),
                        category = category,
                        dateCreated = local.dateAdded * 1000 // MediaStore uses seconds
                    )
                    dao.insert(entity)
                }
            }
            loadNotes()
        }
    }

    private fun suggestTags(text: String): List<String> {
        val tags = mutableListOf<String>()
        val lowerText = text.lowercase()
        if (lowerText.contains("java") || lowerText.contains("kotlin") || lowerText.contains("android")) tags.add("programming")
        if (lowerText.contains("recipe") || lowerText.contains("food") || lowerText.contains("cook")) tags.add("food")
        if (lowerText.contains("upi") || lowerText.contains("bank") || lowerText.contains("payment")) tags.add("finance")
        if (lowerText.contains("otp") || lowerText.contains("verification")) tags.add("security")
        return tags
    }

    private fun suggestCategory(text: String): String {
        val lowerText = text.lowercase()
        return when {
            lowerText.contains("java") || lowerText.contains("kotlin") -> "Programming"
            lowerText.contains("upi") || lowerText.contains("bank") -> "Finance"
            lowerText.contains("recipe") -> "Food"
            else -> "General"
        }
    }
}
