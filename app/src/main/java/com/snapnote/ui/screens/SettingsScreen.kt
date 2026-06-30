package com.snapnote.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Backup
import androidx.compose.material.icons.outlined.DocumentScanner
import androidx.compose.material.icons.outlined.SyncAlt
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.snapnote.R
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

    LaunchedEffect(Unit) {
        settingsDataStore.autoScanEnabled.collectLatest { enabled ->
            autoScanEnabled = enabled
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        stringResource(R.string.settings),
                        fontWeight = FontWeight.SemiBold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.back))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(vertical = 8.dp)
        ) {
            // Auto-scan toggle
            ListItem(
                headlineContent = {
                    Text(
                        stringResource(R.string.auto_scan_title),
                        style = MaterialTheme.typography.bodyLarge
                    )
                },
                supportingContent = {
                    Text(
                        stringResource(R.string.auto_scan_description),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                leadingContent = {
                    Icon(
                        Icons.Outlined.DocumentScanner,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                },
                trailingContent = {
                    Switch(
                        checked = autoScanEnabled,
                        onCheckedChange = { checked ->
                            autoScanEnabled = checked
                            scope.launch { settingsDataStore.setAutoScanEnabled(checked) }
                        }
                    )
                }
            )
            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))

            // Re-run OCR
            ListItem(
                headlineContent = {
                    Text(
                        stringResource(R.string.rerun_ocr_title),
                        style = MaterialTheme.typography.bodyLarge
                    )
                },
                supportingContent = {
                    Text(
                        stringResource(R.string.rerun_ocr_description),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                leadingContent = {
                    Icon(
                        Icons.Outlined.SyncAlt,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                },
                modifier = Modifier.clickable { /* TODO: implement OCR re-run */ }
            )
            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))

            // Backup
            ListItem(
                headlineContent = {
                    Text(
                        stringResource(R.string.backup_title),
                        style = MaterialTheme.typography.bodyLarge
                    )
                },
                supportingContent = {
                    Text(
                        stringResource(R.string.backup_description),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                leadingContent = {
                    Icon(
                        Icons.Outlined.Backup,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                },
                modifier = Modifier.clickable { /* TODO: implement backup */ }
            )
            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))

            Spacer(Modifier.weight(1f))

            // App info
            Text(
                text = "SnapNote v1.0",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.outline,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp)
            )
        }
    }
}
