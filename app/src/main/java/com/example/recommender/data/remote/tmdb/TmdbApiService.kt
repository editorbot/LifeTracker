package com.example.recommender.data.remote.tmdb


import com.example.recommender.data.remote.tmdb.dto.TmdbSearchResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface TmdbApiService {

    @GET("search/movie")
    suspend fun searchMovies(
        @Query("api_key") apiKey: String,
        @Query("query") query: String
    ): TmdbSearchResponse
}