package com.snapnote.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.horizontalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Check
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.snapnote.data.local.ScreenshotNoteEntity
import com.snapnote.presentation.MainViewModel
import com.snapnote.presentation.UiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    noteId: Int,
    onNavigateBack: () -> Unit,
    viewModel: MainViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val note = remember(uiState, noteId) {
        (uiState as? UiState.Success)?.notes?.find { it.id == noteId }
    }
    var isEditing by remember(noteId) { mutableStateOf(false) }
    var editedText by remember(note?.extractedText) { mutableStateOf(note?.extractedText ?: "") }
    var editedTags by remember(note?.tags) { mutableStateOf(note?.tags ?: "") }
    var editedCategory by remember(note?.category) { mutableStateOf(note?.category ?: "") }
    var showValidationError by remember { mutableStateOf(false) }
    var showDeleteConfirmation by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isEditing) "Edit Note" else "Note Detail") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (note != null) {
                        if (isEditing) {
                            IconButton(onClick = {
                                if (editedText.isEmpty() || editedCategory.isEmpty()) {
                                    showValidationError = true
                                } else {
                                    showValidationError = false
                                    viewModel.updateNote(note.copy(
                                        extractedText = editedText.trim(),
                                        tags = editedTags.trim(),
                                        category = editedCategory.trim()
                                    ))
                                    isEditing = false
                                }
                            }) {
                                Icon(Icons.Filled.Check, contentDescription = "Save")
                            }
                        } else {
                            IconButton(onClick = { isEditing = true }) {
                                Icon(Icons.Filled.Edit, contentDescription = "Edit")
                            }
                            IconButton(onClick = { showDeleteConfirmation = true }) {
                                Icon(Icons.Filled.Delete, contentDescription = "Delete")
                            }
                        }
                    }
                }
            )
        }
    ) { padding ->
        if (note == null) {
            Box(modifier = Modifier.fillMaxSize().padding(padding)) {
                Text("Note not found")
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                AsyncImage(
                    model = note.imagePath,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(400.dp),
                    contentScale = ContentScale.Fit
                )

                Text("Extracted Text", style = MaterialTheme.typography.titleMedium)
                
                if (isEditing) {
                    OutlinedTextField(
                        value = editedText,
                        onValueChange = { 
                            if (it.length <= 5000) {
                                editedText = it
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp),
                        label = { Text("Edit text") },
                        supportingText = { Text("${editedText.length}/5000") },
                        isError = showValidationError && editedText.isEmpty()
                    )
                } else {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = MaterialTheme.shapes.medium,
                        tonalElevation = 2.dp
                    ) {
                        Text(
                            text = note.extractedText,
                            modifier = Modifier.padding(16.dp),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }

                Text("Tags", style = MaterialTheme.typography.titleMedium)
                if (isEditing) {
                    OutlinedTextField(
                        value = editedTags,
                        onValueChange = { 
                            if (it.length <= 500) {
                                editedTags = it
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Comma separated tags") },
                        supportingText = { Text("${editedTags.length}/500") }
                    )
                } else {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val tags = note.tags.split(",").filter { it.isNotBlank() }.map { it.trim() }
                        if (tags.isEmpty()) {
                            Text("No tags", style = MaterialTheme.typography.bodySmall)
                        } else {
                            tags.forEach { tag ->
                                val displayTag = if (tag.startsWith("#")) tag else "#$tag"
                                AssistChip(onClick = {}, label = { Text(displayTag) })
                            }
                        }
                    }
                }

                Text("Category", style = MaterialTheme.typography.titleMedium)
                if (isEditing) {
                    OutlinedTextField(
                        value = editedCategory,
                        onValueChange = { 
                            if (it.length <= 100) {
                                editedCategory = it
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Category") },
                        supportingText = { Text("${editedCategory.length}/100") },
                        isError = showValidationError && editedCategory.isEmpty()
                    )
                } else {
                    AssistChip(onClick = {}, label = { Text(note.category) })
                }

                if (showValidationError) {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = MaterialTheme.shapes.small,
                        color = MaterialTheme.colorScheme.errorContainer
                    ) {
                        Text(
                            "Text and Category cannot be empty",
                            modifier = Modifier.padding(12.dp),
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        }
    }

    if (showDeleteConfirmation) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmation = false },
            title = { Text("Delete Note") },
            text = { Text("Are you sure you want to delete this note? This action cannot be undone.") },
            confirmButton = {
                Button(
                    onClick = {
                        showDeleteConfirmation = false
                        viewModel.deleteNote(note!!)
                        onNavigateBack()
                    }
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirmation = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

