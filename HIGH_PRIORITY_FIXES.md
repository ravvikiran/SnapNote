# SnapNote - High Priority Fixes (Ready to Implement)

## Fix #1: Extract Hardcoded Strings (2-3 hours)

### Step 1: Update strings.xml

**File:** `app/src/main/res/values/strings.xml`

```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <!-- App Name -->
    <string name="app_name">SnapNote</string>
    
    <!-- Home Screen -->
    <string name="home_title">SnapNote</string>
    <string name="search_placeholder">Search text, tags, etc.</string>
    <string name="scan_button_text">Scan Existing Screenshots</string>
    <string name="recent_screenshots_title">Recent Screenshots</string>
    <string name="no_screenshots_message">No screenshots found. Try scanning!</string>
    <string name="error_loading_message">Error: %s</string>
    
    <!-- Drawer Menu -->
    <string name="menu_home">Home</string>
    <string name="menu_manual">User Manual</string>
    <string name="menu_settings">Settings</string>
    
    <!-- Detail Screen -->
    <string name="detail_screen_title">Note Detail</string>
    <string name="edit_screen_title">Edit Note</string>
    <string name="edit_button">Edit</string>
    <string name="delete_button">Delete</string>
    <string name="save_button">Save</string>
    <string name="back_button">Back</string>
    <string name="extracted_text_label">Extracted Text</string>
    <string name="tags_label">Tags</string>
    <string name="category_label">Category</string>
    <string name="edit_text_hint">Edit text</string>
    <string name="tags_hint">Comma separated tags</string>
    <string name="category_hint">Category</string>
    <string name="character_limit">%d/%d</string>
    <string name="no_tags_message">No tags</string>
    <string name="validation_error_message">Text and Category cannot be empty</string>
    <string name="note_not_found_message">Note not found</string>
    
    <!-- Delete Confirmation -->
    <string name="delete_confirmation_title">Delete Note?</string>
    <string name="delete_confirmation_message">This action cannot be undone.</string>
    <string name="delete_confirm_button">Delete</string>
    <string name="delete_cancel_button">Cancel</string>
    
    <!-- Settings Screen -->
    <string name="settings_title">Settings</string>
    <string name="auto_scan_title">Auto-scan Screenshots</string>
    <string name="auto_scan_description">Automatically import and process new screenshots</string>
    <string name="rerun_ocr_title">Re-run OCR on all notes</string>
    <string name="rerun_ocr_description">Process images again to extract text</string>
    <string name="backup_title">Backup & Restore</string>
    <string name="backup_description">Export database or restore from a backup</string>
    
    <!-- Search Screen -->
    <string name="search_screen_title">Advanced Search</string>
    <string name="search_keywords_hint">Search by keywords or tags...</string>
    <string name="filter_by_tag_label">Filter by Tag</string>
    <string name="search_results_label">Search Results</string>
    
    <!-- Manual Screen -->
    <string name="manual_screen_title">User Manual</string>
    <string name="manual_section_1_title">1. Scanning Screenshots</string>
    <string name="manual_section_1_content">To start, click the \'Scan Existing Screenshots\' button on the home screen. The app will ask for permission to access your photos. Once granted, it will analyze your screenshots and extract text from them.</string>
    <string name="manual_section_2_title">2. Searching</string>
    <string name="manual_section_2_content">Use the search bar at the top of the home screen to find specific screenshots. You can search for text found within the image, tags, or categories.</string>
    <string name="manual_section_3_title">3. Navigation</string>
    <string name="manual_section_3_content">Click on any screenshot card to view its details, including the full extracted text and assigned tags. Use the sidebar (drawer) to switch between the Home screen and this Manual.</string>
    <string name="manual_section_4_title">4. Dark Mode</string>
    <string name="manual_section_4_content">SnapNote supports system-wide dark mode. It will automatically adjust its theme based on your device settings.</string>
    <string name="manual_footer_message">Enjoy using SnapNote!</string>
    
    <!-- Permission Messages -->
    <string name="permission_denied_message">Permission required to scan screenshots</string>
    
    <!-- Common Labels -->
    <string name="all_categories_filter">All</string>
    <string name="menu_icon_description">Menu</string>
    <string name="search_icon_description">Search</string>
    <string name="loading_indicator_description">Loading</string>
</resources>
```

### Step 2: Update HomeScreen.kt

**File:** `app/src/main/java/com/snapnote/ui/screens/HomeScreen.kt`

