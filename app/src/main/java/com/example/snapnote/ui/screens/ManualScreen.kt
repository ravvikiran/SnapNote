package com.example.snapnote.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManualScreen(onNavigateBack: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("User Manual") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
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
                .verticalScroll(rememberScrollState())
        ) {
            ManualSection(
                title = "1. Scanning Screenshots",
                content = "To start, click the 'Scan Existing Screenshots' button on the home screen. The app will ask for permission to access your photos. Once granted, it will analyze your screenshots and extract text from them."
            )
            
            ManualSection(
                title = "2. Searching",
                content = "Use the search bar at the top of the home screen to find specific screenshots. You can search for text found within the image, tags, or categories."
            )
            
            ManualSection(
                title = "3. Navigation",
                content = "Click on any screenshot card to view its details, including the full extracted text and assigned tags. Use the sidebar (drawer) to switch between the Home screen and this Manual."
            )

            ManualSection(
                title = "4. Dark Mode",
                content = "SnapNote supports system-wide dark mode. It will automatically adjust its theme based on your device settings."
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Text(
                "Enjoy using SnapNote!",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun ManualSection(title: String, content: String) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = content,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}
