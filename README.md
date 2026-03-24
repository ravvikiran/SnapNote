# SnapNote

SnapNote is an Android application that helps you organize and search through your screenshots by automatically extracting text using OCR (Optical Character Recognition).

## Features

- **Automatic Text Extraction:** Uses Google's ML Kit to recognize text in your screenshots.
- **Advanced Search:** Find specific screenshots by searching for text contained within them.
- **Organization:** Automatically suggests tags and categories for your screenshots.
- **Edge-to-Edge Design:** A modern, immersive UI built with Jetpack Compose.
- **Dynamic Theming:** Supports Material 3 dynamic colors and automatic dark mode.
- **Privacy Focused:** All OCR processing happens locally on your device.

## Tech Stack

- **UI:** Jetpack Compose with Material 3
- **Language:** Kotlin
- **Local Database:** Room
- **OCR:** ML Kit Text Recognition (Latin)
- **Image Loading:** Coil
- **Concurrency:** Kotlin Coroutines & Flow
- **Dependency Management:** Version Catalogs (libs.versions.toml)

## Getting Started

### Prerequisites

- Android Studio Koala or newer
- Android SDK 35 (Target)
- Minimum Android 8.0 (API level 26)

### Installation

1. Clone the repository.
2. Open the project in Android Studio.
3. Sync the project with Gradle files.
4. Run the app on an emulator or physical device.

---

# User Manual

Welcome to SnapNote! This guide will help you make the most of the application.

## 1. Initial Setup

When you first open SnapNote, you'll see a mostly empty Home screen. To begin organizing your library, follow these steps:

1.  Tap the **"Scan Existing Screenshots"** button on the Home screen.
2.  Grant the app permission to access your **Media/Photos**.
3.  SnapNote will begin scanning your device's "Screenshots" folder. It will analyze each image, extract text, and save it to your local database.

## 2. Browsing and Searching

-   **Home Screen:** View all your processed screenshots in a grid layout.
-   **Search Bar:** Type any word or phrase at the top of the Home screen. SnapNote will instantly filter your screenshots to show only those that contain your search term.
-   **Categories:** Use the horizontal chip bar below the search field to filter screenshots by their auto-detected categories (e.g., Receipts, Recipes, etc.).

## 3. Viewing Details

Tap on any screenshot in the grid to open the **Detail Screen**. Here you can:
-   View the full-sized image.
-   Read the complete text extracted from the image.
-   See the auto-generated tags and category.

## 4. Navigation

-   **Drawer Menu:** Tap the menu icon (three lines) in the top-left corner to open the sidebar.
-   **User Manual:** You can access a simplified version of this guide directly within the app by selecting "User Manual" from the drawer.

## 5. Settings

In the future, the Settings screen (accessible via the sidebar) will allow you to:
-   Toggle automatic background scanning.
-   Manually re-run the OCR process for your entire library.
-   Export or backup your local database.

---

## License

This project is licensed under the MIT License - see the LICENSE file for details.