Changes (find and replace):

```kotlin
// Line 107: Replace
CenterAlignedTopAppBar(
    title = { Text("SnapNote") },
    navigationIcon = {
        IconButton(onClick = { scope.launch { drawerState.open() } }) {
            Icon(Icons.Default.Menu, contentDescription = "Menu")
        }
    }
)

// With:
CenterAlignedTopAppBar(
    title = { Text(stringResource(R.string.home_title)) },
    navigationIcon = {
        IconButton(onClick = { scope.launch { drawerState.open() } }) {
            Icon(Icons.Default.Menu, contentDescription = stringResource(R.string.menu_icon_description))
        }
    }
)

// Line 120: Replace
placeholder = { Text("Search text, tags, etc.") },
leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },

// With:
placeholder = { Text(stringResource(R.string.search_placeholder)) },
leadingIcon = { Icon(Icons.Default.Search, contentDescription = stringResource(R.string.search_icon_description)) },

// Line 133: Replace
Text("Scan Existing Screenshots")

// With:
Text(stringResource(R.string.scan_button_text))

// Line 160: Replace
Text("No screenshots found. Try scanning!")

// With:
Text(stringResource(R.string.no_screenshots_message))

// Line 170: Replace
Text(
    "Recent Screenshots",
    style = MaterialTheme.typography.titleMedium,
    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
)

// With:
Text(
    stringResource(R.string.recent_screenshots_title),
    style = MaterialTheme.typography.titleMedium,
    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
)

// Line 213: Replace
Text("Error: ${state.message}")

// With:
Text(stringResource(R.string.error_loading_message, state.message ?: "Unknown error"))

// Drawer items (lines 70-85):
NavigationDrawerItem(
    icon = { Icon(Icons.Default.Home, contentDescription = null) },
    label = { Text("Home") },  // Replace with stringResource(R.string.menu_home)
    ...
)
NavigationDrawerItem(
    icon = { Icon(Icons.Default.Info, contentDescription = null) },
    label = { Text("User Manual") },  // Replace with stringResource(R.string.menu_manual)
    ...
)
NavigationDrawerItem(
    icon = { Icon(Icons.Default.Settings, contentDescription = null) },
    label = { Text("Settings") },  // Replace with stringResource(R.string.menu_settings)
    ...
)

// Line 59: Add permission message
} else {
    scope.launch {
        snackbarHostState.showSnackbar(context.getString(R.string.permission_denied_message))
    }
}
```

### Step 3: Update DetailScreen.kt

**File:** `app/src/main/java/com/snapnote/ui/screens/DetailScreen.kt`

Add import:
```kotlin
import androidx.compose.ui.res.stringResource
```

Changes:
```kotlin
// Line 42: Replace
TopAppBar(
    title = { Text(if (isEditing) "Edit Note" else "Note Detail") },

// With:
TopAppBar(
    title = { Text(if (isEditing) stringResource(R.string.edit_screen_title) else stringResource(R.string.detail_screen_title)) },

// Line 45: Replace
Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")

// With:
Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.back_button))

// Line 53: Replace
Icon(Icons.Filled.Check, contentDescription = "Save")

// With:
Icon(Icons.Filled.Check, contentDescription = stringResource(R.string.save_button))

// Line 68: Replace
Icon(Icons.Filled.Edit, contentDescription = "Edit")

// With:
Icon(Icons.Filled.Edit, contentDescription = stringResource(R.string.edit_button))

// Line 71: Replace
Icon(Icons.Filled.Delete, contentDescription = "Delete")

// With:
Icon(Icons.Filled.Delete, contentDescription = stringResource(R.string.delete_button))

// Line 79: Replace
Text("Note not found")

// With:
Text(stringResource(R.string.note_not_found_message))

// Line 89: Replace
Text("Extracted Text", style = MaterialTheme.typography.titleMedium)

// With:
Text(stringResource(R.string.extracted_text_label), style = MaterialTheme.typography.titleMedium)

// Line 93: Replace
label = { Text("Edit text") },
supportingText = { Text("${editedText.length}/5000") },

// With:
label = { Text(stringResource(R.string.edit_text_hint)) },
supportingText = { Text(stringResource(R.string.character_limit, editedText.length, 5000)) },

// Line 104: Replace
Text("Tags", style = MaterialTheme.typography.titleMedium)

// With:
Text(stringResource(R.string.tags_label), style = MaterialTheme.typography.titleMedium)

// Line 107: Replace
label = { Text("Comma separated tags") },
supportingText = { Text("${editedTags.length}/500") }

// With:
label = { Text(stringResource(R.string.tags_hint)) },
supportingText = { Text(stringResource(R.string.character_limit, editedTags.length, 500)) }

// Line 116: Replace
Text("No tags", style = MaterialTheme.typography.bodySmall)

// With:
Text(stringResource(R.string.no_tags_message), style = MaterialTheme.typography.bodySmall)

// Line 122: Replace
Text("Category", style = MaterialTheme.typography.titleMedium)

// With:
Text(stringResource(R.string.category_label), style = MaterialTheme.typography.titleMedium)

// Line 125: Replace
label = { Text("Category") },
supportingText = { Text("${editedCategory.length}/100") },

// With:
label = { Text(stringResource(R.string.category_hint)) },
supportingText = { Text(stringResource(R.string.character_limit, editedCategory.length, 100)) },

// Line 135: Replace
Text(
    "Text and Category cannot be empty",
    modifier = Modifier.padding(12.dp),
    color = MaterialTheme.colorScheme.error,
    style = MaterialTheme.typography.bodySmall
)

// With:
Text(
    stringResource(R.string.validation_error_message),
    modifier = Modifier.padding(12.dp),
    color = MaterialTheme.colorScheme.error,
    style = MaterialTheme.typography.bodySmall
)
```

