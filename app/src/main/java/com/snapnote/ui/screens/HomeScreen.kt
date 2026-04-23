package com.snapnote.ui.screens

import android.Manifest
import android.os.Build
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.snapnote.R
import com.snapnote.data.local.ScreenshotNoteEntity
import com.snapnote.presentation.MainViewModel
import com.snapnote.presentation.UiState
import kotlinx.coroutines.launch
import androidx.compose.runtime.rememberCoroutineScope
import android.app.Activity
import androidx.core.app.ActivityCompat

// Helper to retrieve Activity from Context
fun Context.findActivity(): Activity? {
    var context = this
    while (context is android.content.ContextWrapper) {
        if (context is Activity) return context
        context = context.baseContext
    }
    return null
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToDetail: (Int) -> Unit,
    onNavigateToManual: () -> Unit,
    onNavigateToSettings: () -> Unit,
    viewModel: MainViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    
    val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        Manifest.permission.READ_MEDIA_IMAGES
    } else {
        Manifest.permission.READ_EXTERNAL_STORAGE
    }

    val snackbarHostState = remember { SnackbarHostState() }
    var showPermissionRationale by remember { mutableStateOf(false) }

    // Single permission launcher used throughout
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            viewModel.scanExistingScreenshots()
        } else {
            scope.launch {
                snackbarHostState.showSnackbar(context.getString(R.string.permission_denied_message))
            }
        }
    }

    // Handle permission request with rationale
    fun requestPermission() {
        val activity = context.findActivity()
        if (activity != null && !ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {
            // Need to show rationale dialog
            showPermissionRationale = true
        } else {
            // Direct request (first time or already denied with "Don't ask again")
            permissionLauncher.launch(permission)
        }
    }

    // Permission rationale dialog
    if (showPermissionRationale) {
        AlertDialog(
            onDismissRequest = { showPermissionRationale = false },
            title = { Text(stringResource(R.string.permission_rationale_title)) },
            text = { Text(stringResource(R.string.permission_rationale_message)) },
            confirmButton = {
                Button(onClick = {
                    showPermissionRationale = false
                    permissionLauncher.launch(permission)
                }) {
                    Text(stringResource(R.string.allow))
                }
            },
            dismissButton = {
                TextButton(onClick = { showPermissionRationale = false }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Spacer(Modifier.height(12.dp))
                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.Home, contentDescription = null) },
                    label = { Text(stringResource(R.string.menu_home)) },
                    selected = true,
                    onClick = { scope.launch { drawerState.close() } },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )
                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.Info, contentDescription = null) },
                    label = { Text(stringResource(R.string.menu_manual)) },
                    selected = false,
                    onClick = { 
                        scope.launch { drawerState.close() }
                        onNavigateToManual() 
                    },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )
                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.Settings, contentDescription = null) },
                    label = { Text(stringResource(R.string.menu_settings)) },
                    selected = false,
                    onClick = { 
                        scope.launch { drawerState.close() }
                        onNavigateToSettings() 
                    },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )
            }
        }
    ) {
            Scaffold(
                snackbarHost = { SnackbarHost(snackbarHostState) },
                topBar = {
                    CenterAlignedTopAppBar(
                        title = { Text(stringResource(R.string.home_title)) },
                        navigationIcon = {
                            IconButton(onClick = { scope.launch { drawerState.open() } }) {
                                Icon(Icons.Default.Menu, contentDescription = stringResource(R.string.menu_icon_description))
                            }
                        }
                    )
                }
            ) { padding ->
                SearchContent(
                    viewModel = viewModel,
                    searchQuery = searchQuery,
                    selectedCategory = selectedCategory,
                    onNavigateToDetail = onNavigateToDetail,
                    uiState = uiState,
                    onRequestPermission = { requestPermission() }
                )
        }
    }
}

 @Composable
 fun ScreenshotCard(note: ScreenshotNoteEntity, onClick: () -> Unit) {
     ElevatedCard(
         modifier = Modifier
             .fillMaxWidth()
             .height(220.dp)
             .clickable(onClick = onClick),
         shape = MaterialTheme.shapes.medium
     ) {
         Column {
             Box(
                 modifier = Modifier
                     .fillMaxWidth()
                     .weight(1f),
                 contentAlignment = Alignment.Center
             ) {
                 AsyncImage(
                     model = ImageRequest.Builder(LocalContext.current)
                         .data(note.imagePath)
                         .crossfade(true)
                         .build(),
                     contentDescription = null,
                     modifier = Modifier
                         .fillMaxWidth()
                         .fillMaxHeight(),
                     contentScale = ContentScale.Crop,
                     onError = {
                         Log.e("ScreenshotCard", "Failed to load image: ${note.imagePath}")
                     }
                 )
             }
             Column(modifier = Modifier.padding(8.dp)) {
                 Text(
                     text = note.category,
                     style = MaterialTheme.typography.labelSmall,
                     color = MaterialTheme.colorScheme.primary
                 )
                 Text(
                     text = note.extractedText.take(40),
                     style = MaterialTheme.typography.bodySmall,
                     maxLines = 2
                 )
             }
         }
     }
 }

@Composable
private fun SearchContent(
    viewModel: MainViewModel,
    searchQuery: String,
    selectedCategory: String?,
    onNavigateToDetail: (Int) -> Unit,
    uiState: UiState,
    onRequestPermission: () -> Unit
) {
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { viewModel.onSearchQueryChanged(it) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            placeholder = { Text(stringResource(R.string.search_placeholder)) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = stringResource(R.string.search_icon_description)) },
            shape = MaterialTheme.shapes.medium,
            singleLine = true
        )

        Button(
            onClick = { requestPermission() },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            shape = MaterialTheme.shapes.medium
        ) {
            Text(stringResource(R.string.scan_button))
        }

        Spacer(modifier = Modifier.height(16.dp))

        when (val state = uiState) {
            is UiState.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            is UiState.Success -> {
                if (state.notes.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(stringResource(R.string.no_screenshots))
                    }
                } else {
                    // Categories
                    val categories = listOf(stringResource(R.string.all_categories_filter)) + state.notes.map { it.category }.distinct()
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(categories) { category ->
FilterChip(
                                    selected = (if (category == stringResource(R.string.all_categories_filter)) selectedCategory == null else category == selectedCategory),
                                    onClick = {
                                        if (category == stringResource(R.string.all_categories_filter)) {
                                            viewModel.selectCategory(null)
                                        } else {
                                            viewModel.selectCategory(category)
                                        }
                                    },
                                label = { Text(category) }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        stringResource(R.string.recent_screenshots),
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )

                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        contentPadding = PaddingValues(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(state.notes, key = { it.id }) { note ->
                            ScreenshotCard(note = note, onClick = { onNavigateToDetail(note.id) })
                        }
                    }
                }
            }
            is UiState.Error -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(stringResource(R.string.error_loading_message, state.message ?: "Unknown error"))
                }
            }
        }
    }
}
