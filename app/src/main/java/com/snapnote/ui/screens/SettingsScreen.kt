package com.snapnote.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.snapnote.R
import com.snapnote.data.settings.SettingsDataStore
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import com.snapnote.data.settings.SettingsDataStore
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(onNavigateBack: () -> Unit) {
    val context = LocalContext.current
    val settingsDataStore = remember { SettingsDataStore(context) }
    var autoScanEnabled by remember { mutableStateOf(true) }
    val scope = rememberCoroutineScope()

    // Load setting from DataStore
    LaunchedEffect(Unit) {
        settingsDataStore.autoScanEnabled.collectLatest { enabled ->
            autoScanEnabled = enabled
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.settings_title)) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.back_button))
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
                headlineContent = { Text(stringResource(R.string.auto_scan_title)) },
                supportingContent = { Text(stringResource(R.string.auto_scan_description)) },
                trailingContent = {
                    Switch(
                        checked = autoScanEnabled,
                        onCheckedChange = { checked ->
                            autoScanEnabled = checked
                            scope.launch {
                                settingsDataStore.setAutoScanEnabled(checked)
                            }
                        }
                    )
                }
            )
            HorizontalDivider()
            
            ListItem(
                headlineContent = { Text(stringResource(R.string.rerun_ocr_title)) },
                supportingContent = { Text(stringResource(R.string.rerun_ocr_description)) },
                modifier = Modifier.padding(vertical = 8.dp)
            )
            HorizontalDivider()
            
            ListItem(
                headlineContent = { Text(stringResource(R.string.backup_title)) },
                supportingContent = { Text(stringResource(R.string.backup_description)) },
                modifier = Modifier.padding(vertical = 8.dp)
            )
            HorizontalDivider()
        }
    }
}
