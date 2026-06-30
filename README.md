# SnapNote

A modern Android app that organizes your screenshots by extracting text with OCR. Search, tag, and categorize — all processed locally on your device.

<p align="center">
  <img src="https://img.shields.io/badge/Platform-Android-green?style=flat-square" />
  <img src="https://img.shields.io/badge/Min%20SDK-26-blue?style=flat-square" />
  <img src="https://img.shields.io/badge/Target%20SDK-36-blue?style=flat-square" />
  <img src="https://img.shields.io/badge/Language-Kotlin-purple?style=flat-square" />
  <img src="https://img.shields.io/badge/UI-Jetpack%20Compose-4285F4?style=flat-square" />
</p>

---

## Features

| Feature | Description |
|---------|-------------|
| **OCR Text Extraction** | Uses Google ML Kit to extract text from screenshots locally |
| **Smart Search** | Search through all screenshots by extracted text, tags, or category |
| **Auto-Tagging** | Automatically suggests tags (#finance, #programming, #food, etc.) |
| **Category Filtering** | Filter notes by auto-detected category with chip-based UI |
| **Edit & Manage** | Edit extracted text, tags, and categories; delete with confirmation |
| **Onboarding Tutorial** | Interactive 5-page walkthrough on first launch |
| **Dark Mode** | Full support for system dark/light themes |
| **Dynamic Colors** | Material You dynamic color support (Android 12+) |
| **Privacy First** | All processing happens on-device, no network required |

---

## Screenshots

The app includes:
- **Splash Screen** — Animated branded loading screen
- **Onboarding** — Swipeable tutorial explaining app features
- **Home Screen** — Grid view with search, scan button, category filters
- **Detail Screen** — Full image view with editable text, tags, and category
- **Settings** — Auto-scan toggle, OCR re-run, backup options
- **User Manual** — In-app help guide

---

## Tech Stack

| Layer | Technology |
|-------|------------|
| **UI** | Jetpack Compose + Material 3 |
| **Language** | Kotlin 2.3 |
| **Architecture** | Clean Architecture (Domain / Data / Presentation) |
| **Database** | Room (SQLite) with migrations |
| **OCR** | Google ML Kit Text Recognition |
| **Image Loading** | Coil |
| **Async** | Kotlin Coroutines & Flow |
| **Preferences** | DataStore |
| **Navigation** | Jetpack Navigation Compose |
| **Build** | Gradle with Version Catalogs |

---

## Project Structure

```
app/src/main/java/com/snapnote/
├── data/
│   ├── local/          # Room database, DAO, Entity
│   ├── repository/     # Repository implementation
│   └── settings/       # DataStore preferences
├── domain/
│   ├── models/         # Domain models
│   ├── repository/     # Repository interface
│   └── usecases/       # ExtractText, SuggestTags
├── presentation/       # MainViewModel, UiState
├── ui/
│   ├── navigation/     # NavGraph with sealed Screen routes
│   ├── screens/        # All Composable screens
│   └── theme/          # Colors, Typography, Theme
├── util/               # Constants, Mapper
├── utils/              # ScreenshotScanner
└── MainActivity.kt
```

---

## Getting Started

### Prerequisites

- **Android Studio** Ladybug (2024.2) or newer
- **JDK 17**
- **Android SDK 36** (compile & target)
- **Min SDK 26** (Android 8.0)

### Build & Run

```bash
# Clone the repo
git clone https://github.com/yourusername/SnapNote.git
cd SnapNote

# Open in Android Studio, sync Gradle, then run
# Or build from command line:
./gradlew assembleDebug
```

### Permissions

The app requests:
- `READ_MEDIA_IMAGES` (Android 13+) — to access screenshots
- `READ_EXTERNAL_STORAGE` (Android 12 and below) — fallback for older devices

---

## Architecture

The app follows **Clean Architecture** with clear separation:

```
UI (Compose) → ViewModel → Use Cases → Repository → Room Database
```

- **Presentation**: Compose screens observe `StateFlow` from the ViewModel
- **Domain**: Use cases contain business logic (OCR, tag suggestion)
- **Data**: Room handles persistence, repository abstracts the data source

### Key Design Decisions

- **No DI framework** — Simple manual injection via `AndroidViewModel` for minimal complexity
- **Flow-based reactivity** — Database changes propagate automatically to the UI
- **Semaphore for OCR** — Limits concurrent processing to 4 to prevent OOM
- **Debounced search** — 300ms debounce on search queries for performance

---

## User Guide

### First Launch
1. The app shows an onboarding tutorial explaining each feature
2. Tap "Get Started" or "Skip" to reach the Home screen

### Scanning Screenshots
1. Tap **"Scan Existing Screenshots"** on the Home screen
2. Grant photo access permission when prompted
3. The app scans your Screenshots folder and extracts text from each image
4. A progress bar shows scanning status

### Searching & Filtering
- Use the **search bar** to find screenshots by text content
- Tap **category chips** to filter by auto-detected categories
- Tap "All" to reset the filter

### Viewing & Editing
- Tap any screenshot card to open the **Detail Screen**
- Tap the **edit icon** to modify extracted text, tags, or category
- Tap the **delete icon** to remove (with confirmation)

### Navigation
- Use the **hamburger menu** (☰) to access the drawer
- Navigate to **User Manual** or **Settings** from the drawer

---

## Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

---

## License

This project is licensed under the MIT License. See [LICENSE](LICENSE) for details.
