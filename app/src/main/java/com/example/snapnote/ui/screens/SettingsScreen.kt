package com.example.snapnote.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(onNavigateBack: () -> Unit) {
    var autoScanEnabled by remember { mutableStateOf(true) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
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
        ) {
            ListItem(
                headlineContent = { Text("Auto-scan Screenshots") },
                supportingContent = { Text("Automatically import and process new screenshots") },
                trailingContent = {
                    Switch(
                        checked = autoScanEnabled,
                        onCheckedChange = { autoScanEnabled = it }
                    )
                }
            )
            Divider()
            
            ListItem(
                headlineContent = { Text("Re-run OCR on all notes") },
                supportingContent = { Text("Process images again to extract text") },
                modifier = Modifier.padding(vertical = 8.dp)
            )
            Divider()
            
            ListItem(
                headlineContent = { Text("Backup & Restore") },
                supportingContent = { Text("Export database or restore from a backup") },
                modifier = Modifier.padding(vertical = 8.dp)
            )
            Divider()
        }
    }
}