### Step 4: Update SettingsScreen.kt

**File:** `app/src/main/java/com/snapnote/ui/screens/SettingsScreen.kt`

```kotlin
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(onNavigateBack: () -> Unit) {
    var autoScanEnabled by remember { mutableStateOf(true) }

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
                        onCheckedChange = { autoScanEnabled = it }
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
```

---

## Fix #2: Delete Confirmation Dialog (1-2 hours)

### Update DetailScreen.kt

**File:** `app/src/main/java/com/snapnote/ui/screens/DetailScreen.kt`

Add state variable after line 36:
```kotlin
var showDeleteConfirmation by remember { mutableStateOf(false) }
```

Replace delete button onClick (around line 71):
```kotlin
// OLD:
IconButton(onClick = {
    viewModel.deleteNote(note)
    onNavigateBack()
}) {
    Icon(Icons.Filled.Delete, contentDescription = stringResource(R.string.delete_button))
}

// NEW:
IconButton(onClick = { showDeleteConfirmation = true }) {
    Icon(Icons.Filled.Delete, contentDescription = stringResource(R.string.delete_button))
}
```

Add confirmation dialog before Scaffold closing brace (before line 40):
```kotlin
if (showDeleteConfirmation) {
    AlertDialog(
        onDismissRequest = { showDeleteConfirmation = false },
        title = { Text(stringResource(R.string.delete_confirmation_title)) },
        text = { Text(stringResource(R.string.delete_confirmation_message)) },
        confirmButton = {
            Button(
                onClick = {
                    viewModel.deleteNote(note)
                    showDeleteConfirmation = false
                    onNavigateBack()
                }
            ) { 
                Text(stringResource(R.string.delete_confirm_button)) 
            }
        },
        dismissButton = {
            Button(onClick = { showDeleteConfirmation = false }) { 
                Text(stringResource(R.string.delete_cancel_button)) 
            }
        }
    )
}
```

---

## Fix #3: Category Filtering (2-3 hours)

### Update MainViewModel.kt

**File:** `app/src/main/java/com/snapnote/presentation/MainViewModel.kt`

Add after line 41:
```kotlin
private val _selectedCategory = MutableStateFlow<String?>(null)
val selectedCategory: StateFlow<String?> = _selectedCategory.asStateFlow()

fun setSelectedCategory(category: String?) {
    _selectedCategory.value = category
}
```

Replace the uiState definition (lines 43-60) with:
```kotlin
@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
val uiState: StateFlow<UiState> = combine(
    _searchQuery.debounce(300),
    _selectedCategory
) { query, category ->
    Pair(query, category)
}
    .flatMapLatest { (query, selectedCategory) ->
        val notesFlow = if (query.isEmpty()) {
            repository.getAllNotes()
        } else {
            repository.searchNotes(query)
        }
        
        notesFlow.map { notes ->
            if (selectedCategory != null && selectedCategory != "All") {
                notes.filter { it.category == selectedCategory }
            } else {
                notes
            }
        }
    }
    .map { notes -> 
        UiState.Success(notes.map { it.toEntity() }) as UiState 
    }
    .onStart { emit(UiState.Loading) }
    .catch { e -> emit(UiState.Error(e.message ?: "Unknown error")) }
    .flowOn(Dispatchers.IO)
    .stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = UiState.Loading
    )
```

