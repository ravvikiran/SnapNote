package com.snapnote.ui.screens

import android.util.Log
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.snapnote.R
import com.snapnote.presentation.MainViewModel
import com.snapnote.presentation.UiState
import com.snapnote.util.Constants

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    noteId: Int,
    onNavigateBack: () -> Unit,
    viewModel: MainViewModel = viewModel(
        viewModelStoreOwner = LocalContext.current as androidx.activity.ComponentActivity
    )
) {
    val uiState by viewModel.uiState.collectAsState()
    val note = remember(uiState, noteId) {
        (uiState as? UiState.Success)?.notes?.find { it.id == noteId }
    }
    var isEditing by remember(noteId) { mutableStateOf(false) }
    var editedText by remember(note?.extractedText) {
        mutableStateOf(note?.extractedText?.takeIf { it.isNotEmpty() } ?: "")
    }
    var editedTags by remember(note?.tags) {
        mutableStateOf(note?.tags?.takeIf { it.isNotEmpty() } ?: "")
    }
    var editedCategory by remember(note?.category) {
        mutableStateOf(note?.category?.takeIf { it.isNotEmpty() } ?: "")
    }
    var showValidationError by remember { mutableStateOf(false) }
    var showDeleteConfirmation by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        if (isEditing) stringResource(R.string.edit_note)
                        else stringResource(R.string.note_detail),
                        fontWeight = FontWeight.SemiBold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.back))
                    }
                },
                actions = {
                    if (note != null) {
                        if (isEditing) {
                            FilledIconButton(
                                onClick = {
                                    if (editedText.isEmpty() || editedCategory.isEmpty()) {
                                        showValidationError = true
                                    } else {
                                        showValidationError = false
                                        viewModel.updateNote(
                                            note.copy(
                                                extractedText = editedText.trim(),
                                                tags = editedTags.trim(),
                                                category = editedCategory.trim()
                                            )
                                        )
                                        isEditing = false
                                    }
                                },
                                modifier = Modifier.padding(end = 4.dp)
                            ) {
                                Icon(Icons.Filled.Check, contentDescription = stringResource(R.string.save))
                            }
                        } else {
                            IconButton(onClick = { isEditing = true }) {
                                Icon(Icons.Filled.Edit, contentDescription = stringResource(R.string.edit))
                            }
                            IconButton(onClick = { showDeleteConfirmation = true }) {
                                Icon(
                                    Icons.Filled.Delete,
                                    contentDescription = stringResource(R.string.delete),
                                    tint = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { padding ->
        when {
            uiState is UiState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            note == null -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        stringResource(R.string.note_not_found),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            else -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .verticalScroll(rememberScrollState())
                ) {
                    // Image
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(note.imagePath)
                            .crossfade(true)
                            .build(),
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 200.dp, max = 300.dp)
                            .padding(16.dp)
                            .clip(RoundedCornerShape(16.dp)),
                        contentScale = ContentScale.Fit,
                        onError = {
                            Log.e("DetailScreen", "Image failed to load: ${note.imagePath}")
                        }
                    )

                    // Content
                    Column(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(20.dp)
                    ) {
                        // Extracted Text
                        SectionHeader(stringResource(R.string.extracted_text))
                        if (isEditing) {
                            OutlinedTextField(
                                value = editedText,
                                onValueChange = { if (it.length <= Constants.MAX_TEXT_LENGTH) editedText = it },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(150.dp),
                                label = { Text(stringResource(R.string.edit_text_label)) },
                                supportingText = { Text("${editedText.length}/${Constants.MAX_TEXT_LENGTH}") },
                                isError = showValidationError && editedText.isEmpty(),
                                shape = RoundedCornerShape(12.dp)
                            )
                        } else {
                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                            ) {
                                Text(
                                    text = note.extractedText.ifEmpty { "No text extracted" },
                                    modifier = Modifier.padding(16.dp),
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }

                        // Tags
                        SectionHeader(stringResource(R.string.tags))
                        if (isEditing) {
                            OutlinedTextField(
                                value = editedTags,
                                onValueChange = { if (it.length <= Constants.MAX_TAGS_LENGTH) editedTags = it },
                                modifier = Modifier.fillMaxWidth(),
                                label = { Text(stringResource(R.string.comma_separated_tags)) },
                                supportingText = { Text("${editedTags.length}/${Constants.MAX_TAGS_LENGTH}") },
                                shape = RoundedCornerShape(12.dp)
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
                                    Text(
                                        stringResource(R.string.no_tags),
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.outline
                                    )
                                } else {
                                    tags.forEach { tag ->
                                        val displayTag = if (tag.startsWith("#")) tag else "#$tag"
                                        SuggestionChip(onClick = {}, label = { Text(displayTag) })
                                    }
                                }
                            }
                        }

                        // Category
                        SectionHeader(stringResource(R.string.category))
                        if (isEditing) {
                            OutlinedTextField(
                                value = editedCategory,
                                onValueChange = { if (it.length <= Constants.MAX_CATEGORY_LENGTH) editedCategory = it },
                                modifier = Modifier.fillMaxWidth(),
                                label = { Text(stringResource(R.string.category_label)) },
                                supportingText = { Text("${editedCategory.length}/${Constants.MAX_CATEGORY_LENGTH}") },
                                isError = showValidationError && editedCategory.isEmpty(),
                                shape = RoundedCornerShape(12.dp)
                            )
                        } else {
                            SuggestionChip(onClick = {}, label = { Text(note.category) })
                        }

                        // Validation Error
                        if (showValidationError) {
                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(8.dp),
                                color = MaterialTheme.colorScheme.errorContainer
                            ) {
                                Text(
                                    stringResource(R.string.validation_error),
                                    modifier = Modifier.padding(12.dp),
                                    color = MaterialTheme.colorScheme.onErrorContainer,
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }

                        Spacer(Modifier.height(16.dp))
                    }
                }
            }
        }
    }

    // Delete dialog
    if (showDeleteConfirmation) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmation = false },
            icon = { Icon(Icons.Filled.Delete, contentDescription = null, tint = MaterialTheme.colorScheme.error) },
            title = { Text(stringResource(R.string.delete_confirmation_title)) },
            text = { Text(stringResource(R.string.delete_confirmation_message)) },
            confirmButton = {
                Button(
                    onClick = {
                        showDeleteConfirmation = false
                        (uiState as? UiState.Success)?.notes?.find { it.id == noteId }
                            ?.let { viewModel.deleteNote(it) }
                        onNavigateBack()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) { Text(stringResource(R.string.delete)) }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirmation = false }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }
}

@Composable
private fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleSmall,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.onSurface
    )
}
