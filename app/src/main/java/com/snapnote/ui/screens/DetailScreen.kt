package com.snapnote.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
    val note = (uiState as? UiState.Success)?.notes?.find { it.id == noteId }
    var isEditing by remember { mutableStateOf(false) }
    var editedText by remember(note?.extractedText) { mutableStateOf(note?.extractedText ?: "") }
    var editedTags by remember(note?.tags) { mutableStateOf(note?.tags ?: "") }
    var editedCategory by remember(note?.category) { mutableStateOf(note?.category ?: "") }

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
                                viewModel.updateNote(note.copy(
                                    extractedText = editedText,
                                    tags = editedTags,
                                    category = editedCategory
                                ))
                                isEditing = false
                            }) {
                                Icon(Icons.Filled.Check, contentDescription = "Save")
                            }
                        } else {
                            IconButton(onClick = { isEditing = true }) {
                                Icon(Icons.Filled.Edit, contentDescription = "Edit")
                            }
                            IconButton(onClick = {
                                viewModel.deleteNote(note)
                                onNavigateBack()
                            }) {
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
                        onValueChange = { editedText = it },
                        modifier = Modifier.fillMaxWidth()
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
                        onValueChange = { editedTags = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Comma separated tags") }
                    )
                } else {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        note.tags.split(",").filter { it.isNotBlank() }.forEach { tag ->
                            AssistChip(onClick = {}, label = { Text(tag.trim().let { if (it.startsWith("#")) it else "#$it" }) })
                        }
                    }
                }

                Text("Category", style = MaterialTheme.typography.titleMedium)
                if (isEditing) {
                    OutlinedTextField(
                        value = editedCategory,
                        onValueChange = { editedCategory = it },
                        modifier = Modifier.fillMaxWidth()
                    )
                } else {
                    AssistChip(onClick = {}, label = { Text(note.category) })
                }
            }
        }
    }
}
