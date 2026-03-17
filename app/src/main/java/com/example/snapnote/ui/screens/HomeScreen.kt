package com.example.snapnote.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen() {
    var searchQuery by remember { mutableStateOf("") }
    val categories = listOf("All", "Programming", "Finance", "Recipes", "Social Media")
    var selectedCategory by remember { mutableStateOf("All") }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("SnapNote") })
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                placeholder = { Text("Search text, tags, etc.") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                singleLine = true
            )

            // Category Filters
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(categories) { category ->
                    FilterChip(
                        selected = category == selectedCategory,
                        onClick = { selectedCategory = category },
                        label = { Text(category) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Screenshot Grid
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(10) { index -> // Placeholder items
                    Card(modifier = Modifier.height(200.dp)) {
                        // Image Placeholder (normally anAsyncImage from Coil)
                        Box(modifier = Modifier.fillMaxSize()) {
                            Text(
                                "Screenshot Note $index",
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
