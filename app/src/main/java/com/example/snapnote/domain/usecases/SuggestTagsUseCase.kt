package com.example.snapnote.domain.usecases

class SuggestTagsUseCase {

    private val categoryMap = mapOf(
        "Programming" to listOf("java", "kotlin", "api", "git", "code", "bug", "compiler"),
        "Finance" to listOf("upi", "payment", "bank", "receipt", "transfer", "invoice"),
        "Recipes" to listOf("recipe", "ingredients", "cook", "bake", "cup", "tbsp"),
        "Social Media" to listOf("tweet", "retweet", "post", "like", "profile"),
        "Shopping" to listOf("order", "cart", "shipping", "delivery", "amazon")
    )

    operator fun invoke(extractedText: String): Pair<List<String>, String> {
        val lowerText = extractedText.lowercase()
        val suggestedTags = mutableSetOf<String>()
        val categoryCounts = mutableMapOf<String, Int>()

        for ((category, keywords) in categoryMap) {
            for (keyword in keywords) {
                if (lowerText.contains(keyword)) {
                    suggestedTags.add("#$keyword")
                    categoryCounts[category] = categoryCounts.getOrDefault(category, 0) + 1
                }
            }
        }

        // Determine category by highest keyword frequency, default to "Other"
        val suggestedCategory = categoryCounts.maxByOrNull { it.value }?.key ?: "Other"

        return Pair(suggestedTags.toList(), suggestedCategory)
    }
}
