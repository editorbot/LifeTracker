package com.example.recommender.data.remote.waste



import com.example.recommender.data.remote.waste.dto.RecommendationResponse
import retrofit2.http.Body
import retrofit2.http.POST

data class RecommendRequest(val preferences: String)

interface ApiService {

    @POST("recommend")
    suspend fun getBookRecommendations(
        @Body body: RecommendRequest
    ): RecommendationResponse

    @POST("recommend_movie")
    suspend fun getMovieRecommendations(
        @Body body: RecommendRequest
    ): RecommendationResponse
}