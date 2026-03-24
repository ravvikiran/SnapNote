package com.snapnote.domain.usecases

class SuggestTagsUseCase {
    fun execute(text: String): List<String> {
        val keywords = listOf("receipt", "bill", "recipe", "code", "bank", "address", "phone")
        return keywords.filter { text.contains(it, ignoreCase = true) }
    }
}
