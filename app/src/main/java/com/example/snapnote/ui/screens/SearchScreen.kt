package com.example.snapnote.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(onNavigateBack: () -> Unit) {
    var searchQuery by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Advanced Search") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
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
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Search by keywords or tags...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") }
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Text("Filter by Tag", style = MaterialTheme.typography.titleSmall)
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(selected = false, onClick = {}, label = { Text("#finance") })
                FilterChip(selected = true, onClick = {}, label = { Text("#recipes") })
                FilterChip(selected = false, onClick = {}, label = { Text("#receipts") })
            }

            Spacer(modifier = Modifier.height(24.dp))
            Text("Search Results", style = MaterialTheme.typography.titleMedium)
            // Example list of results would go here
        }
    }
}
