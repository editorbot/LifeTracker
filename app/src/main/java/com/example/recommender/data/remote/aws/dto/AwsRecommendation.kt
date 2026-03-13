package com.example.recommender.data.remote.aws.dto


import com.google.gson.annotations.SerializedName

data class AwsRecommendationResponse(
    val recommendations: List<AwsRecommendation>
)

data class AwsRecommendation(
    val title: String,
    val author: String?,
    val director: String?,
    @SerializedName("summary_snippet")
    val summarySnippet: String?,
    val score: Int?,
    val genres: List<String>?,
    val posterPath: String?
) {
    // Convenience — same pattern as your Flutter model
    val authorOrDirector: String?
        get() = author ?: director
}