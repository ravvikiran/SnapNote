package com.example.snapnote.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(onNavigateBack: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Note Detail") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { /* Edit action */ }) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit")
                    }
                    IconButton(onClick = { /* Delete action */ }) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Placeholder Image preview
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
            ) {
                // AsyncImage normally
            }

            // Tags
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                AssistChip(onClick = {}, label = { Text("#java") })
                AssistChip(onClick = {}, label = { Text("#programming") })
            }

            // Extracted Text section
            Text("Extracted Text", style = MaterialTheme.typography.titleMedium)
            
            Card(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Java Stream map vs flatMap explanation...",
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}
