package com.snapnote.util

object Constants {
    // Screenshot processing
    const val MAX_SCREENSHOTS_TO_SCAN = 50
    const val MAX_CONCURRENT_PROCESSING = 4
    
    // Text limits
    const val MAX_TEXT_LENGTH = 5000
    const val MAX_TAGS_LENGTH = 500
    const val MAX_CATEGORY_LENGTH = 100
    
    // Database
    const val DATABASE_VERSION = 2
    const val DATABASE_NAME = "snapnote-db"
    
    // Debounce
    const val SEARCH_DEBOUNCE_MS = 300L
    const val VIEWMODEL_TIMEOUT_MS = 5000L
}
