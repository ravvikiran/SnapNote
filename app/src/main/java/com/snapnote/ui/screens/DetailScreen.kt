package com.snapnote.ui.screens

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.horizontalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Check
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.snapnote.R
import com.snapnote.data.local.ScreenshotNoteEntity
import com.snapnote.presentation.MainViewModel
import com.snapnote.presentation.UiState
import com.snapnote.util.Constants

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
    var editedText by remember(note?.extractedText) { mutableStateOf(note?.extractedText?.takeIf { it.isNotEmpty() } ?: "") }
    var editedTags by remember(note?.tags) { mutableStateOf(note?.tags?.takeIf { it.isNotEmpty() } ?: "") }
    var editedCategory by remember(note?.category) { mutableStateOf(note?.category?.takeIf { it.isNotEmpty() } ?: "") }
    var showValidationError by remember { mutableStateOf(false) }
    var showDeleteConfirmation by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isEditing) stringResource(R.string.edit_note) else stringResource(R.string.note_detail)) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.back))
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
                                Icon(Icons.Filled.Check, contentDescription = stringResource(R.string.save))
                            }
                        } else {
                            IconButton(onClick = { isEditing = true }) {
                                Icon(Icons.Filled.Edit, contentDescription = stringResource(R.string.edit))
                            }
                            IconButton(onClick = { showDeleteConfirmation = true }) {
                                Icon(Icons.Filled.Delete, contentDescription = stringResource(R.string.delete))
                            }
                        }
                    }
                }
            )
        }
    ) { padding ->
        if (note == null) {
            Box(modifier = Modifier.fillMaxSize().padding(padding)) {
                Text(stringResource(R.string.note_not_found))
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
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(400.dp),
                    contentAlignment = Alignment.Center
                ) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(note.imagePath)
                            .crossfade(true)
                            .build(),
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight(),
                        contentScale = ContentScale.Fit,
                        onError = {
                            Log.e("DetailScreen", "Image failed to load: ${note.imagePath}")
                        }
                    )
                }

                Text(stringResource(R.string.extracted_text), style = MaterialTheme.typography.titleMedium)
                
                if (isEditing) {
                    OutlinedTextField(
                        value = editedText,
                        onValueChange = { 
                            if (it.length <= Constants.MAX_TEXT_LENGTH) {
                                editedText = it
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp),
                        label = { Text(stringResource(R.string.edit_text_label)) },
                        supportingText = { Text("${editedText.length}/${Constants.MAX_TEXT_LENGTH}") },
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

                Text(stringResource(R.string.tags), style = MaterialTheme.typography.titleMedium)
                if (isEditing) {
                    OutlinedTextField(
                        value = editedTags,
                        onValueChange = { 
                            if (it.length <= Constants.MAX_TAGS_LENGTH) {
                                editedTags = it
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text(stringResource(R.string.comma_separated_tags)) },
                        supportingText = { Text("${editedTags.length}/${Constants.MAX_TAGS_LENGTH}") }
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
                            Text(stringResource(R.string.no_tags), style = MaterialTheme.typography.bodySmall)
                        } else {
                            tags.forEach { tag ->
                                val displayTag = if (tag.startsWith("#")) tag else "#$tag"
                                AssistChip(onClick = {}, label = { Text(displayTag) })
                            }
                        }
                    }
                }

                Text(stringResource(R.string.category), style = MaterialTheme.typography.titleMedium)
                if (isEditing) {
                    OutlinedTextField(
                        value = editedCategory,
                        onValueChange = { 
                            if (it.length <= Constants.MAX_CATEGORY_LENGTH) {
                                editedCategory = it
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text(stringResource(R.string.category_label)) },
                        supportingText = { Text("${editedCategory.length}/${Constants.MAX_CATEGORY_LENGTH}") },
                        isError = showValidationError && editedCategory.isEmpty()
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
                            stringResource(R.string.validation_error),
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
            title = { Text(stringResource(R.string.delete_confirmation)) },
            text = { Text(stringResource(R.string.delete_message)) },
            confirmButton = {
                Button(
                    onClick = {
                        showDeleteConfirmation = false
                        val currentNote = (uiState as? UiState.Success)?.notes?.find { it.id == noteId }
                        currentNote?.let { viewModel.deleteNote(it) }
                        onNavigateBack()
                    }
                ) {
                    Text(stringResource(R.string.delete))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirmation = false }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
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

