package com.example.recommender.data.remote.aws

import com.example.recommender.data.remote.aws.dto.AwsRecommendRequest
import com.example.recommender.data.remote.aws.dto.AwsRecommendationResponse
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface AwsApiService {

    @POST("recommend")
    suspend fun getBookRecommendations(
        @Header("Authorization") token: String,
        @Body body: AwsRecommendRequest
    ): AwsRecommendationResponse

    @POST("recommend_movie")
    suspend fun getMovieRecommendations(
        @Header("Authorization") token: String,
        @Body body: AwsRecommendRequest
    ): AwsRecommendationResponse
}