package com.example.recommender.data.remote.aws.dto



data class AwsRecommendRequest(
    val preferences: String   // space-separated genres e.g. "Drama Thriller Crime"
)