package com.snapnote.domain.usecases

class SuggestTagsUseCase {
    fun execute(text: String): List<String> {
        val lowerText = text.lowercase()
        val tags = mutableSetOf<String>()

        val tagMap = mapOf(
            "#finance" to listOf("receipt", "bill", "bank", "pay", "invoice", "upi", "amount", "transaction"),
            "#programming" to listOf("code", "class", "fun", "def", "function", "api", "git", "java", "kotlin", "python", "script"),
            "#food" to listOf("recipe", "ingredients", "cook", "menu", "restaurant", "bake"),
            "#travel" to listOf("flight", "ticket", "hotel", "booking", "itinerary", "boarding"),
            "#shopping" to listOf("order", "delivery", "cart", "amazon", "flipkart", "shipping"),
            "#contact" to listOf("address", "phone", "email", "contact")
        )

        for ((tag, keywords) in tagMap) {
            if (keywords.any { lowerText.contains(it) }) {
                tags.add(tag)
            }
        }

        return tags.toList()
    }
}
