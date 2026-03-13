package com.example.recommender.data.remote.waste.dto



import com.google.gson.annotations.SerializedName

// Maps exactly to your Lambda response JSON
data class RecommendationResponse(
    val recommendations: List<RecommendationDto>
)

data class RecommendationDto(
    val title: String,
    val author: String?,           // books
    val director: String?,         // movies
    @SerializedName("summary_snippet")
    val summarySnippet: String?,
    val score: Int?,
    val genres: List<String>?,
    val posterPath: String?
)