package com.snapnote.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.snapnote.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManualScreen(onNavigateBack: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.user_manual)) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back)
                        )
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
                title = stringResource(R.string.manual_section_1_title),
                content = stringResource(R.string.manual_section_1_content)
            )

            ManualSection(
                title = stringResource(R.string.manual_section_2_title),
                content = stringResource(R.string.manual_section_2_content)
            )

            ManualSection(
                title = stringResource(R.string.manual_section_3_title),
                content = stringResource(R.string.manual_section_3_content)
            )

            ManualSection(
                title = stringResource(R.string.manual_section_4_title),
                content = stringResource(R.string.manual_section_4_content)
            )

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                stringResource(R.string.enjoy_snapnote),
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
