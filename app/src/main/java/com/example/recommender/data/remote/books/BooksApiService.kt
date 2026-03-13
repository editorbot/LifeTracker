package com.example.recommender.data.remote.books



import com.example.recommender.data.remote.books.dto.BooksSearchResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface BooksApiService {

    @GET("volumes")
    suspend fun searchBooks(
        @Query("q") query: String,
        @Query("maxResults") maxResults: Int = 10
    ): BooksSearchResponse
}