### Update HomeScreen.kt

**File:** `app/src/main/java/com/snapnote/ui/screens/HomeScreen.kt`

Add parameter to function signature:
```kotlin
@Composable
fun HomeScreen(
    onNavigateToDetail: (Int) -> Unit,
    onNavigateToManual: () -> Unit,
    onNavigateToSettings: () -> Unit,
    viewModel: MainViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()  // ADD THIS LINE
    // ... rest of code
```

Replace category chips section (around line 159):
```kotlin
// OLD:
LazyRow(
    contentPadding = PaddingValues(horizontal = 16.dp),
    horizontalArrangement = Arrangement.spacedBy(8.dp)
) {
    items(categories) { category ->
        FilterChip(
            selected = false,
            onClick = { /* Filter logic */ },
            label = { Text(category) }
        )
    }
}

// NEW:
LazyRow(
    contentPadding = PaddingValues(horizontal = 16.dp),
    horizontalArrangement = Arrangement.spacedBy(8.dp)
) {
    items(categories) { category ->
        FilterChip(
            selected = selectedCategory == category,
            onClick = { 
                viewModel.setSelectedCategory(
                    if (category == "All") null else category
                )
            },
            label = { Text(category) }
        )
    }
}
```

---

## Fix #4: Image Error Handling (2 hours)

### Update HomeScreen.kt - ScreenshotCard composable

**File:** `app/src/main/java/com/snapnote/ui/screens/HomeScreen.kt`

Replace AsyncImage call (around line 207):
```kotlin
// OLD:
AsyncImage(
    model = note.imagePath,
    contentDescription = null,
    modifier = Modifier
        .fillMaxWidth()
        .weight(1f),
    contentScale = ContentScale.Crop
)

// NEW:
Box(
    modifier = Modifier
        .fillMaxWidth()
        .weight(1f),
    contentAlignment = Alignment.Center
) {
    AsyncImage(
        model = note.imagePath,
        contentDescription = null,
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(),
        contentScale = ContentScale.Crop,
        onError = {
            Log.e("HomeScreen", "Image failed to load: ${note.imagePath}")
        }
    )
}
```

### Update DetailScreen.kt

**File:** `app/src/main/java/com/snapnote/ui/screens/DetailScreen.kt`

Add import:
```kotlin
import android.util.Log
```

Replace AsyncImage call (around line 86):
```kotlin
// OLD:
AsyncImage(
    model = note.imagePath,
    contentDescription = null,
    modifier = Modifier
        .fillMaxWidth()
        .height(400.dp),
    contentScale = ContentScale.Fit
)

// NEW:
Box(
    modifier = Modifier
        .fillMaxWidth()
        .height(400.dp),
    contentAlignment = Alignment.Center
) {
    AsyncImage(
        model = note.imagePath,
        contentDescription = null,
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(),
        contentScale = ContentScale.Fit,
        onError = {
            Log.e("DetailScreen", "Image failed to load: ${note.imagePath}")
        }
    )
}
```

---

## Implementation Checklist

- [ ] Create `strings.xml` with all 40+ string resources
- [ ] Update `HomeScreen.kt` (6 string replacements)
- [ ] Update `DetailScreen.kt` (12 string replacements + delete dialog)
- [ ] Update `SettingsScreen.kt` (3 string replacements)
- [ ] Update `SearchScreen.kt` (2 string replacements) - optional
- [ ] Update `ManualScreen.kt` (all strings) - optional
- [ ] Add category filter state to `MainViewModel.kt`
- [ ] Update category filter logic in `HomeScreen.kt`
- [ ] Add image error handling to `HomeScreen.kt` and `DetailScreen.kt`
- [ ] Test all screens and verify strings load correctly
- [ ] Build APK and verify no runtime crashes

---

**Estimated Implementation Time: 6-8 hours** (1-2 working days)

**Impact When Complete:**
- ✅ All hardcoded strings removed
- ✅ Proper localization support
- ✅ Delete confirmation prevents accidental data loss
- ✅ Category filtering actually works
- ✅ Better error feedback for image loading failures
